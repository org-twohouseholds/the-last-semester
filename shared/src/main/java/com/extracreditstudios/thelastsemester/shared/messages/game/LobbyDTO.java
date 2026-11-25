package com.extracreditstudios.thelastsemester.shared.messages.game;

import com.extracreditstudios.thelastsemester.shared.model.GamePhase;

import java.util.List;

/**
 * The lobby information displayed by the client.
 */
public record LobbyDTO(String lobbyId, long stateVersion, GamePhase phase, Long deadlineMillis,
                       int maxPlayers, List<LobbyPlayerDTO> players) {
    public LobbyDTO {
        players = List.copyOf(players);
    }
}
