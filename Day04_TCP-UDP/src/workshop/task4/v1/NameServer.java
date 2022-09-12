package workshop.task4.v1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class NameServer {

  private final ServerSocket serverSocket;
  private Socket connectionSocket;
  private BufferedReader inFromServer;
  private DataOutputStream outToServer;
  private final AtomicBoolean running = new AtomicBoolean(true);
  private final HashMap<String, String> users = new HashMap<>();

  public NameServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    System.out.printf("Listening on %s%n", serverSocket.getLocalSocketAddress());
  }

  public void start() throws IOException {
    connectionSocket = serverSocket.accept();
    System.out.printf("Connected to %s.%n", connectionSocket.getRemoteSocketAddress());
    inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    outToServer = new DataOutputStream(connectionSocket.getOutputStream());
    run();
  }

  private void close() {
    try {
      serverSocket.close();
      connectionSocket.close();
      inFromServer.close();
      outToServer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void run() {
    System.out.println("Listening to server...");
    while (running.get()) {
      try {
        String input = listenToServer();
        System.out.printf("Received package '%s'.%n", input);
        String result = handleInput(input);
        System.out.printf("Result of processing '%s'.%n", result);
        sendToServer(result);
        System.out.println("Sent result to server.");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private String listenToServer() throws IOException {
    return inFromServer.readLine();
  }

  private String handleInput(String input) {
    String[] split = input.split("_");
    if (split[0].equals("init")) {
      if (users.containsKey(split[1])) {
        return "taken";
      }
      users.put(split[1], split[2]);
      return "added";
    }
    String address = users.get(split[1]);
    if (split[0].equals("get")) {
      if (address != null) {
        return String.format("succ_%s", address);
      }
      return String.format("noex_%s", split[1]);
    }
    for (Map.Entry<String, String> user : users.entrySet()) {
      if (user.getValue().equals(split[1])) {
        users.remove(user.getKey());
        return "succ_";
      }
    }
    return "noex_";
  }

  private void sendToServer(String input) throws IOException {
    outToServer.writeBytes(input + "\n");
  }

  public static void main(String[] args) throws IOException {
    NameServer server = new NameServer(6780);
    server.start();
  }

}
