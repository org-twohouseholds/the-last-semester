package com.extracreditstudios.thelastsemester.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.extracreditstudios.thelastsemester.server.db.AccountStore;
import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.MountableFile;

/** Runs against a throwaway MySQL container. Skipped when Docker is not available. */
class SelectCharacterDbTest {

    private static final String LOOPBACK = "127.0.0.1";

    private static MySQLContainer mysql;
    private static HikariDataSource ds;
    private static Javalin server;
    private static HttpClient client;
    private static String baseUrl;

    @BeforeAll
    static void setUp() {
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(),
            "Docker is not available");
        // Same schema and MySQL version as docker-compose.yml.
        mysql = new MySQLContainer("mysql:8.4")
            .withDatabaseName("the_last_semester")
            .withCopyFileToContainer(
                MountableFile.forHostPath(Path.of("../db/init.sql").toAbsolutePath()),
                "/docker-entrypoint-initdb.d/init.sql");
        mysql.start();

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(mysql.getJdbcUrl());
        cfg.setUsername(mysql.getUsername());
        cfg.setPassword(mysql.getPassword());
        ds = new HikariDataSource(cfg);

        server = Server.createServer(ds).start(LOOPBACK, 0);
        baseUrl = "http://" + LOOPBACK + ":" + server.port();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
        if (ds != null) {
            ds.close();
        }
        if (mysql != null) {
            mysql.stop();
        }
    }

    @BeforeEach
    void clearAccounts() throws SQLException {
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM account");
        }
    }

    @Test
    void selectCharacterStoresSelection() throws Exception {
        String token = createAccount("alice");

        assertEquals(204, selectCharacter(token, 2).statusCode());
        assertEquals(2, selectedCharacterId("alice"));
    }

    @Test
    void selectCharacterOnlyUpdatesAccountOfSessionToken() throws Exception {
        String aliceToken = createAccount("alice");
        String bobToken = createAccount("bob");
        assertEquals(204, selectCharacter(bobToken, 1).statusCode());

        assertEquals(204, selectCharacter(aliceToken, 3).statusCode());

        assertEquals(3, selectedCharacterId("alice"));
        assertEquals(1, selectedCharacterId("bob"));
    }

    @Test
    void selectCharacterAgainReplacesPreviousSelection() throws Exception {
        String token = createAccount("alice");
        assertEquals(204, selectCharacter(token, 1).statusCode());

        assertEquals(204, selectCharacter(token, 3).statusCode());
        assertEquals(3, selectedCharacterId("alice"));

        // Selecting the same character again still succeeds.
        assertEquals(204, selectCharacter(token, 3).statusCode());
        assertEquals(3, selectedCharacterId("alice"));
    }

    @Test
    void selectCharacterWithUnknownSessionTokenIsUnauthorized() throws Exception {
        createAccount("alice");

        HttpResponse<String> response = selectCharacter(SessionTokens.newToken(), 2);

        assertEquals(401, response.statusCode());
        assertEquals("Bearer", response.headers().firstValue("WWW-Authenticate").orElse(""));
        assertNull(selectedCharacterId("alice"));
    }

    @Test
    void selectCharacterAfterLogoutIsUnauthorized() throws Exception {
        String token = createAccount("alice");
        AccountStore.setSessionTokenNull(ds, SessionTokens.hash(token));

        assertEquals(401, selectCharacter(token, 2).statusCode());
        assertNull(selectedCharacterId("alice"));
    }

    @Test
    void selectCharacterWithInvalidCharacterIdKeepsPreviousSelection() throws Exception {
        String token = createAccount("alice");
        assertEquals(204, selectCharacter(token, 1).statusCode());

        assertEquals(400, selectCharacter(token, 99).statusCode());
        assertEquals(1, selectedCharacterId("alice"));
    }

    /** Creates an account directly in the database and returns its session token. */
    private static String createAccount(String username) throws SQLException {
        String token = SessionTokens.newToken();
        // The password hash is never checked here, so skip the slow Argon2 hashing.
        AccountStore.createAccount(ds, username, "unused", SessionTokens.hash(token));
        return token;
    }

    private static Integer selectedCharacterId(String username) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT selected_character_id FROM account WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getObject("selected_character_id", Integer.class);
            }
        }
    }

    private static HttpResponse<String> selectCharacter(String token, int characterId)
        throws IOException, InterruptedException {
        HttpRequest.Builder request = ServerTest.selectCharacter(baseUrl, token,
            "{\"character_id\": " + characterId + "}");
        return client.send(request.build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
}
