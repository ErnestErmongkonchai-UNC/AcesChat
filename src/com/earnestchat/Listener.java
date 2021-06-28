package com.earnestchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

public class Listener implements Runnable{

    private DatagramSocket socket;
    private int port;
    private Thread run, receive;

    private boolean running = false;

    public Listener(int port) {
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        run = new Thread(this, "Listener");
        run.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Server started on port " + port);
        //manageClients();
        receive();
    }

    private void receive() {
        receive = new Thread("Receive") {
            public void run() {
                while (running) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (SocketException e) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    process(packet);
                }
            }
        };
        receive.start();
    }

    private void process(DatagramPacket packet) {
        String string = new String(packet.getData());
        System.out.println(string);
/*
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
 */
    }


}
