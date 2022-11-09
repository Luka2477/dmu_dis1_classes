package task2.highTransparancy;

import task2.lowTransparancy.Person;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ViaViewclass {
    public static void main(String[] args) {
        try {
            ArrayList<Person> list = new ArrayList<>();

            //	 læser viewet person via native SQL-Server driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection minConnection;
            minConnection = DriverManager.getConnection("jdbc:sqlserver://localhost;" +
                    "databaseName=dmu_dis1_day17_ddba;user=SA;password=Knudsen123!;");
            Statement stmt = minConnection.createStatement();

            String sql= "select * from person";
            ResultSet res = stmt.executeQuery(sql);

            while (res.next()) {
                Person p = new Person();
                p.setCpr(res.getString("cpr"));
                p.setNavn(res.getString("navn"));
                p.setByNavn(res.getString("bynavn"));
                p.setLoen(res.getInt("loen"));
                p.setSkatteprocent(res.getInt("skatteprocent"));
                list.add(p);
            }

            //	 udskriver indholdet af de to tabeller
            for (Person s : list) {
                System.out.print(s.getCpr() +    "    ");
                System.out.print(s.getNavn() + "    ");
                System.out.print(s.getByNavn() +    "    ");
                System.out.print(s.getLoen() +    "    ");
                System.out.println(s.getSkatteprocent());
            }
        } catch (Exception e) {
            System.out.println("fejl:  "+e.getMessage());
        }
    }
}
