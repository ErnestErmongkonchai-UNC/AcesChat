package com.earnestchat;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class Initiator extends JFrame {
  private Socket chatSocket;

  private Thread connectThread;

  public Initiator(String name, String address, int port) {
    if (name == null || address == null || port < 0) {
      throw new RuntimeException("Error: Invalid inputs");
    }

    connectThread =
        new Thread("PeerReceive") {
          public void run() {
            try {
              chatSocket = new Socket(address, port);
              ChatWindow chatWindow = new ChatWindow(name, chatSocket);
            } catch (IOException e) {
              System.out.println("Error: Failed to connect to " + address + ":" + port);
            }
          }
        };
    connectThread.start();
  }
}
