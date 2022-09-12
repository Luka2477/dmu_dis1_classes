package task9;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

	public static void main(String[] args)throws Exception {
		String clientSentence;
		String capitalizedSentence;
		try (ServerSocket welcomeSocket = new ServerSocket(6789)) {
			// welcomeSocket.setSoTimeout(500);
			Socket connectionSocket = welcomeSocket.accept();
			if (connectionSocket.isConnected()) {
				System.out.println(connectionSocket.getLocalAddress());
			}

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			// connectionSocket.close();
			outToClient.writeBytes(capitalizedSentence);
		}
	}

}
