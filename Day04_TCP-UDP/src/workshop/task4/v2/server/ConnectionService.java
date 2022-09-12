package workshop.task4.v2.server;

import workshop.task4.v2.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionService extends Thread {

  private final Server server;
  private final ServerSocket serverSocket;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public ConnectionService(Server server, ServerSocket serverSocket) {
    this.server = server;
    this.serverSocket = serverSocket;
  }

  @Override
  public synchronized void start() {
    super.start();
    running.set(true);
    Logger.info("---------------------------------------------------------------------------");
    Logger.info("Listening for new connections...");
  }

  public void run() {
    while (running.get()) {
      try {
        Socket connectionSocket = serverSocket.accept();
        server.clientConnected(connectionSocket);
      } catch (IOException e) {
        Logger.error("Closing connection service...");
        close();
      }
    }
  }

  public void close() {
    running.set(false);
  }
}
