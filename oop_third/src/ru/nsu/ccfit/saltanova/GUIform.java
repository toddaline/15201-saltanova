package ru.nsu.ccfit.saltanova;

import ru.nsu.ccfit.saltanova.messages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUIform extends JFrame implements ObserverMessage {

    private JTextArea textField;
    private JTextArea onlineField = new JTextArea("Online users:\n",15,10);
    private JTextField messageField = new JTextField();

    private static final Logger log = LogManager.getLogger("log");

    public GUIform(ClientHandler client) {
        super("u dont need this chat because u dont have friends");

        client.addObserver((ServerMessage message) -> message.process(this));

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                client.addNewMessage(new Logout(client.getSessionID()));
            }
        };

        this.addWindowListener(exitListener);
        textField = new JTextArea("Welcome to chat\n",15,10);
        textField.setLineWrap(true);
        textField.setWrapStyleWord(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setBounds(300,300,600,600);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 100;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        textField.setEditable(false);
        this.add(new JScrollPane(textField),c);
        c.gridx = 0;
        c.gridy = 100;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 0.0125;
        c.weighty = 0.0125;
        this.add(messageField,c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 100;
        c.weightx = 0.25;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        onlineField.setEditable(false);
        this.add(onlineField, c);
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    UserTextMessage message = new UserTextMessage(messageField.getText(), client.getSessionID());
                    client.addNewMessage(message);
                    log.info("sending message to users");
                    messageField.setText(null);
                }
            }
        });
    }

    @Override
    public void process(ServerTextMessage message) {
        textField.append("[ " + java.util.Calendar.getInstance().getTime() + " ] " + message.getLogin() + " : " + message.getMessage() + "\n");
        java.awt.Rectangle rect = new java.awt.Rectangle(0, textField.getHeight(), 1, 1);
        textField.scrollRectToVisible(rect);
    }

    @Override
    public void process(RequestListSuccess message) {
        if (message.getUsersList() != null){
            onlineField.setText("Online users:\n");
            for (String s : message.getUsersList())
                onlineField.append(s + "\n");
        }
    }

    @Override
    public void process(RequestListError message) {
        textField.append(message.getReason());
        log.info("cannot get list of users");
    }

    @Override
    public void process(LoginSuccess message) {
        this.setVisible(true);
        log.info("connected successfully");
    }

    @Override
    public void process(LoginError message) {}

    @Override
    public void process(TextMessageError message) {
        textField.append(message.getReason() + "\n");
    }

    @Override
    public void process(TextMessageSuccess message) {}

    @Override
    public void process(UserLoginMessage message) {
        textField.append(message.getLogin() + " was connected\n");
        onlineField.append(message.getLogin() + "\n");
    }

    @Override
    public void process(UserLogoutMessage message) {
        textField.append(message.getLogin() + " was disconnected\n");
        onlineField.setText(onlineField.getText().replaceFirst(message.getLogin() + "\n", ""));
    }

    @Override
    public void process(LogoutSuccess message) {
        log.info("disconnected successfully");
        Thread.currentThread().interrupt();
    }

    @Override
    public void process(LogoutError message) {
        log.info("logout error");
        System.exit(-1);
    }
}