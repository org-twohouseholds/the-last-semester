package com.extracreditstudios.thelastsemester.shared.messages.game;

import java.util.List;

/** The lobby information displayed by the client. */
public record LobbyDTO(String lobbyId, int maxPlayers, List<LobbyPlayerDTO> players) {
    public LobbyDTO {
        players = List.copyOf(players);
    }
}
