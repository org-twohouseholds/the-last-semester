package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.security.Passwords;
import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.extracreditstudios.thelastsemester.server.shared.Protocol;
import com.extracreditstudios.thelastsemester.server.shared.messages.Register;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.javalin.Javalin;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;

public class Server {

    public static void main(String[] args) throws IOException {
        HikariDataSource ds = setupDB();
        Javalin server = Javalin.create();
        server.before(Server::checkProtocolVersion);
        setupRegister(server, ds);
        server.start(7070);
    }

    private static HikariDataSource setupDB() throws IOException {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://db:3306/the_last_semester");
        cfg.setUsername("user");
        cfg.setPassword(Files.readString(Path.of("/run/secrets/db_pw")).strip());
        return new HikariDataSource(cfg);
    }

    private static void checkProtocolVersion(Context ctx) {
        String protocolVersion = ctx.header("protocol-version");
        if (protocolVersion == null || !protocolVersion.equals(Protocol.VERSION)) {
            throw new HttpResponseException(426, "Please update your game");
        }
    }

    private static void setupRegister(Javalin server, HikariDataSource ds) {
        server.post("/auth/register", ctx -> {
            Register.Request request = ctx.bodyValidator(Register.Request.class)
                .check(r -> r.username() != null && r.username().matches("[A-Za-z0-9_]{3,20}"),
                    "Username must be 3-20 characters: letters, digits, _")
                .check(r -> r.password() != null && r.password().length() >= 15 &&
                    r.password().length() <= 128, "Password must be 15-128 characters")
                .get();
            String password_hash = Passwords.hash(request.password());
            String session_token = SessionTokens.newToken();
            String session_token_hash = SessionTokens.hash(session_token);
            try (Connection c = ds.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO account (username, password_hash, session_token_hash) VALUES " +
                         "(?, ?, ?)")) {
                ps.setString(1, request.username());
                ps.setString(2, password_hash);
                ps.setString(3, session_token_hash);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                if (e.getErrorCode() == 1062) { // duplicate key
                    if (e.getMessage().contains("uq_account_username")) {
                        throw new ConflictResponse("Username already taken");
                    }
                }
                throw e; // session token collision (very unlikely) only fails one request
            }
            ctx.json(new Register.Response(session_token));
            ctx.status(201);
        });
    }

}
