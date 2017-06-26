package ru.nsu.ccfit.saltanova.messages;

import ru.nsu.ccfit.saltanova.Client;
import ru.nsu.ccfit.saltanova.Config;
import ru.nsu.ccfit.saltanova.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerHandler implements IServerHandler {

    private LinkedBlockingQueue<Message> messagesQueue;
    private ArrayBlockingQueue<ServerTextMessage> history = Server.getHistory();

    private int sessionID = -1;

    public ServerHandler(LinkedBlockingQueue<Message> messagesQueue) {
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void process(AddUserMessage message) {
        try {
            if (message.getLogin().trim().equals("")) {
                LoginError errorMessage = new LoginError("Please\n");
                messagesQueue.add(errorMessage);
                return;
            }

            for (Client client : Server.getClientsList()) {
                if (message.getLogin().equals(client.getLogin())) {
                    LoginError errorMessage = new LoginError("Username is not available\n");
                    messagesQueue.add(errorMessage);
                    return;
                }
            }

            sessionID = Server.createID();
            messagesQueue.add(new LoginSuccess(sessionID));
            Server.addUser(message.getLogin(), sessionID, message.getType(), messagesQueue);

            for (ServerTextMessage m : history) {
                ServerTextMessage historyMessage = history.take();
                messagesQueue.add(historyMessage);
                history.add(historyMessage);
            }

            for (Client client : Server.getClientsList()) {
                if (!client.getLogin().equals(message.getLogin())) {
                    client.getMessagesQueue().add(new UserLoginMessage(message.getLogin()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(UserTextMessage message) {
        if (!checkID(message.getSessionID()) || message.getSessionID() != sessionID) {
            messagesQueue.add(new TextMessageError("(!) cannot send your message (!) : " + message.getMessage()));
            return;
        }

        messagesQueue.add(new TextMessageSuccess());
        ServerTextMessage textMessage = new ServerTextMessage(Server.findUser(message.getSessionID()), message.getMessage());

        for (Client client : Server.getClientsList()) {
            client.getMessagesQueue().add(textMessage);
        }

        if (history.remainingCapacity() == 0 ) {
            try {
                history.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        history.add(textMessage);
    }

    @Override
    public void process(RequestList message) {

            if (!checkID(message.getSessionID()) && sessionID == message.getSessionID()) {
                messagesQueue.add(new RequestListError("cannot get users list"));
                return;
            }
            RequestListSuccess requestList = new RequestListSuccess(Server.getUsers(), Server.getTypes());
            messagesQueue.add(requestList);
    }

    @Override
    public void process(Logout message) {
        String login = Server.findUser(message.getSessionID());
        if (!checkID(message.getSessionID())) {
            messagesQueue.add(new LogoutError("you are not in users iist"));
            return;
        }
        if (sessionID != message.getSessionID()) {
            messagesQueue.add(new LogoutError("your id is wrong"));
            return;
        }
        messagesQueue.add(new LogoutSuccess());
        Server.deleteUser(message.getSessionID());
        for (Client client : Server.getClientsList()) {
                client.getMessagesQueue().add(new UserLogoutMessage(login));
        }
    }

    public void deleteUser() {
        if (sessionID != -1) {
            Server.deleteUser(sessionID);
        }
    }

    private boolean checkID(int id) {
        boolean check = false;
        for (Client client : Server.getClientsList()) {
            if (client.getSessionID() == id) {
                check = true;
            }
        }
        return check;
    }
}
