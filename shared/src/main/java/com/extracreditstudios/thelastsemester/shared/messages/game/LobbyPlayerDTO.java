package com.extracreditstudios.thelastsemester.shared.messages.game;

/** Public lobby information; characterId is null before character selection. */
public record LobbyPlayerDTO(String username, String characterName, boolean bot) {
}
