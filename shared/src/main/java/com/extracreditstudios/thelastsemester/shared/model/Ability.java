package com.extracreditstudios.thelastsemester.shared.model;

/** An action a character can choose in a fight. The higher speed acts first. */
public record Ability(String name, int baseDamage, int energyCost, int speed) {
}
