package workshop.task4.v2.server;

import workshop.task4.v2.Config;
import workshop.task4.v2.utils.Logger;
import workshop.task4.v2.utils.Package;
import workshop.task4.v2.utils.PackageService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

  private final ArrayList<CommService> commServices = new ArrayList<>();
  private final Scanner scanner = new Scanner(System.in);
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final AtomicBoolean usingNameServer = new AtomicBoolean(false);
  private ServerSocket serverSocket;
  private ConnectionService connectionService;
  private Socket nameServerSocket;
  private BufferedReader inFromNameServer;
  private DataOutputStream outToNameServer;

  public static void main(String[] args) {
    Server server = new Server();
    server.start();
  }

  public void start() {
    Logger.info("Type 'close' to close the server.");
    Logger.info("Type 'show' to show all connected clients.");
    Logger.info("---------------------------------------------------------------------------");

    try {
      serverSocket = new ServerSocket(Config.SERVER_PORT);
      Logger.info("Launched server on %s", serverSocket.getLocalSocketAddress());
    } catch (IOException e) {
      Logger.error("Could not launch server on port %s. This is likely due to the port being in use.",
              Config.SERVER_PORT);
      System.exit(0);
    }

    usingNameServer.set(useNameServer());
    connectionService = new ConnectionService(this, serverSocket);
    connectionService.start();
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
    connectionService.close();
    scanner.close();
    commServices.forEach(CommService::close);

    try {
      if (usingNameServer.get()) nameServerSocket.close();
      serverSocket.close();
    } catch (IOException e) {
      Logger.warn(e.getMessage());
    }
  }

  public void clientConnected(Socket connectionSocket) {
    Logger.info("---------------------------------------------------------------------------");
    Logger.info("%s connected", connectionSocket.getRemoteSocketAddress());
    CommService clientService = new CommService(this, connectionSocket);
    clientService.start();
    commServices.add(clientService);

    if (usingNameServer.get()) {
      Package pack = new Package("", clientService.getSocketAddress(), "SERVER", "init");
      sendPackage(clientService, pack);
    }
  }

  public void clientDisconnected(CommService clientService) {
    Logger.info("---------------------------------------------------------------------------");
    Logger.info("%s disconnected", clientService.getSocketAddress());
    commServices.remove(clientService);

    if (usingNameServer.get()) {
      Package pack = new Package("", clientService.getSocketAddress(), "SERVER", "delete");
      sendPackageToNameServer(pack);
    }
  }

  public void sendPackage(CommService clientService, Package pack) {
    if (usingNameServer.get()) {
      pack = sendPackageToNameServer(pack);

      if (pack.status.equals("noexist")) {
        clientService.sendData(pack);
        return;
      }
    }

    for (CommService targetClientService : commServices) {
      if (targetClientService.getSocketAddress().equals(pack.to)) {
        targetClientService.sendData(pack);
        return;
      }
    }

    clientService.sendData(new Package(String.format("Unable to find %s", pack.to), "SERVER", pack.from, "no"));
  }

  private boolean useNameServer() {
    while (true) {
      System.out.print("Would you like to use the name server? (Yes or No) : ");
      String input = scanner.nextLine();
      if (input.equals("Yes")) {
        boolean result = connectToNameServer();
        if (result) return true;
      } else if (input.equals("No")) {
        return false;
      } else {
        Logger.warn("I couldn't understand you...");
      }
    }
  }

  private boolean connectToNameServer() {
    try {
      nameServerSocket = new Socket(Config.NAME_SERVER_HOST, Config.NAME_SERVER_PORT);
      Logger.info("Connected to name server at %s", nameServerSocket.getRemoteSocketAddress());
    } catch (IOException e) {
      Logger.warn("Unable to connect to name server at %s:%s. Make sure the name server is running and the host " +
              "and port are correct.", Config.NAME_SERVER_HOST, Config.NAME_SERVER_PORT);
      return false;
    }

    try {
      inFromNameServer = new BufferedReader(new InputStreamReader(nameServerSocket.getInputStream()));
    } catch (IOException e) {
      Logger.warn("Unable to receive data from name server");
      return false;
    }

    try {
      outToNameServer = new DataOutputStream(nameServerSocket.getOutputStream());
    } catch (IOException e) {
      Logger.error("Unable to send data to name server");
      return false;
    }
    return true;
  }

  private Package sendPackageToNameServer(Package pack) {
    try {
      String data = PackageService.construct(pack);
      outToNameServer.writeBytes(data + "\n");
      Logger.info("Sent data to name server");
    } catch (IOException e) {
      Logger.warn("Failed sending data to name server");
      pack.message = "Something went wrong...";
      pack.to = pack.from;
      pack.from = "SERVER";
      pack.status = "no";
      return pack;
    }

    try {
      String response = inFromNameServer.readLine();
      Logger.info("Received data from name server");
      return PackageService.deconstruct(response);
    } catch (IOException e) {
      Logger.warn("Failed receiving data from name server");
      pack.message = "Something went wrong...";
      pack.to = pack.from;
      pack.from = "SERVER";
      pack.status = "no";
      return pack;
    }
  }

  private void show() {
    for (CommService commService : commServices) {
      Duration time = Duration.between(commService.getCreatedAt(), LocalDateTime.now());
      Logger.info("address = %-22s | up-time = %d:%02d:%02d", commService.getSocketAddress(), time.toHoursPart(),
              time.toMinutesPart(), time.toSecondsPart());
    }
  }

}
