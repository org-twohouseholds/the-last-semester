package com.extracreditstudios.thelastsemester.shared.model;

/** The fixed template of a playable character. */
public record Character(String name, int baseHealth, int baseEnergy, Ability basicAttack) {
}
