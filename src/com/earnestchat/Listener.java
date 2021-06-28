package com.earnestchat;

import java.io.IOException;
import java.net.*;


public class Listener implements Runnable{

    private ServerSocket listenerSocket;
    private int port;
    private Thread run, receiveThread;

    private boolean running = false;

    public Listener(int port) {
        this.port = port;
        try {
            listenerSocket = new ServerSocket(port);
            run = new Thread(this, "Listener");
            run.start();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            try {
                Socket peerSocket = listenerSocket.accept();
                ChatWindow chatWindow = new ChatWindow(peerSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean condition) {
        if(running && !condition) {
            try {
                listenerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.running = condition;
    }
}
