package com.extracreditstudios.thelastsemester.shared.messages;

import com.extracreditstudios.thelastsemester.shared.messages.game.AbilityDTO;

public class SelectAction {
    public record Request(String lobbyId, String matchId, Integer turn, AbilityDTO ability) {}
}
