package com.earnestchat;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Listener listener = new Listener(54321);

    Scanner scanner = new Scanner(System.in);
    while (true) {
      String text = scanner.nextLine();
      if (text.equals("quit")) {
        break;
      } else if (text.startsWith("connect ")) {
        String IPaddr = text.split("connect ")[1];
        System.out.println("Enter name: ");
        String name = scanner.nextLine();
        System.out.println("Enter port: ");
        int port = Integer.parseInt(scanner.nextLine());
        Initiator initiator = new Initiator(name, IPaddr, port);
      }
    }
    return;
  }
}
