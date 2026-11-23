package com.extracreditstudios.thelastsemester.shared.messages;

import com.extracreditstudios.thelastsemester.shared.messages.game.AbilityDTO;

public class SelectAction {
    public record Request(String tournament, String fight, Integer turn, AbilityDTO ability) {}
}
