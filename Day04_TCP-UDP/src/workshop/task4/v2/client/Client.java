package workshop.task4.v2.client;

import workshop.task4.v2.Config;
import workshop.task4.v2.utils.Logger;
import workshop.task4.v2.utils.Package;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

  private final CommService serverService;
  private final Scanner scanner = new Scanner(System.in);
  private final AtomicBoolean running = new AtomicBoolean(true);
  private final HashMap<String, Boolean> connections = new HashMap<>();
  private Socket connectionSocket;
  private String replyingTo = "";

  public Client() {
    Logger.info("Type 'close' when replyin to nobody to close the application.");
    Logger.info("Type '->' to switch between connections you are replying to.");
    Logger.info("Type 'Hello <name>' to connect with some one, 'No' to decline a connection and 'Leave chat' to " +
            "disconnect.");
    Logger.info("---------------------------------------------------------------------------");

    try {
      connectionSocket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT);
      Logger.info("Connected to %s", connectionSocket.getRemoteSocketAddress());
    } catch (IOException e) {
      Logger.error("Failed to connect to server on %s:%s. Make sure both the host and port is correct",
              Config.SERVER_HOST, Config.SERVER_PORT);
      System.exit(0);
    }

    serverService = new CommService(this, connectionSocket);
    serverService.start();
    run();
  }

  public static void main(String[] args) {
    new Client();
  }

  private void run() {
    while (running.get()) {
      printCommandLine();
      String input = scanner.nextLine();
      handleClientInput(input);
    }
  }

  public void close() {
    running.set(false);
    scanner.close();

    try {
      connectionSocket.close();
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    serverService.close();
  }

  public void handleServerPackage(Package pack) {
    switch (pack.status) {
      case "hello":
        if (connections.get(pack.from) != null) {
          connections.replace(pack.from, true);
        } else {
          connections.put(pack.from, false);
          replyingTo = replyingTo.isBlank() ? pack.from : replyingTo;
        }
        break;
      case "no":
        connections.remove(pack.from);
        replyingTo = replyingTo.equals(pack.from) ? "" : replyingTo;
        break;
      case "init":
        connections.put(pack.from, true);
        replyingTo = pack.from;
        break;
    }

    clearCommandLine();
    System.out.printf("%s : %s%n", pack.from, pack.message);
    printCommandLine();
  }

  public void handleClientInput(String input) {
    if (replyingTo.isBlank() && input.equals("close")) {
      close();
      return;
    }

    if (input.equals("->")) {
      replyingTo = nextReplyingTo();
      return;
    }

    String to;
    String status;

    if (connections.get(replyingTo) == null || !connections.get(replyingTo) && !input.startsWith("No")) {
      if (input.startsWith("Hello")) {
        to = replyingTo.isBlank() ? input.split(" ")[1] : replyingTo;
        status = connections.get(to) == null ? "hello" : connections.get(to) ? "chat" : "hello";

        if (connections.get(to) != null) {
          connections.replace(to, true);
        } else {
          connections.put(to, false);
          replyingTo = to;
        }
      } else {
        Logger.warn("Please make a connection first...");
        return;
      }
    } else if ((!connections.get(replyingTo) && input.startsWith("No")) || input.startsWith("Leave chat")) {
      to = replyingTo;
      status = "no";
      connections.remove(to);
      replyingTo = "";
    } else {
      to = replyingTo;
      status = replyingTo.equals("SERVER") ? "init" : "chat";
    }

    serverService.sendData(new Package(input, connectionSocket.getLocalSocketAddress().toString(), to, status));
  }

  private String nextReplyingTo() {
    boolean found = replyingTo.isBlank();
    for (Map.Entry<String, Boolean> connection : connections.entrySet()) {
      if (found) return connection.getKey();

      if (connection.getKey().equals(replyingTo)) {
        found = true;
      }
    }

    return "";
  }

  private void printCommandLine() {
    boolean exists = connections.get(replyingTo) != null;
    System.out.printf(
            "-> %s%s : ",
            replyingTo.isBlank() ? "nobody" : replyingTo,
            !exists ? "" : connections.get(replyingTo) ? "" : " (Not connected)"
    );
  }

  public void clearCommandLine() {
    System.out.print("\b".repeat(50));
  }

}
