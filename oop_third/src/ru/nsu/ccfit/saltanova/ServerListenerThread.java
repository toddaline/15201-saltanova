package ru.nsu.ccfit.saltanova;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.nsu.ccfit.saltanova.messages.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListenerThread {

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ClientHandler clientHandler;
    private ServerHandlerXML serverHandlerXML;
    private LinkedBlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> responseMessages = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<UserTextMessage> textMessages = new LinkedBlockingQueue<>();
    private Thread reader;
    private Thread writer;
    private static String userName = "";
    private static Socket socket;
    private LinkedBlockingQueue<Document> docMessagesQueue;

    private static final Logger log = LogManager.getLogger("log");

    static {
        System.getProperties().setProperty("log4j.configurationFile", "src/log4j2.xml");
    }

    public static void main(String[] args) {
        Logger logger = LogManager.getRootLogger();
        if (!Config.LOGGING){
            Configurator.setLevel(logger.getName(), Level.OFF);
        }
        new GUIwelcome();
    }

    void connect(String ip, int serverPort, String version) {
        try {
            log.info("connected to port: " + serverPort);
            socket = new Socket(ip, serverPort);
            socket.setKeepAlive(true);
            clientHandler = new ClientHandler(this, messagesQueue, textMessages);
            switch (version) {
                case "obj":
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    writer = new Thread(new ServerWriter());
                    reader = new Thread(new ServerReader());
                    writer.start();
                    reader.start();
                    return;
                case "xml":
                    docMessagesQueue = new LinkedBlockingQueue<>();
                    serverHandlerXML = new ServerHandlerXML(docMessagesQueue);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.flush();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    writer = new Thread(new XMLServerWriter());
                    reader = new Thread(new XMLServerReader());
                    writer.start();
                    reader.start();
            }
        } catch (IOException e) {
            log.info("cannot connect to server");
        }
    }

    private class ServerReader implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    ServerMessage message = (ServerMessage) objectInputStream.readObject();
                    message.process(clientHandler);
                    log.info("received message to server: " + message.getClass().getName());
                }
            } catch (IOException | ClassNotFoundException e) {
                log.info("socket was closed");
            } finally {
                log.info("reader thread closed");
            }
        }
    }

    private class ServerWriter implements Runnable {
        @Override
        public void run() {
            try {
                clientHandler.setType("obj");
                userName = JOptionPane.showInputDialog("Enter your username");
                messagesQueue.add(new AddUserMessage(userName, "obj"));
                while (true) {
                    ClientMessage message = (ClientMessage) messagesQueue.take();
                    objectOutputStream.writeObject(message);
                    log.info("sending message to server: " + message.getClass().getName());
                    objectOutputStream.flush();
                    if (message instanceof UserTextMessage) {
                        textMessages.add((UserTextMessage)message);
                    }
                }
            } catch (InterruptedException e) {
                log.info("writer thread interrupted");
            } catch (IOException e) {
            } finally {
                log.info("writer thread closed");
            }
        }
    }

    private class XMLServerReader implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    int length = dataInputStream.readInt();
                    byte[] buffer = new byte[length];
                    int read = 0;
                    while (read != length) {
                        int temp = dataInputStream.read(buffer, read, length - read);
                        read += temp;
                    }

                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = documentBuilder.parse(new InputSource(new InputStreamReader(new ByteArrayInputStream(buffer, 0, length), "UTF-8")));

                    Node root = document.getDocumentElement();
                    ServerMessage message = null;
                    switch (root.getNodeName()) {
                        case "event":
                        if (root.getAttributes().getNamedItem("name").getNodeValue().equals("message")) {
                            message = new ServerTextMessage(document.getElementsByTagName("name").item(0).getTextContent(), document.getElementsByTagName("message").item(0).getTextContent());
                        } else if (root.getAttributes().getNamedItem("name").getNodeValue().equals("userlogin")) {
                            message = new UserLoginMessage(document.getElementsByTagName("name").item(0).getTextContent(), document.getElementsByTagName("type").item(0).getTextContent());
                        } else if (root.getAttributes().getNamedItem("name").getNodeValue().equals("userlogout")) {
                            message = new UserLogoutMessage(document.getElementsByTagName("name").item(0).getTextContent());
                        }
                        break;
                        case "error":
                        ClientMessage responseError = (ClientMessage) responseMessages.take();
                        if (responseError instanceof AddUserMessage) {
                            message = new LoginError(document.getElementsByTagName("message").item(0).getTextContent());
                        } else if (responseError instanceof RequestList) {
                            message = new RequestListError(document.getElementsByTagName("message").item(0).getTextContent());
                        } else if (responseError instanceof UserTextMessage) {
                            message = new TextMessageError(document.getElementsByTagName("message").item(0).getTextContent());
                        } else if (responseError instanceof Logout) {
                            message = new LogoutError(document.getElementsByTagName("message").item(0).getTextContent());
                        }
                        break;
                        case "success":
                        ClientMessage responseSuccess = (ClientMessage) responseMessages.take();
                        if (responseSuccess instanceof AddUserMessage || responseSuccess instanceof RequestList) {
                            NodeList list = document.getElementsByTagName("user");
                            if (list.getLength() == 0) {
                                message = new LoginSuccess(Integer.parseInt(document.getElementsByTagName("session").item(0).getTextContent()));
                            } else {
                                int listLength = document.getElementsByTagName("user").getLength();
                                String[] users = new String[listLength];
                                String[] types = new String[listLength];
                                for (int i = 0; i < listLength; i++) {
                                    users[i] = ((Element)(list.item(i).getChildNodes())).getElementsByTagName("name").item(0).getTextContent();
                                    types[i] = ((Element)(list.item(i).getChildNodes())).getElementsByTagName("type").item(0).getTextContent();
                                }
                                message = new RequestListSuccess(users, types);
                            }
                        } else if (responseSuccess instanceof UserTextMessage) {
                            message = new TextMessageSuccess();
                        } else if (responseSuccess instanceof Logout) {
                            message = new LogoutSuccess();
                        }
                    }
                    log.info("received message from server: " + message.getClass().getName());
                    message.process(clientHandler);
                }
            } catch (UnsupportedEncodingException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                log.info("reader thread interrupted");
            } catch (IOException e) {
                log.info("socket closed");
            } finally {
                log.info("reader thread closed");
            }
        }
    }

    private class XMLServerWriter implements Runnable {

        @Override
        public void run() {
            try {
                clientHandler.setType("xml");
                userName = JOptionPane.showInputDialog("Enter your username");
                messagesQueue.add(new AddUserMessage(userName, "xml"));
                while (true) {
                    ClientMessage message = (ClientMessage) messagesQueue.take();
                    responseMessages.add(message);
                    message.process(serverHandlerXML);
                    sendMessage();
                    if (message instanceof UserTextMessage) {
                        textMessages.add((UserTextMessage)message);
                    }
                    log.info("sending message to server: " + message.getClass().getName());
                }
            } catch (InterruptedException e) {
                log.info("writer thread interrupted");
            } catch (IOException e) {
            } finally {
                log.info("writer thread closed");
            }
        }
    }

    private void sendMessage() throws InterruptedException, IOException {
        try {
            Document document = docMessagesQueue.take();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
            dataOutputStream.writeInt(byteArrayOutputStream.size());
            dataOutputStream.write(byteArrayOutputStream.toByteArray());
            dataOutputStream.flush();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return userName;
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.interrupt();
        writer.interrupt();
    }
}
