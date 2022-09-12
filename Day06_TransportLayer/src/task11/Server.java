package task11;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

	private ServerSocket serverSocket;
	private ConnectionService connectionService;
	private final Scanner scanner = new Scanner(System.in);
	private final AtomicBoolean running = new AtomicBoolean(false);

	public void start(int port) {
		try {
			serverSocket = new ServerSocket(port);
			connectionService = new ConnectionService(this, serverSocket);
			connectionService.start();
			running.set(true);
			run();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void run() {
		while (running.get()) {
			System.out.print("YOU     : ");
			String input = scanner.nextLine();
			if (input.equals("close")) {
				close();
			} else {
				System.out.println("CONSOLE : Unknown command");
			}
		}
	}

	private void close() {
		try {
			running.set(false);
			serverSocket.close();
			connectionService.close();
			scanner.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void clientConnected(Socket connectionSocket) {
		CommunicationService communicationService = new CommunicationService(connectionSocket);
		communicationService.start();
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start(8081);
	}

}
