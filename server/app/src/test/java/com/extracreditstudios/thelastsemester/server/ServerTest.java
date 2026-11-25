package com.extracreditstudios.thelastsemester.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.extracreditstudios.thelastsemester.shared.messages.Protocol;
import io.javalin.Javalin;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ServerTest {

    private static final String LOOPBACK = "127.0.0.1";

    private static Javalin server;
    private static HttpClient client;
    private static String baseUrl;
    private static byte[] indexHtml;

    @BeforeAll
    static void setUp() throws IOException {
        try (InputStream stream = ServerTest.class.getResourceAsStream("/static/index.html")) {
            assertNotNull(stream, "Expected /static/index.html resource on classpath");
            indexHtml = stream.readAllBytes();
        }
        // No test reaches the database, so no DataSource is needed.
        server = startServer(Server.createServer(null));
        baseUrl = urlOf(server);
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void getRootServesStaticHtmlWithoutProtocolVersion() throws Exception {
        HttpResponse<String> response = send(request("GET", "/"));

        assertEquals(200, response.statusCode());
        assertHtml(response);
        assertEquals(new String(indexHtml, StandardCharsets.UTF_8), response.body());
    }

    @Test
    void headRootSendsSameHeadersAsGetWithoutBody() throws Exception {
        HttpResponse<String> response = send(request("HEAD", "/"));

        assertEquals(200, response.statusCode());
        assertHtml(response);
        assertEquals(String.valueOf(indexHtml.length),
            response.headers().firstValue("Content-Length").orElse(""));
        assertEquals("", response.body());
    }

    @Test
    void getRootWithQueryParametersServesStaticHtml() throws Exception {
        HttpResponse<String> response = send(request("GET", "/?source=test"));

        assertEquals(200, response.statusCode());
        assertHtml(response);
        assertTrue(response.body().contains("<!DOCTYPE html>"));
    }

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
        "POST, /api/auth/login",
        "GET, /api/auth/login",
        "HEAD, /api/auth/login",
        "POST, /api/auth/register",
        "POST, /api/auth/logout",
        "PUT, /api/account/password",
        "POST, /api/account/delete",
        "GET, /api/account/delete",
        "POST, /api/character/select",
        "GET, /api/auth/unknown",
    })
    void apiPathsRequireProtocolVersion(String method, String path) throws Exception {
        assertEquals(426, send(request(method, path)).statusCode());
    }

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
        "GET, /api/auth/login",
        "POST, /api/auth/login",
        "POST, /api/character/select",
    })
    void apiPathsRejectWrongProtocolVersion(String method, String path) throws Exception {
        HttpRequest.Builder request = request(method, path).header("protocol-version", "0.0.0");

        assertEquals(426, send(request).statusCode());
    }

    @Test
    void apiPathWithProtocolVersionReachesHandler() throws Exception {
        // An empty body fails validation before the handler touches the database.
        HttpRequest.Builder request = request("POST", "/api/auth/login")
            .header("protocol-version", Protocol.VERSION)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{}"));

        assertEquals(400, send(request).statusCode());
    }

    // The select character tests below fail before the handler touches the database.

    @Test
    void selectCharacterWithoutSessionTokenIsUnauthorized() throws Exception {
        HttpResponse<String> response = send(selectCharacter(null, "{\"character_id\": 2}"));

        assertEquals(401, response.statusCode());
        assertEquals("Bearer", response.headers().firstValue("WWW-Authenticate").orElse(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer ", "Bearer invalid", "Basic abc", "invalid"})
    void selectCharacterWithInvalidSessionTokenIsUnauthorized(String authorization)
        throws Exception {
        HttpRequest.Builder request = selectCharacter(null, "{\"character_id\": 2}")
            .header("Authorization", authorization);

        assertEquals(401, send(request).statusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{}",                                  // missing character_id
        "{\"character_id\": null}",
        "{\"other\": 2}",
        "{\"character_id\": -1}",              // not a selectable character
        "{\"character_id\": 4}",
        "{\"character_id\": 2147483648}",
        "{\"character_id\": 2.5}",             // not an integer
        "{\"character_id\": \"2\"}",
        "{\"character_id\": true}",
        "{\"character_id\": 2, \"other\": 1}", // unexpected field
        "",                                    // invalid JSON
        "{\"character_id\": 2",
        "{\"character_id\": 2} {}",
        "[2]",
        "2",
    })
    void selectCharacterWithInvalidBodyIsBadRequest(String body) throws Exception {
        assertEquals(400, send(selectCharacter(SessionTokens.newToken(), body)).statusCode());
    }

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
        "GET, /favicon.ico",
        "GET, /robots.txt",
        "HEAD, /favicon.ico",
        "POST, /",
        "OPTIONS, /",
        "GET, /auth",
    })
    void nonApiPathsDoNotRequireProtocolVersion(String method, String path) throws Exception {
        assertEquals(404, send(request(method, path)).statusCode());
    }

    @Test
    void contextPathKeepsPageAndProtocolCheckWorking() throws Exception {
        Javalin prefixed = startServer(
            Server.createServer(null, cfg -> cfg.router.contextPath = "/prefix"));
        try {
            String prefixedUrl = urlOf(prefixed) + "/prefix";

            HttpResponse<String> page = send(request(prefixedUrl, "GET", "/"));
            assertEquals(200, page.statusCode());
            assertHtml(page);

            assertEquals(426, send(request(prefixedUrl, "POST", "/api/auth/login")).statusCode());
            assertEquals(404, send(request(prefixedUrl, "GET", "/favicon.ico")).statusCode());
        } finally {
            prefixed.stop();
        }
    }

    private static Javalin startServer(Javalin javalin) {
        // Port 0 picks a free port. Binding to loopback keeps the test server off the network.
        return javalin.start(LOOPBACK, 0);
    }

    private static String urlOf(Javalin javalin) {
        return "http://" + LOOPBACK + ":" + javalin.port();
    }

    private static HttpRequest.Builder request(String method, String path) {
        return request(baseUrl, method, path);
    }

    private static HttpRequest.Builder request(String url, String method, String path) {
        return HttpRequest.newBuilder(URI.create(url + path))
            .method(method, HttpRequest.BodyPublishers.noBody());
    }

    /** Builds a select character request with a valid protocol version. */
    static HttpRequest.Builder selectCharacter(String url, String sessionToken, String body) {
        HttpRequest.Builder request = HttpRequest
            .newBuilder(URI.create(url + "/api/character/select"))
            .header("protocol-version", Protocol.VERSION)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body));
        if (sessionToken != null) {
            request.header("Authorization", "Bearer " + sessionToken);
        }
        return request;
    }

    private static HttpRequest.Builder selectCharacter(String sessionToken, String body) {
        return selectCharacter(baseUrl, sessionToken, body);
    }

    private static HttpResponse<String> send(HttpRequest.Builder request)
        throws IOException, InterruptedException {
        return client.send(request.build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static void assertHtml(HttpResponse<String> response) {
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        assertTrue(contentType.startsWith("text/html"),
            "Expected text/html Content-Type, got: " + contentType);
    }
}
