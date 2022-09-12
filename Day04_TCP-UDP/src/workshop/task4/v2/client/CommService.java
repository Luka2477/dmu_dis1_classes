package workshop.task4.v2.client;

import workshop.task4.v2.utils.Logger;
import workshop.task4.v2.utils.Package;
import workshop.task4.v2.utils.PackageService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommService extends Thread {

  private final Client client;
  private final Socket connectionSocket;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private BufferedReader inFromServer;
  private DataOutputStream outToServer;

  public CommService(Client client, Socket connectionSocket) {
    this.client = client;
    this.connectionSocket = connectionSocket;

    try {
      inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    } catch (IOException e) {
      Logger.error("Unable to receive data from the server...");
      client.close();
    }

    try {
      outToServer = new DataOutputStream(connectionSocket.getOutputStream());
    } catch (IOException e) {
      Logger.error("Unable to send data to server...");
      client.close();
    }
  }

  @Override
  public synchronized void start() {
    super.start();
    running.set(true);
    Logger.info("Listening for data to %s", connectionSocket.getLocalSocketAddress());
  }

  public void run() {
    while (running.get()) {
      try {
        String data = inFromServer.readLine();
        handleData(data);
      } catch (IOException e) {
        if (running.get()) {
          Logger.warn("Failed to receive data from the server");
        }
      }
    }
  }

  public void close() {
    Logger.warn("Closing communication service...");
    running.set(false);

    try {
      inFromServer.close();
      outToServer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.exit(0);
  }

  private void handleData(String data) {
    if (data == null) {
      client.clearCommandLine();
      Logger.warn("Lost communication to server...");
      close();
      return;
    }

    Package pack = PackageService.deconstruct(data);
    client.handleServerPackage(pack);
  }

  public void sendData(Package pack) {
    String data = PackageService.construct(pack);

    try {
      outToServer.writeBytes(data + "\n");
    } catch (IOException e) {
      Logger.warn("Failed sending data to the server");
    }
  }

}
