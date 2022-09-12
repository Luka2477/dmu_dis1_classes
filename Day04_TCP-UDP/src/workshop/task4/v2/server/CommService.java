package workshop.task4.v2.server;

import workshop.task4.v2.utils.Logger;
import workshop.task4.v2.utils.Package;
import workshop.task4.v2.utils.PackageService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommService extends Thread {

  private final Server server;
  private final Socket connectionSocket;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private BufferedReader inFromClient;
  private DataOutputStream outToClient;
  private LocalDateTime createdAt;

  public CommService(Server server, Socket connectionSocket) {
    this.server = server;
    this.connectionSocket = connectionSocket;

    try {
      inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    } catch (IOException e) {
      Logger.warn("Unable to receive data from %s", getSocketAddress());
      close();
    }

    try {
      outToClient = new DataOutputStream(connectionSocket.getOutputStream());
    } catch (IOException e) {
      Logger.warn("Unable to send data to %s", getSocketAddress());
      close();
    }
  }

  @Override
  public synchronized void start() {
    super.start();
    createdAt = LocalDateTime.now();
    running.set(true);
    Logger.info("Listening for data from %s", getSocketAddress());
  }

  public void run() {
    while (running.get()) {
      try {
        String data = inFromClient.readLine();
        handleData(data);
      } catch (IOException e) {
        Logger.warn("Closing communication service...");
        close();
      }
    }
  }

  public void close() {
    running.set(false);

    try {
      connectionSocket.close();
    } catch (IOException e) {
      Logger.warn(e.getMessage());
    }
  }

  private void handleData(String data) {
    if (data == null) {
      server.clientDisconnected(this);
      close();
      return;
    }

    Package pack = PackageService.deconstruct(data);
    Logger.info("---------------------------------------------------------------------------");
    Logger.info("Received data from %s", getSocketAddress());
    server.sendPackage(this, pack);
  }

  public void sendData(Package pack) {
    String data = PackageService.construct(pack);

    try {
      outToClient.writeBytes(data + "\n");
      Logger.info("Sending data to %s", getSocketAddress());
    } catch (IOException e) {
      Logger.warn("Failed sending data to %s", getSocketAddress());
    }
  }

  public String getSocketAddress() {
    return connectionSocket.getRemoteSocketAddress().toString();
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

}
