package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.server.db.*;
import com.extracreditstudios.thelastsemester.server.security.*;
import com.extracreditstudios.thelastsemester.shared.messages.ChangePassword;
import com.extracreditstudios.thelastsemester.shared.messages.Delete;
import com.extracreditstudios.thelastsemester.shared.messages.Login;
import com.extracreditstudios.thelastsemester.shared.messages.Protocol;
import com.extracreditstudios.thelastsemester.shared.messages.Register;
import com.extracreditstudios.thelastsemester.shared.messages.game.LobbyDTO;
import com.extracreditstudios.thelastsemester.shared.messages.game.LobbyPlayerDTO;
import com.extracreditstudios.thelastsemester.shared.model.GamePhase;
import com.extracreditstudios.thelastsemester.shared.model.Lobby;
import com.extracreditstudios.thelastsemester.shared.model.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.sql.DataSource;

public class Server {

    private static final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();
    private static final Map<Lobby, ScheduledFuture<?>> lobbyTimers = new ConcurrentHashMap<>();
    private static final long START_LOBBY_AFTER_MILLIS = 60_000;

    private static final AtomicInteger nextLobbyId = new AtomicInteger(0);
    private static final Set<Lobby> lobbies = ConcurrentHashMap.newKeySet();
    private static final Map<String, Player> username2Player = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HikariDataSource ds = setupDB();
        Javalin server = createServer(ds);
        server.start(7070);
    }

    static Javalin createServer(DataSource ds) {
        return createServer(ds, cfg -> {
        });
    }

    // extraConfig lets tests change the setup
    static Javalin createServer(DataSource ds, Consumer<JavalinConfig> extraConfig) {
        Javalin server = Javalin.create(cfg -> {
            cfg.staticFiles.add(sfConfig -> {
                sfConfig.directory = "/static"; // serve src/main/resources/static (index.html)
                sfConfig.location = Location.CLASSPATH;
                sfConfig.hostedPath = "/";
            });
            extraConfig.accept(cfg);
        });
        server.before("/api/*", Server::checkProtocolVersion);
        setupRegister(server, ds);
        setupDelete(server, ds);
        setupLogin(server, ds);
        setupLogout(server, ds);
        setupChangePassword(server, ds);
        setupJoinLobby(server, ds);
        setupQuitLobby(server, ds);
        setupLobbyPolling(server, ds);
        setupMatchPolling(server, ds);
        return server;
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
        if (authorization == null ||
            !authorization.regionMatches(true, 0, bearerPrefix, 0, bearerPrefix.length())) {
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

    private static void setupRegister(Javalin server, DataSource ds) {
        server.post("/api/auth/register", ctx -> {
            Register.Request request = MessageValidator.validateRegisterRequest(ctx);
            String password_hash = Passwords.hash(request.password());
            String session_token = SessionTokens.newToken();
            String session_token_hash = SessionTokens.hash(session_token);
            AccountStore.createAccount(ds, request.username(), password_hash, session_token_hash);
            ctx.json(new Register.Response(session_token));
            ctx.status(201);
        });
    }

    private static void setupDelete(Javalin server, DataSource ds) {
        server.post("/api/account/delete", ctx -> {
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

    private static void setupLogin(Javalin server, DataSource ds) {
        server.post("/api/auth/login", ctx -> {
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

    private static void setupLogout(Javalin server, DataSource ds) {
        server.post("/api/auth/logout", ctx -> {
            String sessionToken = checkAndGetSessionToken(ctx);
            String sessionTokenHash = SessionTokens.hash(sessionToken);
            AccountStore.setSessionTokenNull(ds, sessionTokenHash);
            ctx.status(204);
        });
    }


    private static void setupChangePassword(Javalin server, DataSource ds) {
        server.put("/api/account/password", ctx -> {
            String authorization = ctx.header("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ") ||
                authorization.substring(7).isBlank()) {
                throw new UnauthorizedResponse("Missing or invalid session token");
            }

            String sessionTokenHash = SessionTokens.hash(authorization.substring(7));
            ChangePassword.Request request = MessageValidator.validateChangePasswordRequest(ctx);

            AccountStore.changePassword(ds, sessionTokenHash, request.old_password(),
                request.new_password());

            ctx.status(204);
        });
    }

    private static void setupJoinLobby(Javalin server, DataSource ds) {
        server.post("/api/lobby/join", ctx -> {
            String sessionToken = checkAndGetSessionToken(ctx);
            String sessionTokenHash = SessionTokens.hash(sessionToken);
            Account account = AccountStore.getAccountBySessionTokenHash(ds, sessionTokenHash);
            Player player = username2Player.computeIfAbsent(account.username(), Player::new);
            synchronized (lobbies) {
                if (player.getLobby() != null) {
                    throw new ConflictResponse("Already in a lobby");
                }
                Lobby lobby = lobbies.stream()
                    .filter(l -> l.getPhase() == GamePhase.LOBBY)
                    .filter(l -> l.getPlayerList().size() < l.getMaxPlayers())
                    .findFirst()
                    .orElseGet(Server::createLobby);
                lobby.getPlayerList().add(player);
                player.setLobby(lobby);
                lobby.incrementStateVersion();
                if (lobby.getPlayerList().size() == lobby.getMaxPlayers()) {
                    startLobby(lobby);
                }
            }
            ctx.status(204);
        });
    }

    private static Lobby createLobby() {
        Lobby lobby = new Lobby(String.valueOf(nextLobbyId.getAndAdd(1)), START_LOBBY_AFTER_MILLIS);
        lobbies.add(lobby);
        lobbyTimers.put(lobby, scheduler.schedule(() -> {
            synchronized (lobbies) {
                startLobby(lobby);
            }
        }, START_LOBBY_AFTER_MILLIS, TimeUnit.MILLISECONDS));
        return lobby;
    }

    private static void startLobby(Lobby lobby) {
        if (lobby.getPhase() != GamePhase.LOBBY ||
            !lobbies.contains(lobby)) return;
        ScheduledFuture<?> timer = lobbyTimers.remove(lobby);
        if (timer != null) timer.cancel(false);
        int botNumber = 1;
        while (lobby.getPlayerList().size() < lobby.getMaxPlayers()) {
            Player bot = Player.bot(botNumber++, lobby);
            lobby.getPlayerList().add(bot);
        }
        // TODO: create tournament + first round of matches here
        lobby.setPhase(GamePhase.FIGHT);
        lobby.setDeadlineMillis(null);
        lobby.incrementStateVersion();
    }

    private static void setupQuitLobby(Javalin server, DataSource ds) {
        server.post("/api/lobby/quit", ctx -> {
            String sessionToken = checkAndGetSessionToken(ctx);
            String sessionTokenHash = SessionTokens.hash(sessionToken);
            Account account = AccountStore.getAccountBySessionTokenHash(ds, sessionTokenHash);
            Player player = username2Player.get(account.username());
            synchronized (lobbies) {
                if (player == null || player.getLobby() == null) {
                    throw new ConflictResponse("Not in a lobby");
                }
                Lobby lobby = lobbies.stream()
                    .filter(l -> l.getPlayerList().contains(player))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                        "Player " + player.getUsername() + " is marked inLobby but is in no " +
                            "lobby"));
                if (lobby.getPhase() != GamePhase.LOBBY) {
                    throw new ConflictResponse("Game already started");
                }
                lobby.getPlayerList().remove(player);
                player.setLobby(null);
                lobby.incrementStateVersion();
                if (lobby.getPlayerList().stream().allMatch(Player::isBot)) {
                    ScheduledFuture<?> timer = lobbyTimers.remove(lobby);
                    if (timer != null) timer.cancel(false);
                    lobbies.remove(lobby);
                }
            }
            ctx.status(204);
        });
    }

    private static void setupLobbyPolling(Javalin server, DataSource ds) {
        server.get("/api/lobby", ctx -> {
            String sessionToken = checkAndGetSessionToken(ctx);
            String sessionTokenHash = SessionTokens.hash(sessionToken);
            Account account = AccountStore.getAccountBySessionTokenHash(ds, sessionTokenHash);
            Player player = username2Player.get(account.username());
            Lobby lobby = player.getLobby();
            ctx.json(new LobbyDTO(lobby.getLobbyID(), lobby.getStateVersion(), lobby.getPhase(),
                lobby.getDeadlineMillis(), lobby.getMaxPlayers(), lobby.getPlayerList()
                .stream()
                .map(p -> new LobbyPlayerDTO(p.getUsername(), p.getCharacter().name(), p.isBot()))
                .toList()));
            ctx.status(200);
        });
    }

    private static void setupMatchPolling(Javalin server, DataSource ds) {
        server.get("/api/fight", ctx -> {
            String sessionToken = checkAndGetSessionToken(ctx);
            String sessionTokenHash = SessionTokens.hash(sessionToken);
            Account account = AccountStore.getAccountBySessionTokenHash(ds, sessionTokenHash);
            Player player = username2Player.get(account.username());
            Lobby lobby = player.getLobby();
            // TODO
        });
    }

}
