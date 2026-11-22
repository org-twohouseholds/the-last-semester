package com.extracreditstudios.thelastsemester.shared.messages.game;

import com.extracreditstudios.thelastsemester.shared.model.MatchStatus;
import java.util.List;
import java.util.UUID;

/**
 * Fight information from one player's perspective, following the UML.
 * Only that player's selected ability is included; the opponent has a readiness flag.
 * Deadlines are nullable Unix epoch milliseconds when no timer has been set.
 */
public record MatchDTO(String matchId, long stateVersion, MatchStatus status, int turnNumber,
                       long serverTimeMillis, Long turnDeadlineMillis, Long matchDeadlineMillis,
                       CombatantDTO self, CombatantDTO opponent, List<AbilityDTO> myAbilities,
                       AbilityDTO mySubmittedAbility, boolean opponentHasSubmitted,
                       List<TurnEventDTO> lastTurnEvents, String winnerId) {
    public MatchDTO {
        myAbilities = List.copyOf(myAbilities);
        lastTurnEvents = List.copyOf(lastTurnEvents);
    }
}
