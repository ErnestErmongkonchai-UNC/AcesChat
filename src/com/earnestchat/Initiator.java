package com.earnestchat;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Initiator extends JFrame{
    private Socket chatSocket;

    private Thread connectThread;

    public Initiator (String name, String address, int port) {
        connectThread = new Thread("PeerReceive") {
            public void run() {
                try {
                    chatSocket = new Socket(address, port);
                    ChatWindow chatWindow = new ChatWindow(chatSocket);
                } catch (IOException e) {
                    System.out.println("Failed to connect to " + address + ":" + port);
                }
            }
        };
        connectThread.start();

    }
}
