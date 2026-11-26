package com.extracreditstudios.thelastsemester.shared.messages.game;

/** Public lobby information; characterId is null before character selection. */
public record LobbyPlayerDTO(String playerId, String username, Integer characterId, boolean bot) {
}
