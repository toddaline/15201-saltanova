package ru.nsu.ccfit.saltanova;

import javax.swing.*;
import java.awt.*;

public class GUIwelcome extends JFrame {

    private JRadioButton xmlButton;
    private JRadioButton objButton;
    private JTextField ip;
    private JTextField port;
    private JButton okButton;

    GUIwelcome() {

        this.setBounds(400, 250, 300, 150);
        JPanel panel = new JPanel();
        FlowLayout f = new FlowLayout();
        panel.setLayout(f);
        panel.add(new JLabel("Choose version:"));

        ButtonGroup bGroup = new ButtonGroup();
        objButton = new JRadioButton();
        objButton.setText("Object");
        bGroup.add(objButton);
        panel.add(objButton);
        objButton.setSelected(true);
        xmlButton = new JRadioButton();
        xmlButton.setText("XML");
        bGroup.add(xmlButton);
        panel.add(xmlButton);

        JPanel panel2 = new JPanel();
        panel2.setLayout(f);

        panel2.add(new JLabel("Server IP"));
        ip = new JTextField(" localhost ");
        panel2.add(ip);
        panel2.add(new JLabel("Server port"));
        port = new JTextField("              ");
        panel2.add(port);

        JPanel panel3 = new JPanel(f);
        okButton = new JButton("OK");
        panel3.add(okButton);

        this.getContentPane().add(panel, BorderLayout.NORTH);
        this.getContentPane().add(panel2, BorderLayout.CENTER);
        this.getContentPane().add(panel3, BorderLayout.SOUTH);
        this.setVisible(true);

        okButton.addActionListener(e -> {
            String IP = ip.getText().trim().toLowerCase();
            String serverPort = port.getText().trim();
            if (isIpValid(IP) && isPortValid(serverPort)) {
                if (objButton.isSelected()) {
                    ServerListenerThread client = new ServerListenerThread();
                    new Thread(() -> client.connect(IP, Integer.parseInt(serverPort), "obj")).start();
                    System.out.println("obj version");
                    dispose();
                } else if (xmlButton.isSelected()) {
                    ServerListenerThread client = new ServerListenerThread();
                    new Thread(() -> client.connect(IP, Integer.parseInt(serverPort), "xml")).start();
                    System.out.println("xml version");
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "nedopusk");
            }
        });
    }

    private boolean isIpValid(String ip) {
        if (ip.equals("localhost")) {
            return true;
        }
        else if (ip.matches(IPADDRESS_PATTERN)) {
            return true;
        }
        return false;
    }

    private boolean isPortValid(String port) {
        try {
            if (Integer.parseInt(port) >= 1) if (Integer.parseInt(port) <= 65535) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

}
