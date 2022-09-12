package workshop.task2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerClientThread extends Thread {

  private final Server server;
  private final Socket clientSocket;
  private final BufferedReader inFromClient;
  private final DataOutputStream outToClient;
  private final AtomicBoolean inConversation = new AtomicBoolean(false);
  private final AtomicBoolean running = new AtomicBoolean(true);

  public ServerClientThread(Server server, Socket clientSocket) throws IOException {
    this.server = server;
    this.clientSocket = clientSocket;
    this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.outToClient = new DataOutputStream(clientSocket.getOutputStream());
    this.start();
  }

  public void run() {
    while (running.get()) {
      try {
        String input = inFromClient.readLine();
        handleInput(input);
      } catch (Exception e) {
        System.out.printf("%s disconnected...%n", getSocketAddress());
        close();
      }
    }
  }

  public void close() {
    running.set(false);
    try {
      inFromClient.close();
      outToClient.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleInput(String input) throws Exception {
    String[] split = input.split(";");
    String[] data = split[1].split("&");
    String to = data[1];

    server.sendToClient(input, this, to);
  }

  public void handleOutput(String output) throws IOException {
    outToClient.writeBytes(output + "\n");
  }

  public String getSocketAddress() {
    return clientSocket.getRemoteSocketAddress().toString();
  }
}
