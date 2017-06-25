package ru.nsu.ccfit.saltanova;

import ru.nsu.ccfit.saltanova.messages.ClientMessage;
import ru.nsu.ccfit.saltanova.messages.Message;
import ru.nsu.ccfit.saltanova.messages.ServerHandler;
import ru.nsu.ccfit.saltanova.messages.ServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ClientThread extends AllClientThreads {

    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private ServerHandler serverHandler;
    private LinkedBlockingQueue<Message> messagesQueue;
    private Socket socket;
    private Thread reader;
    private Thread writer;

    private static final Logger log = LogManager.getLogger("log");

    ClientThread(Socket socket) {
        try {
            this.socket = socket;
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        messagesQueue = new LinkedBlockingQueue<>();
        serverHandler = new ServerHandler(messagesQueue);
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
                    ClientMessage message = (ClientMessage) inputStream.readObject();
                    message.process(serverHandler);
                    log.info("received message from client");
                }
            } catch (IOException | ClassNotFoundException e) {
                writer.interrupt();
                log.info("disconnected");
            } finally {
                log.info("reader closed");
            }
        }
    }

    public class ClientWriter implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    ServerMessage message = (ServerMessage) messagesQueue.take();
                    outputStream.writeObject(message);
                    outputStream.flush();
                    log.info("sending message to client");
                }
            } catch(InterruptedException e) {
                log.info("thread was interrupted");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                log.info("writer closed");
            }
        }
    }

    @Override
    public void stop() {
        reader.interrupt();
    }

    @Override
    public void banUser() {
        log.info("user was banned");
        stop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

