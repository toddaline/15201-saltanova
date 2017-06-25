package ru.nsu.ccfit.saltanova;

import ru.nsu.ccfit.saltanova.messages.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private String login;
    private int sessionID;
    private String type;
    private LinkedBlockingQueue<Message> messagesQueue;

    Client(String login, int sessionID, String type, LinkedBlockingQueue<Message> messagesQueue) {
        this.login = login;
        this.sessionID = sessionID;
        this.type = type;
        this.messagesQueue = messagesQueue;
    }

    public String getLogin() {
        return login;
    }

    public int getSessionID() {
        return sessionID;
    }

    public String getType() {
        return type;
    }

    public LinkedBlockingQueue<Message> getMessagesQueue() {
        return messagesQueue;
    }
}
