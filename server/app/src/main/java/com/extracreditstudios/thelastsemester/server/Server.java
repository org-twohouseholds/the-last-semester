package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.db.AccountStore;
import com.extracreditstudios.thelastsemester.server.security.Passwords;
import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.extracreditstudios.thelastsemester.server.shared.Protocol;
import com.extracreditstudios.thelastsemester.server.shared.messages.Delete;
import com.extracreditstudios.thelastsemester.server.shared.messages.Register;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import io.javalin.http.UnauthorizedResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {

    public static void main(String[] args) throws IOException {
        HikariDataSource ds = setupDB();
        Javalin server = Javalin.create();
        server.before(Server::checkProtocolVersion);
        setupRegister(server, ds);
        setupDelete(server, ds);
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
            Register.Request request = Register.validateRequest(ctx);
            String password_hash = Passwords.hash(request.password());
            String session_token = SessionTokens.newToken();
            String session_token_hash = SessionTokens.hash(session_token);
            AccountStore.createAccount(ds, request.username(), password_hash, session_token_hash);
            ctx.json(new Register.Response(session_token));
            ctx.status(201);
        });
    }

    private static void setupDelete(Javalin server, HikariDataSource ds) {
        server.post("/account/delete", ctx -> {
            Delete.Request request = Delete.validateRequest(ctx);
            String authorization = ctx.header("Authorization");
            String bearerPrefix = "Bearer ";
            if (authorization == null || !authorization.regionMatches(
                true, 0, bearerPrefix, 0, bearerPrefix.length())) {
                ctx.header("WWW-Authenticate", "Bearer");
                throw new UnauthorizedResponse("A valid session token is required");
            }

            String session_token = authorization.substring(bearerPrefix.length()).strip();
            if (session_token.isEmpty()) {
                ctx.header("WWW-Authenticate", "Bearer");
                throw new UnauthorizedResponse("A valid session token is required");
            }

            String session_token_hash = SessionTokens.hash(session_token);
            String password_hash = AccountStore.findPasswordHash(ds, session_token_hash);
            if (password_hash == null) {
                ctx.header("WWW-Authenticate", "Bearer");
                throw new UnauthorizedResponse("A valid session token is required");
            }
            if (!Passwords.verify(password_hash, request.password())) {
                throw new ForbiddenResponse("Incorrect password");
            }
            AccountStore.deleteAccount(ds, session_token_hash, password_hash);
            ctx.status(204);
        });
    }

}
