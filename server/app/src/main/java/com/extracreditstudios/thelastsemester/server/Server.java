package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.db.*;
import com.extracreditstudios.thelastsemester.server.security.*;
import com.extracreditstudios.thelastsemester.shared.messages.ChangePassword;
import com.extracreditstudios.thelastsemester.shared.messages.Delete;
import com.extracreditstudios.thelastsemester.shared.messages.Login;
import com.extracreditstudios.thelastsemester.shared.messages.Protocol;
import com.extracreditstudios.thelastsemester.shared.messages.Register;
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
        setupLogin(server, ds);
        setupLogout(server, ds);
        setupChangePassword(server, ds);
        selectAction(server, ds);
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

    private static String checkAndGetSessionToken(Context ctx) {
        // Require the Bearer prefix before extracting the token.
        String authorization = ctx.header("Authorization");
        String bearerPrefix = "Bearer ";
        if (authorization == null || !authorization.regionMatches(true, 0, bearerPrefix, 0, bearerPrefix.length())) {
            ctx.header("WWW-Authenticate", "Bearer");
            throw new UnauthorizedResponse("A valid session token is required");
        }

        String session_token = authorization.substring(bearerPrefix.length()).strip();
        if (!MessageValidator.validateSessionToken(session_token)) {
            ctx.header("WWW-Authenticate", "Bearer");
            throw new UnauthorizedResponse("Invalid session token");
        }
        return session_token;
    }

    private static void setupRegister(Javalin server, HikariDataSource ds) {
        server.post("/auth/register", ctx -> {
            Register.Request request = MessageValidator.validateRegisterRequest(ctx);
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
            String session_token = checkAndGetSessionToken(ctx);
            String session_token_hash = SessionTokens.hash(session_token);
            String password_hash = AccountStore.findPasswordHash(ds, session_token_hash);
            if (password_hash == null) {
                ctx.header("WWW-Authenticate", "Bearer");
                throw new UnauthorizedResponse("A valid session token is required");
            }

            Delete.Request request = MessageValidator.validateDeleteRequest(ctx);

            if (!Passwords.verify(password_hash, request.password())) {
                throw new ForbiddenResponse("Incorrect password");
            }
            AccountStore.deleteAccount(ds, session_token_hash, password_hash);
        });
    }

    private static void setupLogin(Javalin server, HikariDataSource ds) {
        server.post("/auth/login", ctx -> {
            Login.Request request = MessageValidator.validateLoginRequest(ctx);
            Account account = AccountStore.getAccount(ds, request.username());
            String password = request.password();
            boolean isVerified = Passwords.verify(account.password_hash(), password);
            if (isVerified) {
                String session_token = SessionTokens.newToken();
                String session_token_hash = SessionTokens.hash(session_token);
                AccountStore.updateSessionToken(ds, account.username(), session_token_hash);
                ctx.json(new Login.Response(session_token));
                ctx.status(200);
            } else {
                throw new HttpResponseException(401, "Invalid password");
            }
        });
    }

    private static void setupLogout(Javalin server, HikariDataSource ds) {
        server.post("/auth/logout", ctx -> {
            String session_token = checkAndGetSessionToken(ctx);
            String session_token_hash = SessionTokens.hash(session_token);
            AccountStore.setSessionTokenNull(ds, session_token_hash);
            ctx.status(204);
        });
    }


    private static void setupChangePassword(Javalin server, HikariDataSource ds) {
        server.put("/account/password", ctx -> {
            String authorization = ctx.header("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ") || authorization.substring(7).isBlank()) {
                throw new UnauthorizedResponse("Missing or invalid session token");
            }

            String sessionTokenHash = SessionTokens.hash(authorization.substring(7));
            ChangePassword.Request request = MessageValidator.validateChangePasswordRequest(ctx);

            AccountStore.changePassword(ds, sessionTokenHash, request.old_password(), request.new_password());

            ctx.status(204);
        });
    }

    private static void selectAction(Javalin server, HikariDataSource ds) {
        server.post("/fight/select", ctx -> {
            throw new HttpResponseException(501, "Select Action is not implemented yet");
        });
    }

}
