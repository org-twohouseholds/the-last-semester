package com.extracreditstudios.thelastsemester.server.state;

import com.extracreditstudios.thelastsemester.shared.messages.game.GamePhase;
import com.extracreditstudios.thelastsemester.shared.model.Tournament;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Simple server-side container. The HTTP handlers will synchronize access to this state. */
public class LobbyState {
    public final String lobbyId;
    public final int maxPlayers;
    public long stateVersion;
    public GamePhase phase = GamePhase.LOBBY;
    public Long deadlineMillis;
    public Tournament tournament;
    public final Map<UUID, PlayerState> players = new LinkedHashMap<>();
    public final Map<UUID, FightState> fights = new LinkedHashMap<>();

    public LobbyState(String lobbyId, int maxPlayers) {
        if (maxPlayers < 1) {
            throw new IllegalArgumentException("maxPlayers must be positive");
        }
        this.lobbyId = lobbyId;
        this.maxPlayers = maxPlayers;
    }

    public boolean addPlayer(PlayerState player) {
        if (phase != GamePhase.LOBBY || players.containsKey(player.playerId) || players.size() >= maxPlayers) {
            return false;
        }
        players.put(player.playerId, player);
        markChanged();
        return true;
    }

    public boolean removePlayer(UUID playerId) {
        if (phase != GamePhase.LOBBY || players.remove(playerId) == null) {
            return false;
        }
        markChanged();
        return true;
    }

    /** Call after other changes, e.g. selecting a character, changing phase or updating a fight. */
    public void markChanged() {
        stateVersion++;
    }
}
