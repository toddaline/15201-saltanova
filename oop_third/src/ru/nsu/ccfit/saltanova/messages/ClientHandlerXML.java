package ru.nsu.ccfit.saltanova.messages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandlerXML implements ObserverMessage {

    private LinkedBlockingQueue<Document> messagesQueue;

    public ClientHandlerXML(LinkedBlockingQueue<Document> messagesQueue) {
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void process(ServerTextMessage message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("event");
            root.setAttribute("name", "message");
            document.appendChild(root);
            Element m = document.createElement("message");
            m.setTextContent(message.getMessage());
            root.appendChild(m);
            Element from = document.createElement("login");
            from.setTextContent(message.getLogin());
            root.appendChild(from);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(RequestListSuccess message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("success");
            document.appendChild(root);
            Element listusers = document.createElement("listusers");
            root.appendChild(listusers);
            for (String s : message.getUsersList()) {
                Element user = document.createElement("user");
                listusers.appendChild(user);
                Element name = document.createElement("name");
                name.setTextContent(s);
                user.appendChild(name);
                Element type = document.createElement("type");
                type.setTextContent("xml");                                //redo!
                user.appendChild(type);
            }
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(RequestListError message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("error");
            document.appendChild(root);
            Element child = document.createElement("message");
            child.setTextContent(message.getReason());
            root.appendChild(child);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(LoginSuccess message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("success");
            document.appendChild(root);
            Element child = document.createElement("session");
            child.setTextContent(Integer.toString(message.getSessionID()));
            root.appendChild(child);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(LoginError message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("error");
            document.appendChild(root);
            Element child = document.createElement("message");
            child.setTextContent(message.getReason());
            root.appendChild(child);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(TextMessageError message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("error");
            document.appendChild(root);
            Element child = document.createElement("message");
            child.setTextContent(message.getReason());
            root.appendChild(child);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(TextMessageSuccess message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("success");
            document.appendChild(root);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(UserLoginMessage message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("event");
            root.setAttribute("name","userlogin");
            document.appendChild(root);
            Element name = document.createElement("name");
            name.setTextContent(message.getLogin());
            root.appendChild(name);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(UserLogoutMessage message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("event");
            root.setAttribute("name","userlogout");
            document.appendChild(root);
            Element name = document.createElement("name");
            name.setTextContent(message.getLogin());
            root.appendChild(name);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(LogoutSuccess message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("success");
            document.appendChild(root);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(LogoutError message) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("error");
            document.appendChild(root);
            Element child = document.createElement("message");
            child.setTextContent(message.getReason());
            root.appendChild(child);
            messagesQueue.add(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
