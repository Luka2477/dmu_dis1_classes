package task15;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    try {
      Connection connection;
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      connection = DriverManager.getConnection("jdbc:sqlserver://localhost;" +
              "databaseName=dmu_dis1_day14;user=SA;password=Knudsen123!;");

      Scanner scanner = new Scanner(System.in);

      System.out.print("Please input the account you would like to transfer from: ");
      int from = Integer.parseInt(scanner.nextLine());

      System.out.print("Please input the account you would like to transfer to: ");
      int to = Integer.parseInt(scanner.nextLine());

      System.out.print("Please input the amount you would like to transfer: ");
      int amount = Integer.parseInt(scanner.nextLine());

      Statement statement = connection.createStatement();
      ResultSet result = statement.executeQuery("select saldo from konto where kontonr=" + from);

      if (!result.next()) {
        System.out.println("Transfer account doesn't exist...");
        return;
      }

      int fromBalance = result.getInt(1);

      if (fromBalance < amount) {
        System.out.println("Transfer account does not have sufficient balance...");
        return;
      }

      result = statement.executeQuery("select saldo from konto where kontonr=" + to);

      if (!result.next()) {
        System.out.println("Receiving account doesn't exist...");
        return;
      }

      int toBalance = result.getInt(1);
      int newFromBalance = fromBalance - amount;
      int newToBalance = toBalance + amount;

      statement.executeUpdate("update konto set saldo=" + newFromBalance + " where kontonr=" + from);
      statement.executeUpdate("update konto set saldo=" + newToBalance + " where kontonr=" + to);

      System.out.println("Transfer complete!");

      statement.close();
      connection.close();
    } catch (Exception e) {
      System.out.print("fejl:  " + e.getMessage());
    }
  }
}