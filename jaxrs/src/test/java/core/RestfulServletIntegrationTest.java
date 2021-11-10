package core;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import examples.entity.Widget;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestfulServletIntegrationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void should_return_200_status_given_request_matching_succeed_and_response_generated() {
        String url = "http://localhost:8080/widgets/1";
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.connect();

        assertThat(connection.getResponseCode()).isEqualTo(OK.getStatusCode());
    }

    @Test
    @SneakyThrows
    void should_throw_404_given_unknown_resource_path() {
        String url = "http://localhost:8080/notKnown";
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.connect();

        assertThat(connection.getResponseCode()).isEqualTo(NOT_FOUND.getStatusCode());
    }

    @Test
    @SneakyThrows
    void should_return_root_resources_given_root_resource_request() {
        String url = "http://localhost:8080/widgets";
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.connect();

        try (final InputStream resultStream = connection.getInputStream()) {
            final Scanner scanner = new Scanner(resultStream);
            final String resultResponse = scanner.nextLine();
            final List<Widget> responseWidgets = objectMapper.readValue(resultResponse, new TypeReference<>() {
            });

            assertThat(responseWidgets).extracting("id").contains("1", "2", "3");
        }

    }

    @Test
    @SneakyThrows
    void should_return_sub_resource_given_request_with_resource_identifier() {
        final String subResourceIdentifier = "1";
        String url = "http://localhost:8080/widgets/" + subResourceIdentifier;
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.connect();

        try (final InputStream resultStream = connection.getInputStream()) {
            final Scanner scanner = new Scanner(resultStream);
            final String resultResponse = scanner.nextLine();
            final Widget widgetResource = objectMapper.readValue(resultResponse, Widget.class);

            assertThat(widgetResource.getId()).isEqualTo(subResourceIdentifier);
            assertThat(widgetResource.getName()).isEqualTo("first widget");
        }

    }

    @Test
    @SneakyThrows
    void should_store_new_resource_and_return_id_given_post_request_with_body() {
        String url = "http://localhost:8080/widgets/";
        String expectedWidgetName = "new added widget 1";
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        sendPostRequest(expectedWidgetName, connection);

        Widget responseWidget = extractResponse(connection);

        assertThat(responseWidget.getId()).isNotNull();
        assertThat(responseWidget.getName()).isEqualTo(expectedWidgetName);

    }

    private Widget extractResponse(HttpURLConnection connection) throws IOException {
        Widget responseWidget;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            responseWidget = objectMapper.readValue(response.toString(), Widget.class);
        }
        return responseWidget;
    }

    private void sendPostRequest(String expectedWidgetName, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        String newWidget = "{\"name\":\"" + expectedWidgetName + "\"}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = newWidget.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }
}