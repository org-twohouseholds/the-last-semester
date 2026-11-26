package com.extracreditstudios.thelastsemester.shared.messages.game;

/**
 * Public combat values, following the Communication section of the UML.
 * combatantId is the associated Player UUID encoded as a string.
 */
public record CombatantDTO(String combatantId, String name, int maxHP, int currentHP,
                           int maxEnergy, int currentEnergy) {
}
