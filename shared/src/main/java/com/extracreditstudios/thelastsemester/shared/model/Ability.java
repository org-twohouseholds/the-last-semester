package com.extracreditstudios.thelastsemester.shared.model;

import java.util.List;

/** An action a character can choose in a fight. The higher speed acts first. */
public record Ability(String name, int baseDamage, int energyCost, int speed, List<String> effects) {
    // TODO: Add "String description" and "int cpCost" (both are in the UML).
    // description: a short text, the shop screen shows it later.
    // cpCost: the price in CP in the shop, 0 = not for sale (like the basic attack).
    // Before implementing, adapt all places that call new Ability(...).
}
