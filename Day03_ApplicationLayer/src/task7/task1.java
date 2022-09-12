package task7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class task1 {
//    public static void main(String[] args) throws IOException {
//        URL url = new URL("https://dis.students.dk/example1.php");
//        InputStreamReader r = new InputStreamReader(url.openStream());
//        BufferedReader in = new BufferedReader(r);
//        String str;
//        boolean found = false;
//        while ((str = in.readLine()) != null) {
//            if (found) {
//                for (String sec : str.split(" ")) {
//                    try {
//                        System.out.printf("Number is %d.", Integer.parseInt(sec));
//                    } catch (Exception e) {
//                        // DO NOTHING
//                    }
//                }
//
//                break;
//            }
//
//            if (str.equalsIgnoreCase("<body>"))
//                found = true;
//        }
//        in.close();
//    }

    public static void main(String[] args) throws IOException {
        URL url = new URL("https://m.valutakurser.dk");
        InputStreamReader r = new InputStreamReader(url.openStream());
        BufferedReader in = new BufferedReader(r);
        String str;
        int found = 0;
        while ((str = in.readLine()) != null) {
            if (str.contains("<a href=\"/valuta/amerikanske-dollar/USD\">")) {
                String[] arr = str.split("[<>]");

                for (String sec : arr) {
                    if (found == 2) {
                        System.out.printf("100 DKK = %s USD", sec);
                        break;
                    }

                    if (sec.equalsIgnoreCase("div class=\"currencyItem_actualValueContainer__2xLkB\""))
                        found++;
                }
            }
        }
        in.close();
    }
}
