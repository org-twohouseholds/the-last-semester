package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.db.Account;
import com.extracreditstudios.thelastsemester.server.db.AccountStore;
import com.extracreditstudios.thelastsemester.server.security.Passwords;
import com.extracreditstudios.thelastsemester.server.security.SessionTokens;
import com.extracreditstudios.thelastsemester.server.shared.Protocol;
import com.extracreditstudios.thelastsemester.server.shared.messages.Login;
import com.extracreditstudios.thelastsemester.server.shared.messages.Logout;
import com.extracreditstudios.thelastsemester.server.shared.messages.Register;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {

    public static void main(String[] args) throws IOException {
        HikariDataSource ds = setupDB();
        Javalin server = Javalin.create();
        server.before(Server::checkProtocolVersion);
        setupRegister(server, ds);
        setupLogin(server, ds);
        setupLogout(server, ds);
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

    //Comment for Review:
    // Wouldn't it be more readable to do everything(also setupRegister) via an Account (need to pass fewer parameters into the methods)?
    private static void setupLogin(Javalin server, HikariDataSource ds) {
        server.post("/auth/login", ctx -> {
            Login.Request request = Login.validateRequest(ctx);
            Account account = AccountStore.getAccount(ds, request.username());
            String password = request.password();
            boolean isVerified = Passwords.verify(account.password_hash(), password);
            if (isVerified) {
                String session_token = SessionTokens.newToken();
                String session_token_hash = SessionTokens.hash(session_token);
                AccountStore.updateSessionToken(ds, account.username(), session_token_hash);
                ctx.json(new Login.Response(session_token));
                ctx.status(200);
            }
            else {
                throw new HttpResponseException(401, "Invalid password");
            }
        });
    }

    private static void setupLogout(Javalin server, HikariDataSource ds) {
        server.post("/auth/logout", ctx -> {
            Logout.Request request = Logout.validateRequest(ctx);
            String session_token = request.session_token();
            String session_token_hash = SessionTokens.hash(session_token);
            AccountStore.setSessionTokenNull(ds, session_token_hash);
            ctx.status(204);
        });
    }

}
