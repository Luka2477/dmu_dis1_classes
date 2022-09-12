package workshop.task4.v2.server.name;

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

  private final NameServer nameServer;
  private final Socket connectionSocket;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private BufferedReader inFromServer;
  private DataOutputStream outToServer;

  public CommService(NameServer nameServer, Socket connectionSocket) {
    this.nameServer = nameServer;
    this.connectionSocket = connectionSocket;

    try {
      inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    } catch (IOException e) {
      Logger.error("Unable to receive data from %s", connectionSocket.getRemoteSocketAddress());
      close();
    }

    try {
      outToServer = new DataOutputStream(connectionSocket.getOutputStream());
    } catch (IOException e) {
      Logger.error("Unable to send data to %s", connectionSocket.getRemoteSocketAddress());
      close();
    }
  }

  @Override
  public void start() {
    super.start();
    running.set(true);
    Logger.info("Listening for data from %s", connectionSocket.getRemoteSocketAddress());
  }

  public void run() {
    while (running.get()) {
      try {
        String data = inFromServer.readLine();
        handleData(data);
      } catch (IOException e) {
        Logger.error("Closing communication service...");
        close();
      }
    }
  }

  public void close() {
    running.set(false);

    try {
      connectionSocket.close();
      inFromServer.close();
      outToServer.close();
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }
  }

  private void handleData(String data) {
    if (data == null) {
      Logger.warn("Lost communication to server...");
      close();
      nameServer.lookForConnection();
      return;
    }

    Package pack = PackageService.deconstruct(data);
    Logger.info("---------------------------------------------------------------------------");
    Logger.info("Received 'from=%s&to=%s&status=%s' from server", pack.from, pack.to, pack.status);
    nameServer.handlePackage(pack);
  }

  public void sendData(Package pack) {
    String data = PackageService.construct(pack);

    try {
      outToServer.writeBytes(data + "\n");
      Logger.info("Sending 'from=%s&to=%s&status=%s' to server", pack.from, pack.to, pack.status);
    } catch (IOException e) {
      Logger.warn("Failed 'from=%s&to=%s&status=%s' data to server", pack.from, pack.to, pack.status);

    }
  }

}
