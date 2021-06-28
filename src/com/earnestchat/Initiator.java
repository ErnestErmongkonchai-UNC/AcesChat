package com.earnestchat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;

public class Initiator extends JFrame implements Runnable{
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextArea history;
    private DefaultCaret caret;

    private Peer peer;
    private Thread run;

    private boolean running = false;

    public Initiator (String name, String address, int port) {
        setTitle("Amazing Chat Client");
        peer = new Peer(name, address, port);
        boolean connect = peer.openConnection(address);
        if (!connect) {
            System.err.println("Connection failed!");
            console("Connection failed!");
        }
        createWindow();
        console("Attempting a connection to " + address + ":" + port + ", user: " + name);
        //String connection = "/c/" + name + "/e/";
        String connection = "Connected to " + peer.getName() + "!";
        peer.send(connection.getBytes());
        run = new Thread(this, "Running");
        run.start();
    }
    @Override
    public void run() {
        running = true;
    }

    private void createWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 550);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[] { 28, 815, 30, 7 }; // SUM = 880
        gbl_contentPane.rowHeights = new int[] { 25, 485, 40 }; // SUM = 550
        contentPane.setLayout(gbl_contentPane);

        history = new JTextArea();
        history.setEditable(false);
        JScrollPane scroll = new JScrollPane(history);
        caret = (DefaultCaret) history.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        GridBagConstraints scrollConstraints = new GridBagConstraints();
        scrollConstraints.insets = new Insets(0, 0, 5, 5);
        scrollConstraints.fill = GridBagConstraints.BOTH;
        scrollConstraints.gridx = 0;
        scrollConstraints.gridy = 0;
        scrollConstraints.gridwidth = 3;
        scrollConstraints.gridheight = 2;
        scrollConstraints.weightx = 1;
        scrollConstraints.weighty = 1;
        scrollConstraints.insets = new Insets(0, 5, 0, 0);
        contentPane.add(scroll, scrollConstraints);

        txtMessage = new JTextField();
        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    send(txtMessage.getText(), true);
                }
            }
        });
        GridBagConstraints gbc_txtMessage = new GridBagConstraints();
        gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
        gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMessage.gridx = 0;
        gbc_txtMessage.gridy = 2;
        gbc_txtMessage.gridwidth = 2;
        gbc_txtMessage.weightx = 1;
        gbc_txtMessage.weighty = 0;
        contentPane.add(txtMessage, gbc_txtMessage);
        txtMessage.setColumns(10);

        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send(txtMessage.getText(), true);
            }
        });
        GridBagConstraints gbc_btnSend = new GridBagConstraints();
        gbc_btnSend.insets = new Insets(0, 0, 0, 5);
        gbc_btnSend.gridx = 2;
        gbc_btnSend.gridy = 2;
        gbc_btnSend.weightx = 0;
        gbc_btnSend.weighty = 0;
        contentPane.add(btnSend, gbc_btnSend);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //String disconnect = "/d/" + client.getID() + "/e/";
                String disconnect = peer.getName() + " disconnected!";
                send(disconnect, false);
                running = false;
                peer.close();
            }
        });

        setVisible(true);

        txtMessage.requestFocusInWindow();
    }

    private void send(String message, boolean text) {
        if (message.equals("")) return;
        if (text) {
            message = peer.getName() + ": " + message;
            //message = "/m/" + message + "/e/";
            txtMessage.setText("");
        }
        peer.send(message.getBytes());
    }

    public void console(String message) {
        history.append(message + "\n\r");
        history.setCaretPosition(history.getDocument().getLength());
    }
}
