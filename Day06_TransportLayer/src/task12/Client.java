package task12;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

  public static void main(String[] args) throws IOException {
    ArrayList<Person> persons = new ArrayList<>();
    persons.add(new Person(1, "Lukas", "Viby J"));
    persons.add(new Person(2, "Mads", "Aarhus N"));
    persons.add(new Person(3, "Cilie", "København"));
    persons.forEach(System.out::println);

    try (Socket connectionSocket = new Socket("localhost", 8081)) {
      try (DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream())) {
        PersonService.sendPersons(persons, outToServer);
      }
    }
  }

}
