package task14;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class App {
  public static void main(String[] args) {
    try {
      System.out.println("Program startet");
      Connection minConnection;
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      minConnection = DriverManager.getConnection("jdbc:sqlserver://localhost;" +
              "databaseName=dmu_dis1_day14;user=SA;password=Knudsen123!;");

      Statement stmt = minConnection.createStatement();
      ResultSet res=stmt.executeQuery("select * from konto");

      while (res.next()) {
        System.out.println("Konto " + res.getInt(1) +
                " har saldo " + res.getInt(2) + " og ejer " + res.getString(3));
      }

      if (stmt != null)
        stmt.close();
      if (minConnection != null)
        minConnection.close();
    } catch (Exception e) {
      System.out.print("fejl:  "+e.getMessage());
    }
  }
}
