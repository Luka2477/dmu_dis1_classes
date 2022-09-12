package workshop.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientServerThread extends Thread {

  private final Client client;
  private final BufferedReader inFromServer;
  private final AtomicBoolean running = new AtomicBoolean(true);

  public ClientServerThread(Client client, Socket clientSocket) throws IOException {
    this.client = client;
    this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.start();
  }

  public void close() throws IOException {
    running.set(false);
    inFromServer.close();
  }

  public void run() {
    while (running.get()) {
      try {
        String output = inFromServer.readLine();
        client.handleOutput(output);
      } catch (IOException e) {
        System.out.println("Stopped listening to server...");
      }
    }
  }

}
