package workshop.task4.v1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

  private final Socket clientSocket;
  private final ClientServerThread clientThread;
  private final DataOutputStream outToServer;
  private final Scanner scanner = new Scanner(System.in);
  private final AtomicBoolean running = new AtomicBoolean(true);
  private final HashMap<String, Connection> connections = new HashMap<>();
  private String replyingTo = "";

  public Client(String host, int port) throws IOException {
    clientSocket = new Socket(host, port);
    System.out.printf("Connected to %s%n", clientSocket.getRemoteSocketAddress());
    clientThread = new ClientServerThread(this, clientSocket);
    System.out.printf("Listening on %s%n", clientSocket.getLocalSocketAddress());
    outToServer = new DataOutputStream(clientSocket.getOutputStream());
    listen();
  }

  private void close() throws IOException {
    running.set(false);
    clientSocket.close();
    clientThread.close();
    outToServer.close();
    scanner.close();
  }

  private void listen() throws IOException {
    while (running.get()) {
      printLine();
      String input = scanner.nextLine();
      if (input.equals("close")) {
        close();
      } else if (input.equals("->")) {
        nextReplyingTo();
      } else {
        handleInput(input);
      }
    }
  }

  private void handleInput(String input) throws IOException {
    boolean clearReplyingTo = false;
    Connection connection = connections.get(replyingTo);
    if (connection != null && connection.isWaiting()) {
      System.out.printf("Still waiting for a response from %s.%n", replyingTo);
      return;
    }
    if (connection == null && !input.startsWith("Hello")) {
      System.out.println("Please make a connection first.");
      return;
    }

    if (connection == null || !connection.isConnected()) {
      if (input.startsWith("Hello")) {
        if (replyingTo.isBlank()) {
          String[] split = input.split(" ");
          if (split.length == 1) {
            System.out.println("Please enter the name of the person you want to connect to...");
            return;
          }
          replyingTo = split[1];
          connections.put(replyingTo, new Connection(false, true));
        } else {
          Objects.requireNonNull(connection).setConnected(true);
          connection.setWaiting(true);
        }
      } else if (input.startsWith("No")) {
        connections.remove(replyingTo);
        clearReplyingTo = true;
      } else {
        System.out.println("Please make a connection first.");
        return;
      }
    } else if (input.startsWith("Leave chat")) {
      connections.remove(replyingTo);
      clearReplyingTo = true;
    }

    if (connection != null && connection.isConnected()) {
      connection.setWaiting(true);
    }

    input += String.format(";%s&%s%n", clientSocket.getLocalSocketAddress(), replyingTo);
    replyingTo = clearReplyingTo ? "" : replyingTo;
    outToServer.writeBytes(input);
  }

  public void handleOutput(String output) {
    boolean clearReplyingTo = false;
    String to = output.split(";")[1].split("&")[0];
    replyingTo = replyingTo.isBlank() ? to : replyingTo;

    if (to.equals("SERVER")) {
      if (output.startsWith("Unable") || output.startsWith("Thank you")) {
        connections.remove(replyingTo);
        clearReplyingTo = true;
      } else {
        connections.put(to, new Connection(true, false));
      }
    }

    Connection connection = connections.get(to);
    if (connection == null || !connection.isConnected()) {
      if (output.startsWith("Hello")) {
        if (connection == null) {
          connections.put(to, new Connection(false, false));
        } else {
          connection.setConnected(true);
          connection.setWaiting(false);
        }
      } else if (output.startsWith("No")) {
        connections.remove(to);
        clearReplyingTo = true;
      }
    } else if (output.startsWith("Leave chat")) {
      connections.remove(to);
      clearReplyingTo = true;
    }

    if (connection != null && connection.isConnected()) {
      connection.setWaiting(false);
    }

    clearLine();
    replyingTo = clearReplyingTo ? "" : replyingTo;
    System.out.println(to + " : " + output.substring(0, output.indexOf(";")));
    printLine();
  }

  private void nextReplyingTo() {
    TreeMap<String, Connection> sortedConnections = new TreeMap<>(connections);
    boolean found = replyingTo.isBlank();
    for (String address : sortedConnections.keySet()) {
      if (found) {
        replyingTo = address;
        return;
      } else if (address.equals(replyingTo)) {
        found = true;
      }
    }

    replyingTo = "";
  }

  private void clearLine() {
    System.out.print("\b".repeat(50));
  }

  private void printLine() {
    Connection connection = connections.get(replyingTo);
    System.out.printf("-> %s%s : ", replyingTo.isBlank() ? "nobody" : replyingTo, connection != null && connection.isWaiting() ? " (Waiting)" : "");
  }

  public static void main(String[] args) throws IOException {
    new Client("localhost", 6789);
  }

}
