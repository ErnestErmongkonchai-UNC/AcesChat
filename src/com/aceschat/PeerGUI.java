package com.aceschat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class PeerGUI extends JFrame implements Runnable{
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextArea history;
    private DefaultCaret caret;
    private Thread run, listen;
    private Peer peer;

    private boolean running = false;
    /*
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmOnlineUsers;
    private JMenuItem mntmExit;
*/

    //private OnlineUsers users;

    public PeerGUI() {
        //TODO
        String name = "Ernest";
        String address = "192.168.10.106";
        int port = 54321;

        setTitle("AcesChat");
        peer = new Peer(name, address, port);
        boolean connect = peer.openConnection(address);
        if (!connect) {
            System.err.println("Connection failed!");
            printConsole("Connection failed!");
        }
        createGUI();
        //printConsole("Attempting a connection to " + address + ":" + port + ", user: " + name);
        String connection = "/c/" + name + "/e/";
        peer.sendPacket(connection.getBytes());
        //users = new OnlineUsers();
        running = true;
        run = new Thread(this, "Running");
        run.start();
    }

    private void createGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 550);
        setLocationRelativeTo(null);

       /* menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnFile = new JMenu("File");
        menuBar.add(mnFile);

        mntmOnlineUsers = new JMenuItem("Online Users");
        mntmOnlineUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                users.setVisible(true);
            }
        });
        mnFile.add(mntmOnlineUsers);

        mntmExit = new JMenuItem("Exit");
        mnFile.add(mntmExit);
        */

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
                    sendMessage(txtMessage.getText(), true);
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
                sendMessage(txtMessage.getText(), true);
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
                String disconnect = "/d/" + peer.getID() + "/e/";
                sendMessage(disconnect, false);
                running = false;
                peer.closeSocket();
            }
        });

        setVisible(true);

        txtMessage.requestFocusInWindow();
    }

    public void run() {
        listen();
    }

    private void sendMessage(String message, boolean text) {
        if (message.equals("")) return;
        if (text) {
            message = peer.getName() + ": " + message;
            message = "/m/" + message + "/e/";
            txtMessage.setText("");
        }
        peer.sendPacket(message.getBytes());
    }

    public void listen() {
        listen = new Thread("Listen") {
            public void run() {
                while (running) {
                    String message = "test";
                    if (message.startsWith("/c/")) {
                        peer.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
                        printConsole("Successfully connected to server! ID: " + peer.getID());
                    } else if (message.startsWith("/m/")) {
                        String text = message.substring(3);
                        text = text.split("/e/")[0];
                        printConsole(text);
                    } else if (message.startsWith("/i/")) {
                        String text = "/i/" + peer.getID() + "/e/";
                        sendMessage(text, false);
                    } else if (message.startsWith("/u/")) {
                        String[] u = message.split("/u/|/n/|/e/");
                        //users.update(Arrays.copyOfRange(u, 1, u.length - 1));
                    }
                }
            }
        };
        listen.start();
    }

    /*private void process(DatagramPacket packet) {
        String string = new String(packet.getData());
        //if (raw) System.out.println(string);
        if (string.startsWith("/c/")) {
            // UUID id = UUID.randomUUID();
            int id = UniqueIdentifier.getIdentifier();
            String name = string.split("/c/|/e/")[1];
            System.out.println(name + "(" + id + ") connected!");
            clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
            String ID = "/c/" + id;
            send(ID, packet.getAddress(), packet.getPort());
        } else if (string.startsWith("/m/")) {
            sendToAll(string);
        } else if (string.startsWith("/d/")) {
            String id = string.split("/d/|/e/")[1];
            disconnect(Integer.parseInt(id), true);
        } else if (string.startsWith("/i/")) {
            clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
        } else {
            System.out.println(string);
        }
    }*/

    public void printConsole(String message) {
        history.append(message + "\n\r");
        history.setCaretPosition(history.getDocument().getLength());
    }



    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PeerGUI frame = new PeerGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
