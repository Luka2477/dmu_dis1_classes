package given.sock;

import java.net.*;
import java.util.Scanner;

public class Server {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Common c = new Common("Hej");
		try (ServerSocket welcomeSocket = new ServerSocket(8081)) {
			ServerThread serverThread = new ServerThread(welcomeSocket.accept(), c);
			serverThread.start();

			Scanner scanner = new Scanner(System.in);
			while (true) {
				System.out.print("YOU     : ");
				String input = scanner.nextLine();
				if (input.equals("close")) {
					break;
				}
				System.out.println("CONSOLE : Unknown command");
			}
		}
	}

}
