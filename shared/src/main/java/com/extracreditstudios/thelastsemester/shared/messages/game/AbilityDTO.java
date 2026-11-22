package com.extracreditstudios.thelastsemester.shared.messages.game;

/** Basic ability information for the action buttons, in actionIndex order. */
public record AbilityDTO(String name, int baseDamage, int energyCost, int speed) {
}
