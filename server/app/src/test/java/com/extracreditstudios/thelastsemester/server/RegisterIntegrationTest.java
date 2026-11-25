
package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.security.Passwords;
import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.extracreditstudios.thelastsemester.shared.messages.Register;
import com.extracreditstudios.thelastsemester.shared.messages.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class RegisterIntegrationTest {

    private static Javalin server;
    private static JdbcDataSource ds;
    private static final ObjectMapper JSON = new ObjectMapper();

    private static final String USERNAME = "TestPlayer";
    private static final String PASSWORD = "CorrectPassword123!";

    @BeforeAll
    static void setUp() throws Exception {
        ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:register_test;MODE=MySQL;DB_CLOSE_DELAY=-1");

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

        server = Server.createServer(ds).start("127.0.0.1", 0);
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        try (Connection connection = ds.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM account");
        }
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    private HttpResponse<String> register(String username, String password)
        throws Exception {

        String body = JSON.writeValueAsString(
            new Register.Request(username, password));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:" +
                server.port() + "/api/auth/register"))
            .header("protocol-version", Protocol.VERSION)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        return HttpClient.newHttpClient().send(
            request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void validRegistrationCreatesAccount() throws Exception {
        HttpResponse<String> response = register(USERNAME, PASSWORD);

        assertEquals(201, response.statusCode());

        String token = JSON.readTree(response.body())
            .path("session_token").asText();

        assertFalse(token.isBlank());

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT password_hash, session_token_hash "
                     + "FROM account WHERE username = ?")) {

            ps.setString(1, USERNAME);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());

                String storedPassword = rs.getString("password_hash");
                String storedToken = rs.getString("session_token_hash");

                // Password must be hashed, not stored as plaintext
                assertNotEquals(PASSWORD, storedPassword);
                assertTrue(storedPassword.startsWith("$argon2id$"));
                assertTrue(Passwords.verify(storedPassword, PASSWORD));

                // Only the session token hash is stored
                assertEquals(SessionTokens.hash(token), storedToken);
                assertNotEquals(token, storedToken);
            }
        }
    }


    @Test
    void shortUsernameReturnsBadRequest() throws Exception {
        HttpResponse<String> response =
            register("ab", PASSWORD);

        assertEquals(400, response.statusCode());
    }

    @Test
    void shortPasswordReturnsBadRequest() throws Exception {
        HttpResponse<String> response =
            register(USERNAME, "12345");

        assertEquals(400, response.statusCode());
    }


    @Test
    void invalidRegistrationDoesNotCreateAccount() throws Exception {
        HttpResponse<String> response =
            register("invalid-name!", PASSWORD);

        assertEquals(400, response.statusCode());

        try (Connection connection = ds.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM account")) {

            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }
    }


    @Test
    void duplicateUsernameDoesNotOverwriteAccount() throws Exception {
        // First registration
        HttpResponse<String> first = register(USERNAME, PASSWORD);
        assertEquals(201, first.statusCode());

        // Attempt to register the same username again
        HttpResponse<String> second =
            register(USERNAME, "AnotherPassword123!");

        assertNotEquals(201, second.statusCode());

        // Verify that the original account still exists
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT password_hash FROM account WHERE username = ?")) {

            ps.setString(1, USERNAME);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());

                String storedHash = rs.getString("password_hash");

                assertTrue(Passwords.verify(storedHash, PASSWORD));
                assertFalse(Passwords.verify(
                    storedHash, "AnotherPassword123!"));
                assertFalse(rs.next());
            }
        }
    }

}

