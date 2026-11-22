package com.extracreditstudios.thelastsemester.shared.messages.game;

import java.util.UUID;

/** Public combat values, following the Communication section of the UML. */
public record CombatantDTO(UUID combatantId, String name, int maxHP, int currentHP,
                           int maxEnergy, int currentEnergy) {
}
