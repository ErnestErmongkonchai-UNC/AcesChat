package com.earnestchat;

import java.util.Scanner;

public class Main {
  public final static int CHATPORT = 54321;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter your name: ");
    String name = scanner.nextLine();

    Listener listener = new Listener(name, CHATPORT);
    System.out.println("Server started on port " + CHATPORT);

    while (true) {
      System.out.print("$ ");
      String text = scanner.nextLine();
      if (text.equals("quit")) {
        listener.setRunning(false);
        break;
      } else if (text.startsWith("connect ")) {
        String IPaddr = text.split("connect ")[1];
        Initiator initiator = new Initiator(name, IPaddr, CHATPORT);
      }
    }

    scanner.close();
  }
}
