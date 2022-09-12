package workshop.task4.v2.server.name;

import workshop.task4.v2.Config;
import workshop.task4.v2.utils.Logger;
import workshop.task4.v2.utils.Package;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class NameServer {

  private final Scanner scanner = new Scanner(System.in);
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final ArrayList<Connection> connections = new ArrayList<>();
  private ServerSocket serverSocket;
  private Socket connectionSocket;
  private CommService commService;

  public static void main(String[] args) {
    NameServer nameServer = new NameServer();
    nameServer.start();
  }

  public void start() {
    Logger.info("Type 'close' to close the name server.");
    Logger.info("Type 'show' to show all connections.");
    Logger.info("---------------------------------------------------------------------------");

    try {
      serverSocket = new ServerSocket(Config.NAME_SERVER_PORT);
      Logger.info("Launched name server on %s", serverSocket.getLocalSocketAddress());
    } catch (IOException e) {
      Logger.error("Could not launch name server on port %s. This is likely due to the port being in use.",
              Config.NAME_SERVER_PORT);
      System.exit(0);
    }

    lookForConnection();
    running.set(true);
    run();
  }

  private void run() {
    while (running.get()) {
      String input = scanner.nextLine();
      if (input.equals("close")) {
        close();
      } else if (input.equals("show")) {
        show();
      } else {
        Logger.warn("Unknown command");
      }
    }
  }

  private void close() {
    running.set(false);
    commService.close();
    scanner.close();

    try {
      serverSocket.close();
      connectionSocket.close();
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    System.exit(0);
  }

  public void lookForConnection() {
    try {
      Logger.info("---------------------------------------------------------------------------");
      Logger.info("Listening for new connection...");
      connectionSocket = serverSocket.accept();
      Logger.info("Server connected at %s", connectionSocket.getRemoteSocketAddress());
    } catch (IOException e) {
      Logger.error("Unable to listen for connections");
      close();
    }

    commService = new CommService(this, connectionSocket);
    commService.start();
  }

  public void handlePackage(Package pack) {
    if (pack.status.equals("init")) {
      if (pack.message.isBlank()) {
        pack.message = "Please send your name.";
      } else if (putIfAbsent(pack.message, pack.from) == null) {
        pack.message = String.format("Welcome %s.", pack.message);
        pack.status = "no";
      } else {
        pack.message = String.format("%s is taken. Please send another", pack.message);
      }
      swapParticipants(pack);
    } else if (pack.status.equals("delete")) {
      connections.remove(findByAddress(pack.from));
    } else {
      Connection result = findByAddress(pack.from);
      pack.from = result != null ? result.name : "";
      Connection to = findByName(pack.to);

      if (to == null) {
        pack.message = String.format("Unable to find %s.", pack.to);
        pack.status = "noexist";
        swapParticipants(pack);
      } else {
        pack.to = to.address;
      }
    }

    commService.sendData(pack);
  }

  private void swapParticipants(Package pack) {
    String temp = pack.from;
    pack.from = pack.to;
    pack.to = temp;
  }

  private Connection putIfAbsent(String name, String address) {
    Connection connection = findByName(name);

    if (connection == null) {
      connections.add(new Connection(name, address));
    }

    return connection;
  }

  private Connection findByName(String name) {
    for (Connection connection : connections) {
      if (connection.name.equals(name)) {
        return connection;
      }
    }
    return null;
  }

  private Connection findByAddress(String address) {
    for (Connection connection : connections) {
      if (connection.address.equals(address)) {
        return connection;
      }
    }
    return null;
  }

  private void show() {
    for (Connection connection : connections) {
      Logger.info("name = %s : address = %s", connection.name, connection.address);
    }
  }

}
