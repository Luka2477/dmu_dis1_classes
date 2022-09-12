package workshop.task2;

import java.io.IOException;
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

  public Server(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    connectionThread = new ServerConnectionThread(this, serverSocket);
  }

  public void start() throws IOException {
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
    clientThreads.add(new ServerClientThread(this, clientSocket));
  }

  public void sendToClient(String output, ServerClientThread from, String to) throws IOException {
    System.out.printf("%s wants to send '%s' to %s%n", from.getSocketAddress(), output, to);
    for (ServerClientThread clientThread : clientThreads) {
      if (clientThread.getSocketAddress().equals(to)) {
        clientThread.handleOutput(output);
        return;
      }
    }

    System.out.printf("Unable to find %s%n", to);
    from.handleOutput(String.format("Unable to find %s;SERVER&%s", to, from.getSocketAddress()));
  }

  public static void main(String[] args) throws IOException {
    Server server = new Server(6789);
    server.start();
  }

}
