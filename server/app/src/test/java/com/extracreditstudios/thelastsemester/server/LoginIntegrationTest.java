

package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.security.Passwords;
import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.extracreditstudios.thelastsemester.shared.messages.Login;
import com.extracreditstudios.thelastsemester.shared.messages.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginIntegrationTest {

    private static Javalin server;
    private static JdbcDataSource ds;
    private static final ObjectMapper JSON = new ObjectMapper();

    private static final String USERNAME = "TestPlayer";
    private static final String PASSWORD = "CorrectPassword123!";

    @BeforeAll
    static void setUp() throws Exception {
        ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:login_test;MODE=MySQL;DB_CLOSE_DELAY=-1");

        try (Connection connection = ds.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE account (
                    username VARCHAR(20) PRIMARY KEY,
                    password_hash VARCHAR(255),
                    session_token_hash VARCHAR(255),
                    selected_character_id INT DEFAULT 0
                )
                """);
        }

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "INSERT INTO account (username, password_hash) VALUES (?, ?)")) {

            ps.setString(1, USERNAME);
            ps.setString(2, Passwords.hash(PASSWORD));
            ps.executeUpdate();
        }

        server = Server.createServer(ds).start("127.0.0.1", 0);
    }

    @BeforeEach
    void resetSession() throws Exception {
        try (Connection connection = ds.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(
                "UPDATE account SET session_token_hash = NULL");
        }
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    private HttpResponse<String> login(String username, String password)
        throws Exception {

        String body = JSON.writeValueAsString(
            new Login.Request(username, password));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:" +
                server.port() + "/api/auth/login"))
            .header("protocol-version", Protocol.VERSION)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        return HttpClient.newHttpClient().send(
            request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void validCredentialsReturnSessionToken() throws Exception {
        HttpResponse<String> response = login(USERNAME, PASSWORD);

        assertEquals(200, response.statusCode());

        String token = JSON.readTree(response.body())
            .path("session_token").asText();

        assertFalse(token.isBlank());

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT session_token_hash FROM account WHERE username = ?")) {

            ps.setString(1, USERNAME);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(
                    SessionTokens.hash(token),
                    rs.getString("session_token_hash")
                );
            }
        }
    }

    @Test
    void wrongPasswordReturnsUnauthorized() throws Exception {
        HttpResponse<String> response =
            login(USERNAME, "WrongPassword123!");

        assertEquals(401, response.statusCode());
    }

    @Test
    void emptyUsernameReturnsBadRequest() throws Exception {
        HttpResponse<String> response = login("", PASSWORD);

        assertEquals(400, response.statusCode());
    }

    @Test
    void wrongPasswordDoesNotCreateSession() throws Exception {
        HttpResponse<String> response =
            login(USERNAME, "WrongPassword123!");

        assertEquals(401, response.statusCode());

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT session_token_hash FROM account WHERE username = ?")) {

            ps.setString(1, USERNAME);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertNull(rs.getString("session_token_hash"));
            }
        }
    }


    @Test
    void failedLoginDoesNotInvalidateExistingSession() throws Exception {
        // First login: create a valid session
        HttpResponse<String> firstLogin = login(USERNAME, PASSWORD);
        assertEquals(200, firstLogin.statusCode());

        String token = JSON.readTree(firstLogin.body())
            .path("session_token").asText();

        assertFalse(token.isBlank());
        String expectedHash = SessionTokens.hash(token);

        // Second login: wrong password
        HttpResponse<String> failedLogin =
            login(USERNAME, "WrongPassword123!");

        assertEquals(401, failedLogin.statusCode());

        // Verify the original session is still stored
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT session_token_hash FROM account WHERE username = ?")) {

            ps.setString(1, USERNAME);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(
                    expectedHash,
                    rs.getString("session_token_hash")
                );
            }
        }
    }

}

