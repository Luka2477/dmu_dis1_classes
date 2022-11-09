package task2.lowTransparancy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ViaTwoParts {
    public static void main(String[] args) {
        try {
            ArrayList<Person> list = new ArrayList<>();

            //   læser tabellen Personjyl via native-driver
            Connection minConnection = DriverManager.getConnection("jdbc:sqlserver://localhost;" +
                    "databaseName=dmu_dis1_day17_ddba;user=SA;password=Knudsen123!;");
            Statement stmt = minConnection.createStatement();

            String sql = "select * from Personjyl";
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

            //   l�ser tabellen Personoeer via native-driver
            Connection minCon2 = DriverManager.getConnection("jdbc:sqlserver://10.0.0.2;" +
                    "databaseName=dmu_dis1_day17_ddbb;user=SA;password=Knudsen123!;");
            Statement stmt2 = minCon2.createStatement();

            String sql2 = "select lastname,firstname,sex,round(salary,0) as sal,zip from Personoeer";
            ResultSet res2 = stmt2.executeQuery(sql2);

            while (res2.next()) {
                Person p = new Person();
                p.setCpr(res.getString("cpr"));
                p.setNavn(res.getString("navn"));
                p.setByNavn(res.getString("bynavn"));
                p.setLoen(res.getInt("loen"));
                p.setSkatteprocent(res.getInt("skatteprocent"));
                list.add(p);
            }

            //   udskriver indholdet af de to tabeller
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
