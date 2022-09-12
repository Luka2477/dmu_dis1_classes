package task11;

import java.net.*;
import java.io.*;
import java.util.Arrays;

public class CommunicationService extends Thread {
	private final Socket connectionSocket;

	public CommunicationService(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			String clientSentence = inFromClient.readLine();
			String[] split = clientSentence.split(" ");
			String documentPath = split[1];
			String contentType = getContentType(documentPath);
			// String documentContent = readFile(documentPath);
			byte[] documentContent = read(documentPath);

			outToClient.writeBytes("HTTP 200 OK\n");
			outToClient.writeBytes(contentType + "\n");
			outToClient.writeBytes("Connection: Close\n");
			outToClient.writeBytes("\n");
			// outToClient.writeBytes(documentContent + "\n");
			outToClient.write(documentContent);

			inFromClient.close();
			outToClient.close();
			connectionSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getContentType(String documentName) {
		if (documentName.endsWith(".html")){
			return ("Content-Type: text/html");
		} else if (documentName.endsWith(".gif")) {
			return ("Content-Type: image/gif");
		} else if (documentName.endsWith(".png")) {
			return ("Content-Type: image/png");
		} else if (documentName.endsWith(".jpg")) {
			return ("Content-Type: image/jpg");
		} else if (documentName.endsWith(".js")) {
			return ("Content-Type: text/javascript");
		} else if (documentName.endsWith(".css")) {
			return ("Content-Type: text/css");
		} else {
			return ("Content-Type: text/plain");
		}
	}

	private String readFile(String path) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(String.format("/Users/lukasknudsen/Documents/dmu" +
							"/dis1/Day06_TransportLayer/src/task11/resources%s", path)));
			StringBuilder content = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				content.append(line.replace("\t", ""));
				line = reader.readLine();
			}
			reader.close();
			return content.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] read(String aInputFileName) throws FileNotFoundException{
		// returns the content of a file in a binary array
		System.out.println("Reading in binary file named : " +
						aInputFileName);
		File file =
						new File("/Users/lukasknudsen/Documents/dmu/dis1/Day06_TransportLayer/src/task11/resources" + aInputFileName);
		System.out.println("File size: " + file.length());
		byte[] result = new byte[(int)file.length()];
		try {
			int totalBytesRead = 0;
			InputStream input = new BufferedInputStream(new FileInputStream(file));
			while(totalBytesRead < result.length){
				int bytesRemaining = result.length - totalBytesRead;
				int bytesRead = input.read(result, totalBytesRead,
								bytesRemaining);
				//input.read() returns -1, 0, or more :
				if (bytesRead > 0) {
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}
			System.out.println("Num bytes read: " + totalBytesRead);
			System.out.println("Closing input stream.");
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}

