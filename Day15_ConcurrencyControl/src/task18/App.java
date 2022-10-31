package task18;

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
      Statement statement = connection.createStatement();

      Scanner scanner = new Scanner(System.in);

      System.out.print("Please input the account you would like to transfer from: ");
      int from = Integer.parseInt(scanner.nextLine());

      System.out.print("Please input the account you would like to transfer to: ");
      int to = Integer.parseInt(scanner.nextLine());

      System.out.print("Please input the amount you would like to transfer: ");
      int amount = Integer.parseInt(scanner.nextLine());

      connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
      connection.setAutoCommit(false);
      ResultSet result = statement.executeQuery("select saldo from konto (updlock) where kontonr=" + from);

      if (!result.next()) {
        connection.rollback();
        System.out.println("Transfer account doesn't exist...");
        return;
      }

      int fromBalance = result.getInt(1);

      if (fromBalance < amount) {
        connection.rollback();
        System.out.println("Transfer account does not have sufficient balance...");
        return;
      }

      result = statement.executeQuery("select saldo from konto (updlock) where kontonr=" + to);

      if (!result.next()) {
        connection.rollback();
        System.out.println("Receiving account doesn't exist...");
        return;
      }

      // Wait to simulate deadlock
      System.out.print("Thinking... (Press ENTER to continue)");
      scanner.nextLine();
      // End of wait

      int toBalance = result.getInt(1);
      int newFromBalance = fromBalance - amount;
      int newToBalance = toBalance + amount;

      statement.executeUpdate("update konto set saldo=" + newFromBalance + " where kontonr=" + from);
      statement.executeUpdate("update konto set saldo=" + newToBalance + " where kontonr=" + to);

      connection.commit();
      System.out.println("Transfer complete!");

      statement.close();
      connection.close();
    } catch (Exception e) {
      System.out.print("fejl:  " + e.getMessage());
    }
  }
}
