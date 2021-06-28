package com.earnestchat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatWindow extends JFrame {
  private static final long serialVersionUID = 1L;
  private JPanel contentPane;
  private JTextField txtMessage;
  private JTextArea history;
  private DefaultCaret caret;

  private Socket chatSocket;

  private Thread receiveThread;

  private boolean running = false;

  private void createWindow() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    setTitle(chatSocket.getInetAddress().toString());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(880, 550);
    setLocationRelativeTo(null);

    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);

    GridBagLayout gbl_contentPane = new GridBagLayout();
    gbl_contentPane.columnWidths = new int[] {28, 815, 30, 7}; // SUM = 880
    gbl_contentPane.rowHeights = new int[] {25, 485, 40}; // SUM = 550
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

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            closeWindow();
          }
        });

    setVisible(true);
  }

  private void consolePrint(String message) {
    history.append(message + "\n\r");
    history.setCaretPosition(history.getDocument().getLength());
  }

  private void sendMessage() throws IOException {
    OutputStream output = chatSocket.getOutputStream();
    // text field
    txtMessage = new JTextField();
    txtMessage.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              String textMSG = txtMessage.getText();
              if(!textMSG.equals("")) {
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(textMSG);
                consolePrint(">> " + textMSG);
                txtMessage.setText("");
              }
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
    txtMessage.requestFocusInWindow();

    // Send button
    JButton btnSend = new JButton("Send");
    btnSend.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String textMSG = txtMessage.getText();
            if(!textMSG.equals("")) {
              PrintWriter writer = new PrintWriter(output, true);
              writer.println(textMSG);
              consolePrint(">> " + textMSG);
              txtMessage.setText("");
            }
          }
        });
    GridBagConstraints gbc_btnSend = new GridBagConstraints();
    gbc_btnSend.insets = new Insets(0, 0, 0, 5);
    gbc_btnSend.gridx = 2;
    gbc_btnSend.gridy = 2;
    gbc_btnSend.weightx = 0;
    gbc_btnSend.weighty = 0;
    contentPane.add(btnSend, gbc_btnSend);
  }

  private void closeWindow() {
    System.out.println("disconnect " + chatSocket.getInetAddress().toString());
    running = false;
    try {
      chatSocket.close();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public ChatWindow(Socket socket) {
    this.chatSocket = socket;

    createWindow();

    // manageClients();

    receiveThread =
        new Thread("PeerReceive") {
          public void run() {
            running = true;
            while (running) {
              try {
                InputStream input = chatSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                if (line == null) {
                  closeWindow();
                  dispose();
                } else {
                  consolePrint("<< " + line);
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        };

    receiveThread.start();

    try {
      sendMessage();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
