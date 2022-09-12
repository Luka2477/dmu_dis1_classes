package workshop.task4.v1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerConnectionThread extends Thread {

  private final Server server;
  private final ServerSocket serverSocket;
  private final AtomicBoolean running = new AtomicBoolean(true);

  public ServerConnectionThread(Server server, ServerSocket serverSocket) {
    this.server = server;
    this.serverSocket = serverSocket;
  }

  public void run() {
    while (running.get()) {
      try {
        Socket clientSocket = serverSocket.accept();
        server.clientConnected(clientSocket);
      } catch (IOException e) {
        System.out.println("Stopped listening for new connections...");
      }
    }
  }

  public void close() {
    running.set(false);
  }

}
