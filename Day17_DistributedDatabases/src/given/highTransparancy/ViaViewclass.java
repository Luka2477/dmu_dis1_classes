package given.highTransparancy;

import java.sql.*;
import java.util.*;
import given.lowTransparancy.*;

public class ViaViewclass {
    public static void main(String[] args) {
        try {
            ArrayList<Person> list = new ArrayList<>();

            //	 læser viewet person via native SQL-Server driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection minConnection;
            minConnection = DriverManager.getConnection("jdbc:sqlserver://localhost;" +
                    "databaseName=dmu_dis1_dbba;user=SA;password=Knudsen123!;");
            Statement stmt = minConnection.createStatement();

            String sql= "select * from Person";
            ResultSet res = stmt.executeQuery(sql);

            while (res.next()) {
                Person p = new Person();
                p.setNavn(res.getString("lname"));
                p.setFornavn(res.getString("fname"));
                p.setKoen(res.getString("sex"));
                p.setLoen(res.getInt("sal"));
                p.setPostnr(res.getString("zip"));
                list.add(p);
            }

            //	 udskriver indholdet af de to tabeller
            for (Person s : list) {
                System.out.print(s.getNavn() +    "    ");
                System.out.print(s.getFornavn() + "    ");
                System.out.print(s.getKoen() +    "    ");
                System.out.print(s.getLoen() +    "    ");
                System.out.println(s.getPostnr());
            }
        } catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    }
}
