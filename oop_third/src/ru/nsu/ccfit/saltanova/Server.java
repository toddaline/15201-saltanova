package ru.nsu.ccfit.saltanova;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
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
        System.getProperties().setProperty("log4j.configurationFile", "src/log4j2.xml");
    }

    private static UsersList list = new UsersList();
    private static AtomicInteger sessionID = new AtomicInteger(0);
    private static ArrayBlockingQueue<ServerTextMessage> history = new ArrayBlockingQueue<>(Config.HISTORY_LENGTH);
    private List<AllClientThreads> clients = new ArrayList<>();
    private Thread objectListener;
    private Thread xmlListener;
    private ServerSocket socketObjListener;
    private ServerSocket socketXMLListener;
    private final Object lock = new Object();

    private static final Logger log = LogManager.getLogger("log");

    public static void main(String[] args) {
        try {
            Logger logger = LogManager.getRootLogger();
            if (!Config.LOGGING){
                Configurator.setLevel(logger.getName(), Level.OFF);
            }
            Server server = new Server();
            log.info("write \"exit\" to stop server");
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
        try {
            socketObjListener =  new ServerSocket(Config.PORT1);
            socketXMLListener = new ServerSocket(Config.PORT2);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        objectListener = new Thread(new ObjectServerListener());
        objectListener.start();
        xmlListener = new Thread(new XMLServerListener());
        xmlListener.start();
    }

    private class ObjectServerListener implements Runnable {

        @Override
        public void run() {
            try {
                log.info("started obj thread on port " + Config.PORT1);
                while (true) {
                    Socket client = null;
                    while (client == null) {
                        client = socketObjListener.accept();
                    }
                    client.setKeepAlive(true);
                    synchronized (lock) {
                        clients.add(new ClientThread(client));
                    }
                }
            } catch (IOException e) {
                log.info("socket closed");
            } finally {
                log.info("object thread stopped");
            }
        }
    }

    private class XMLServerListener implements Runnable {
        @Override
        public void run() {
            try {
                log.info("started xml thread on port " + Config.PORT2);
                while (true) {
                    Socket client = null;
                    while (client == null) {
                        client = socketXMLListener.accept();
                    }
                    client.setKeepAlive(true);
                    synchronized (lock) {
                        clients.add(new ClientXMLThread(client));
                    }
                }
            } catch (IOException e) {
                log.info("socket closed");
            } finally {
                log.info("xml thread stopped");
            }
        }
    }

    private void stop() {
        synchronized (lock) {
            for (AllClientThreads clientThread : clients) {
                    clientThread.stop();
            }
        }
        try {
            socketObjListener.close();
            socketXMLListener.close();
        } catch (IOException e) {
            log.info("socket closing error");
        }
        try {
            objectListener.join();
            xmlListener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
