package task7;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class task2 {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://dis.students.dk/example3.php");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write("year=2022&month=august");
        wr.flush();
        Map<String, List<String>> map = conn.getHeaderFields();
        Set<Map.Entry<String, List<String>>> set = map.entrySet();

        for (Map.Entry<String, List<String>> stringListEntry : set) {
            System.out.println(stringListEntry);
        }
        wr.close();
    }
}
