package com.extracreditstudios.thelastsemester.shared.messages.game;

/** Response for game polling. Depending on the phase, lobby or fight may be null. */
public record GameStateDTO(String tournamentId, GamePhase phase, long stateVersion,
                           Long deadlineMillis, LobbyDTO lobby, MatchDTO fight) {
}
