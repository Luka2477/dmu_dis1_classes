package workshop.task4.v1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

  private final ServerSocket serverSocket;
  private final ServerConnectionThread connectionThread;
  private final Scanner scanner = new Scanner(System.in);
  private final ArrayList<ServerClientThread> clientThreads = new ArrayList<>();
  private final AtomicBoolean running = new AtomicBoolean(true);
  private final AtomicBoolean useNameServer = new AtomicBoolean(false);
  private BufferedReader inFromNameServer;
  private DataOutputStream outToNameServer;

  public Server(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    connectionThread = new ServerConnectionThread(this, serverSocket);
  }

  public void start() throws IOException {
    useNameServer.set(useNameServer());
    connectionThread.start();
    System.out.printf("Listening on %s%n", serverSocket.getLocalSocketAddress());
    listen();
  }

  private void close() throws IOException {
    running.set(false);
    serverSocket.close();
    connectionThread.close();
    scanner.close();
    clientThreads.forEach(ServerClientThread::close);
  }

  private void listen() throws IOException {
    while (running.get()) {
      String input = scanner.nextLine();
      if (input.equals("close")) {
        close();
      } else {
        System.out.println("Unknown command");
      }
    }
  }

  public void clientConnected(Socket clientSocket) throws IOException {
    System.out.printf("%s connected%n", clientSocket.getRemoteSocketAddress());
    ServerClientThread clientThread = new ServerClientThread(this, clientSocket);
    clientThreads.add(clientThread);

    if (useNameServer.get()) {
      clientThread.handleOutput(String.format("Please send your name.;SERVER&%s", clientThread.getSocketAddress()));
    }
  }

  public void clientDisconnected(ServerClientThread clientThread) throws IOException {
    System.out.printf("%s disconnected...%n", clientThread.getSocketAddress());
    clientThreads.remove(clientThread);
    sendToNameServer(String.format("del_%s", clientThread.getSocketAddress()));
    clientThread.close();
  }

  public void sendToClient(String output, ServerClientThread from, String to) throws IOException {
    System.out.printf("%s wants to send '%s' to %s%n", from.getSocketAddress(), output, to);

    if (useNameServer.get()) {
      String[] split = output.split(";");
      if (to.equals("SERVER")) {
        String response = sendToNameServer(String.format("init_%s_%s", split[0], from.getSocketAddress()));
        if (response.equals("taken")) {
          response = String.format("That names already taken. Please send another one.;SERVER&%s", from.getSocketAddress());
        } else {
          response = String.format("Thank you %s.;SERVER&%s", split[0], from.getSocketAddress());
        }
        from.handleOutput(response);
        return;
      } else {
        String[] data = split[1].split("&");
        String response = sendToNameServer(String.format("get_%s", data[1]));
        if (response.startsWith("noex_")) {
          from.handleOutput(String.format("Unable to find %s.;SERVER&%s", data[1], from.getSocketAddress()));
          return;
        } else {
          to = response.substring(5);
        }
      }
    }

    for (ServerClientThread clientThread : clientThreads) {
      if (clientThread.getSocketAddress().equals(to)) {
        clientThread.handleOutput(output);
        return;
      }
    }

    from.handleOutput(String.format("Unable to find %s;SERVER&%s", to, from.getSocketAddress()));
  }

  private boolean useNameServer() {
    System.out.println("Would you like to use the name server? (Yes or No)");
    while (true) {
      String input = scanner.nextLine();
      if (input.equals("No")) {
        return false;
      } else if (input.equals("Yes")) {
        while (true) {
          System.out.println("Please make sure the name server is running.");
          System.out.print("Host address and port (separate with ':') : ");
          String[] split = scanner.nextLine().split(":");
          String host = split[0];
          int port;
          try {
            port = Integer.parseInt(split[1]);
          } catch (NumberFormatException e) {
            System.out.println("Please make sure the port is a whole number.");
            continue;
          }
          if (connectToNameServer(host, port)) {
            System.out.println("Connected to name server...");
            return true;
          }
          System.out.printf("Could not connect to name server on %s:%d.%n", host, port);
        }
      } else {
        System.out.println("Please type 'Yes' or 'No'");
      }
    }
  }

  private boolean connectToNameServer(String host, int port) {
    try (Socket nameServerSocket = new Socket(host, port)) {
      inFromNameServer = new BufferedReader(new InputStreamReader(nameServerSocket.getInputStream()));
      outToNameServer = new DataOutputStream(nameServerSocket.getOutputStream());
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  private String sendToNameServer(String output) throws IOException {
    outToNameServer.writeBytes(output + "\n");
    return inFromNameServer.readLine();
  }

  public static void main(String[] args) throws IOException {
    Server server = new Server(6789);
    server.start();
  }

}
