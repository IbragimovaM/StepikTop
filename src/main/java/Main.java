import javafx.util.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("-help : Viewing this help information");
        System.out.println("<number> : Display top <number> popular courses");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {

        if(args.length == 0 || args[0].equals("-help")) {
            printHelp();
        }

        int courseQuantity = 0;
        try {
            courseQuantity = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Not a number: " + args[0] + ".");
            printHelp();
        }

        if(courseQuantity < 1) {
            System.out.println("Number of courses should be more than 0");
            printHelp();
        }

        System.out.print("Please wait...");

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());

        boolean hasNext = true;
        List<Pair<Integer, String>> courses = new ArrayList<>();
        try (CloseableHttpClient client = HttpClients.custom().setSSLContext(sslContext).build()) {
            for (int page = 1; hasNext; page++) {

                HttpGet query = new HttpGet("https://stepik.org:443/api/courses?page=" + page);
                try (CloseableHttpResponse response = client.execute(query)) {
                    String answer = EntityUtils.toString(response.getEntity());
                    JSONObject obj = new JSONObject(answer);
                    hasNext = obj.getJSONObject("meta").getBoolean("has_next");

                    for (Object item : obj.getJSONArray("courses")) {
                        JSONObject data = (JSONObject) item;
                        Integer learners = data.getInt("learners_count");
                        String title = data.getString("title");

                        courses.add(new Pair<>(learners, title));
                    }
                }
            }
        }

        courses.sort((a, b) -> b.getKey().compareTo(a.getKey()));

        System.out.println("\rTop " + courseQuantity + " courses on Stepik: ");

        Iterator<Pair<Integer, String>> it = courses.iterator();
        for (int position = 1; position <= courseQuantity && it.hasNext(); position++) {
            Pair<Integer, String> course = it.next();
            System.out.println(position + ". " + course.getValue() + " (" + course.getKey() + " learners)");
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {}

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {}

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}