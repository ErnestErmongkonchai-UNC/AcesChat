package com.earnestchat;

import java.util.Scanner;

public class Main {
  public final static int CHATPORT = 54321;

  public static void main(String[] args) {
    Listener listener = new Listener(CHATPORT);
    System.out.println("Server started on port " + CHATPORT);

    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("$ ");
      String text = scanner.nextLine();
      if (text.equals("quit")) {
        listener.setRunning(false);
        break;
      } else if (text.startsWith("connect ")) {
        String IPaddr = text.split("connect ")[1];
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        Initiator initiator = new Initiator(name, IPaddr, CHATPORT);
      }
    }
  }
}
