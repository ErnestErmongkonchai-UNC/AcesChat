package com.earnestchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {

  private ServerSocket listenerSocket;
  private String name;
  private int port;
  private Thread run;

  private boolean running = false;

  public Listener(String name, int port) {
    if (name == null || port < 0) {
      throw new RuntimeException("Error: Invalid arguments");
    }

    this.name = name;
    this.port = port;

    try {
      listenerSocket = new ServerSocket(port);
      run = new Thread(this, "Listener");
      run.start();
    } catch (IOException e) {
      System.out.println("Error: Failed to create a listener socket at port " + port);
    }
  }

  @Override
  public void run() {
    running = true;
    while (running) {
      try {
        Socket peerSocket = listenerSocket.accept();
        ChatWindow chatWindow = new ChatWindow(name, peerSocket);
      } catch (IOException e) {
        System.out.println("Error: Failed to accept connection");
      }
    }
  }

  public void setRunning(boolean condition) {
    if (running && !condition) {
      try {
        listenerSocket.close();
      } catch (IOException e) {
        System.out.println("Error: Failed to close socket");
      }
    }
    this.running = condition;
  }
}
