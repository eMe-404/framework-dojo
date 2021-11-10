package fusheng;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import lombok.SneakyThrows;

@FuShengTest
public class JaxRsIntroduction {
    @SneakyThrows
    public String retrieveRootResource(String resourceName) {
        String url = "http://localhost:8080/widgets/" + resourceName;
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.connect();

        try (final InputStream resultStream = connection.getInputStream()) {
            final Scanner scanner = new Scanner(resultStream);
            return scanner.nextLine();
        }

    }
}
