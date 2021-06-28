package com.aceschat;


import java.io.IOException;
import java.net.*;

public class Peer {
    private static final long serialVersionUID = 1L;

    private DatagramSocket socket;

    private String name, address;
    private int port;
    private InetAddress ip;
    private Thread send;
    private int ID = -1;

    public Peer(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean openConnection(String address) {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public DatagramPacket receivePacket() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    public void sendPacket(final byte[] data) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    public void sendPacket(final byte[] data, final InetAddress ip, final int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    public void sendPacket(String message, InetAddress address, int port) {
        message += "/e/";
        sendPacket(message.getBytes(), address, port);
    }

    public void closeSocket() {
        new Thread() {
            public void run() {
                synchronized (socket) {
                    socket.close();
                }
            }
        }.start();
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
