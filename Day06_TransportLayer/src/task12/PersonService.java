package task12;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class PersonService {

  public static void sendPersons(ArrayList<Person> persons, DataOutputStream outStream) {
    ArrayList<String> personStrings = new ArrayList<>();
    persons.forEach(person -> personStrings.add(person.toQueryString()));
    String result = String.join(";", personStrings);

    try {
      outStream.writeBytes(result);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.out.printf("Sent '%s'.%n", result);
  }

  public static ArrayList<Person> receivePersons(BufferedReader inStream) {
    ArrayList<Person> persons = new ArrayList<>();
    String personsString;

    try {
      personsString = inStream.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.out.printf("Received '%s'.%n", personsString);

    for (String personString : personsString.split(";")) {
      String[] attributes = personString.split("&");
      int id = Integer.parseInt(attributes[0].split("=")[1]);
      String name = attributes[1].split("=")[1];
      String city = attributes[2].split("=")[1];

      persons.add(new Person(id, name, city));
    }

    return persons;
  }

}
