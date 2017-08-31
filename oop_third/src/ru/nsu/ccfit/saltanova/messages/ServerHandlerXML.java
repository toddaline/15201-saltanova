package ru.nsu.ccfit.saltanova.messages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerHandlerXML implements IServerHandler {

    private LinkedBlockingQueue<Document> docMessagesQueue;

    public ServerHandlerXML(LinkedBlockingQueue<Document> docMessagesQueue) {
        this.docMessagesQueue = docMessagesQueue;
    }

    @Override
    public void process(AddUserMessage message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("command");
            root.setAttribute("name","login");
            document.appendChild(root);
            Element name = document.createElement("name");
            name.setTextContent(message.getLogin());
            root.appendChild(name);
            Element type = document.createElement("type");
            type.setTextContent(message.getType());
            root.appendChild(type);
            docMessagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(UserTextMessage message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("command");
            root.setAttribute("name","message");
            document.appendChild(root);
            Element name = document.createElement("message");
            name.setTextContent(message.getMessage());
            root.appendChild(name);
            Element type = document.createElement("session");
            type.setTextContent(Integer.toString(message.getSessionID()));
            root.appendChild(type);
            docMessagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(RequestList message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("command");
            root.setAttribute("name","list");
            document.appendChild(root);
            Element session = document.createElement("session");
            session.setTextContent(Integer.toString(message.getSessionID()));
            root.appendChild(session);
            docMessagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(Logout message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("command");
            root.setAttribute("name","logout");
            document.appendChild(root);
            Element session = document.createElement("session");
            session.setTextContent(Integer.toString(message.getSessionID()));
            root.appendChild(session);
            docMessagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
