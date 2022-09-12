package task12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

  public static void main(String[] args) throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(8081)) {
      try (Socket connectionSocket = serverSocket.accept()) {
        try (BufferedReader inFromClient =
                     new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))) {
          ArrayList<Person> persons = PersonService.receivePersons(inFromClient);
          persons.forEach(System.out::println);
        }
      }
    }
  }

}
