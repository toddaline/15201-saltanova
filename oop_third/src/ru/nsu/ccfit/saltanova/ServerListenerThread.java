package ru.nsu.ccfit.saltanova;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
    private Thread reader;
    private Thread writer;
    private static String userName = "";
    private static Socket socket;
    private LinkedBlockingQueue<Document> docMessagesQueue;

    private static final Logger log = LogManager.getLogger("log");

    static {
        System.getProperties().setProperty("log4j.configurationFile", "./log4j2.xml");
    }

    public static void main(String[] args) {
        new GUIwelcome();
    }

    void connect(String ip, int serverPort, String version) {
        try {
            System.out.println(serverPort);
            socket = new Socket(ip, serverPort);
            socket.setKeepAlive(true);
            clientHandler = new ClientHandler(this, messagesQueue);
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
                }
            } catch (IOException | ClassNotFoundException e) {
                writer.interrupt();
                log.info("reader closed");
                log.info("socket was closed");
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
                    objectOutputStream.writeObject(messagesQueue.take());
                    objectOutputStream.flush();
                }
            } catch (InterruptedException e) {
                log.info("writer thread interrupted");
            } catch (IOException e) {
            }
        }
    }

    private class XMLServerReader implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    int length = dataInputStream.readInt();  // + обработка если длина отрицательная?
                    if (length < 0) {
                        System.out.println("negative length: " + length);
                    }
                    byte[] buffer = new byte[length];
                    int read = 0;
                    while (read != length) {
                        int temp = dataInputStream.read(buffer, read, length - read);
                        read += temp;
                    }

                    // проверка на то что длина сообщения действительно такая?

                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = documentBuilder.parse(new InputSource(new InputStreamReader(new ByteArrayInputStream(buffer, 0, length), "UTF-8")));

                    Node root = document.getDocumentElement();
                    ServerMessage message = null;
                    switch (root.getNodeName()) {
                        case "event":
                        if (root.getAttributes().getNamedItem("name").getNodeValue().equals("message")) {
                            message = new ServerTextMessage(document.getElementsByTagName("login").item(0).getTextContent(), document.getElementsByTagName("message").item(0).getTextContent());
                        } else if (root.getAttributes().getNamedItem("name").getNodeValue().equals("userlogin")) {
                            message = new UserLoginMessage(document.getElementsByTagName("name").item(0).getTextContent());
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


                        System.out.println(toXML(document));


                        if (responseSuccess instanceof AddUserMessage || responseSuccess instanceof RequestList) {
                            if (root.getChildNodes().item(0).getNodeName().equals("session")) {
                                message = new LoginSuccess(Integer.parseInt(document.getElementsByTagName("session").item(0).getTextContent()));
                            } else if (root.getChildNodes().item(0).getNodeName().equals("listusers")) {
                                int listLength = root.getChildNodes().item(0).getChildNodes().getLength();
                                String[] users = new String[listLength];
                                String[] types = new String[listLength];
                                for (int i = 0; i < listLength; i++) {
                                    Node node = root.getChildNodes().item(0).getChildNodes().item(i);
                                    users[i] = node.getChildNodes().item(0).getTextContent();
                                    types[i] = node.getChildNodes().item(1).getTextContent();
                                }
                                message = new RequestListSuccess(users, types);
                            }
                        } else if (responseSuccess instanceof UserTextMessage) {
                            message = new TextMessageSuccess();
                        } else if (responseSuccess instanceof Logout) {
                            message = new LogoutSuccess();
                        }
                    }
                    message.process(clientHandler);
                }
            } catch (UnsupportedEncodingException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
                }
            } catch (InterruptedException e) {
                log.info("writer thread interrupted");
            } catch (IOException e) {
                log.info("socket closed");
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

    public void stop() {
        writer.interrupt();
        try {
            socket.close();
            log.info("socket was closed");
        } catch (IOException e) {
            log.info("socket closing error");
            e.printStackTrace();
        }
    }

    public static String toXML(Document document) {
        try {
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (Exception e) {
        }
        return null;
    }
}
