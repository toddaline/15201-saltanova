package ru.nsu.ccfit.saltanova.messages;

import ru.nsu.ccfit.saltanova.GUIform;
import ru.nsu.ccfit.saltanova.ServerListenerThread;

import javax.swing.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements ObserverMessage {

    private int sessionID = -1;
    private LinkedBlockingQueue<Message> messagesQueue;
    private Observer observer;
    private String type;
    ServerListenerThread client;

    public ClientHandler(ServerListenerThread client, LinkedBlockingQueue messagesQueue) {
        this.client = client;
        this.messagesQueue = messagesQueue;
    }

    public void process(LoginSuccess message) {
            this.sessionID = message.getSessionID();
            messagesQueue.add(new RequestList(sessionID));
            new GUIform(this);
            notifyObserver(message);
    }

    public void process(LoginError message) {
        String reason = message.getReason();
        String userName = JOptionPane.showInputDialog(reason + "Enter your username");
        messagesQueue.add(new AddUserMessage(userName, type));
    }

    @Override
    public void process(TextMessageError message) {
        notifyObserver(message);
    }

    @Override
    public void process(TextMessageSuccess message) {}

    @Override
    public void process(ServerTextMessage message) {
        notifyObserver(message);
    }

    public void process(RequestListSuccess message) {
        notifyObserver(message);
    }

    public void process(RequestListError message) {
        notifyObserver(message);
    }

    @Override
    public void process(UserLoginMessage message) {
        notifyObserver(message);
    }

    @Override
    public void process(UserLogoutMessage message) {
        notifyObserver(message);
    }

    @Override
    public void process(LogoutSuccess message) {
        client.stop();
        notifyObserver(message);
    }

    @Override
    public void process(LogoutError message) {
        notifyObserver(message);
    }


    public void addObserver(Observer observer) {
        this.observer = observer;
    }

    private void notifyObserver(ServerMessage message) {
        observer.update(message);
    }

    public interface Observer {
        void update(ServerMessage message);
    }

    public int getSessionID() {
        return sessionID;
    }

    public void addNewMessage(Message message) {
        messagesQueue.add(message);
    }

    public void setType(String type) {
        this.type = type;
    }
}
