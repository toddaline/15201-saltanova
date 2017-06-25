package ru.nsu.ccfit.saltanova;

import ru.nsu.ccfit.saltanova.messages.Message;
import ru.nsu.ccfit.saltanova.messages.ServerTextMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {

    static {
        System.getProperties().setProperty("log4j.configurationFile", "./log4j2.xml");
    }

    private static UsersList list = new UsersList();
    private static AtomicInteger sessionID = new AtomicInteger(0);
    private static ArrayBlockingQueue<ServerTextMessage> history = new ArrayBlockingQueue<>(Config.HISTORY_LENGTH);
    private List<AllClientThreads> clients = new ArrayList<>();
    private Thread objectListener;
    private Thread xmlListener;
    private final Object lock = new Object();

    private static final Logger log = LogManager.getLogger("log");

    public static void main(String[] args) {
        try {
            Server server = new Server();
            System.out.println("write \"exit\" to stop server");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                if (br.readLine().equals("exit")) {
                    server.stop();
                    log.info("server stopped");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() {
        objectListener = new Thread(new ObjectServerListener());
        objectListener.start();
        xmlListener = new Thread(new XMLServerListener());
        xmlListener.start();
    }

    private class ObjectServerListener implements Runnable {

        @Override
        public void run() {
            ServerSocket socketListener;
            try {
                socketListener = new ServerSocket(Config.PORT1);
                log.info("started obj thread on port " + Config.PORT1);
                while (true) {
                        Socket client = null;
                        while (client == null) {
                            client = socketListener.accept();
                        }
                        client.setKeepAlive(true);
                        synchronized (lock) {
                            clients.add(new ClientThread(client));
                        }
                        log.info("new obj client");
                }
            } catch (IOException e) {
                log.info("socket closed");
            }
        }
    }

    private class XMLServerListener implements Runnable {
    @Override
        public void run() {
        ServerSocket socketListener;
        try {
            socketListener = new ServerSocket(Config.PORT2);
            log.info("started xml thread on port " + Config.PORT2);
            while (true) {
                Socket client = null;
                while (client == null) {
                    client = socketListener.accept();
                }
                client.setKeepAlive(true);
                synchronized (lock) {
                    clients.add(new ClientXMLThread(client));
                }
                log.info("new xml client");
            }
            } catch (IOException e) {
                log.info("socket closed");
            }
        }
    }

    private void stop() {
        objectListener.interrupt();
        xmlListener.interrupt();
        synchronized (lock) {
            for (AllClientThreads clientThread : clients) {
                clientThread.stop();
            }
        }
        if (objectListener.isAlive()) {
            log.info("object thread wasnt interrupted");
        }
        if (xmlListener.isAlive()) {
            log.info("xml thread wasnt interrupted");



        }
    }

    public synchronized static ArrayBlockingQueue<ServerTextMessage> getHistory() {
        return history;
    }
    public static int createID() {
        return sessionID.getAndIncrement();
    }

    public synchronized static void addUser(String login, int sessionID, String type, LinkedBlockingQueue<Message> messagesQueue) throws IOException {
        list.addUser(login, sessionID, type, messagesQueue);
    }
    public synchronized static ArrayList<Client> getClientsList() {
        return list.getClientsList();
    }
    public synchronized static String[] getUsers() {
        return list.getUsers();
    }
    public synchronized static String[] getTypes() {
        return list.getTypes();
    }
    public synchronized static void deleteUser(int id) {
        list.deleteUser(id);
    }
    public synchronized static String findUser(int id) {
        return list.findUser(id);
    }
}
