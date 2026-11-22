package com.extracreditstudios.thelastsemester.server.state;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** All lobbies/tournaments held in RAM. Entries disappear when the server restarts. */
public class GameStateStore {
    private final Map<String, LobbyState> lobbies = new LinkedHashMap<>();

    public LobbyState createLobby(int maxPlayers) {
        String id = UUID.randomUUID().toString();
        LobbyState lobby = new LobbyState(id, maxPlayers);
        lobbies.put(id, lobby);
        return lobby;
    }

    /** Returns null if the lobby is unknown. */
    public LobbyState getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public void removeLobby(String lobbyId) {
        lobbies.remove(lobbyId);
    }
}
