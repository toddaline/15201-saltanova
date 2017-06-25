package ru.nsu.ccfit.saltanova;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.nsu.ccfit.saltanova.messages.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ClientXMLThread extends AllClientThreads {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private LinkedBlockingQueue<Message> messagesQueue;
    private LinkedBlockingQueue<Document> docMessagesQueue;
    private ServerHandler serverHandler;
    private ClientHandlerXML clientHandlerXML;
    private Thread reader;
    private Thread writer;

    private static final Logger log = LogManager.getLogger("log");

    ClientXMLThread(Socket socket){
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        messagesQueue = new LinkedBlockingQueue<>();
        docMessagesQueue = new LinkedBlockingQueue<>();
        serverHandler = new ServerHandler(messagesQueue);
        clientHandlerXML = new ClientHandlerXML(docMessagesQueue);

        reader = new Thread(new ClientReader());
        writer = new Thread(new ClientWriter());
        reader.start();
        writer.start();
    }

    public class ClientReader implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    int length = inputStream.readInt();  // + обработка если длина отрицательная?
                    byte[] buffer = new byte[length];
                    int read = 0;
                    while (read != length) {
                        int temp = inputStream.read(buffer, read, length - read);
                        read += temp;
                    }
                // проверка на то что длина сообщения действительно такая?

                    if (buffer == null) {   //чет подумать надо че с этим делать
                        break;
                    }
                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = documentBuilder.parse(new InputSource(new InputStreamReader(new ByteArrayInputStream(buffer, 0, length), "UTF-8")));
                    Node root = document.getDocumentElement();
                    ClientMessage message = null;
                    switch (root.getAttributes().getNamedItem("name").getNodeValue()) {
                        case "login" :
                            message = new AddUserMessage(document.getElementsByTagName("name").item(0).getTextContent(), document.getElementsByTagName("type").item(0).getTextContent());
                            break;
                            // +type - xml
                        case "list" :
                            message = new RequestList(Integer.parseInt(document.getElementsByTagName("session").item(0).getTextContent()));
                            break;
                        case "message" :
                            message = new UserTextMessage(document.getElementsByTagName("message").item(0).getTextContent(),
                                    Integer.parseInt(document.getElementsByTagName("session").item(0).getTextContent()));
                            break;
                        case "logout" :
                            message = new Logout(Integer.parseInt(document.getElementsByTagName("session").item(0).getTextContent()));
                    }
                    log.info("received message from client");

                    message.process(serverHandler);
                }
            } catch (IOException e) {
                writer.interrupt();
                log.info("disconnected");
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
            finally {
                log.info("reader closed");
            }
        }
    }

    public class ClientWriter implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    ServerMessage message = (ServerMessage)messagesQueue.take();
                    message.process(clientHandlerXML);
                    sendMessage();
                    log.info("sending message to client");
                }
            } catch (InterruptedException e) {
                log.info("thread was interrupted");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                log.info("writer closed");
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
            outputStream.writeInt(byteArrayOutputStream.size());
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.flush();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        reader.interrupt();
    }

    @Override
    void banUser() {

    }
}
