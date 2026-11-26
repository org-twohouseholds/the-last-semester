package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.messages.game.AbilityDTO;
import java.util.List;

/** An action a character can choose in a fight. The higher speed acts first. */
public record Ability(String name, int baseDamage, int energyCost, int speed, List<String> effects) {
    public AbilityDTO toDTO() {
        return new AbilityDTO(name, baseDamage, energyCost, speed);
    }
}
