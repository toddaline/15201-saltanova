package ru.nsu.ccfit.saltanova;

import ru.nsu.ccfit.saltanova.messages.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

class UsersList  {

    private Map<Integer, Client> onlineUsers = new HashMap<>();

    void addUser(String login, int sessionID, String type, LinkedBlockingQueue<Message> messagesQueue) throws IOException {
        if (!this.onlineUsers.containsKey(sessionID)) {
            this.onlineUsers.put(sessionID, new Client(login, sessionID, type, messagesQueue));
        }
    }

    void deleteUser(int id) {
        this.onlineUsers.remove(id);
    }

    String[] getUsers() {

        String[] users = new String[onlineUsers.size()];
        int i = 0;
        for (Map.Entry<Integer, Client> m : this.onlineUsers.entrySet()) {
            users[i] = m.getValue().getLogin();
            i++;
        }
        return users;
    }

    String[] getTypes() {

        String[] types = new String[onlineUsers.size()];
        int i = 0;
        for (Map.Entry<Integer, Client> m : this.onlineUsers.entrySet()) {
            types[i] = m.getValue().getType();
            i++;
        }
        return types;
    }

    ArrayList<Client> getClientsList() {
        ArrayList<Client> clientsList = new ArrayList<>(this.onlineUsers.entrySet().size());
        String s = "";
        for (Map.Entry<Integer, Client> m : this.onlineUsers.entrySet()) {
            clientsList.add(m.getValue());
            System.out.println(m.getKey());
            s = s + m.getKey();
        }
        return clientsList;
    }

    String findUser(Integer id) {
        return onlineUsers.get(id).getLogin();
    }
}
