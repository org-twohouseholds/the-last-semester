package com.extracreditstudios.thelastsemester.shared.model;

import java.util.ArrayList;
import java.util.List;

/** The running state of one fighter: current HP, current energy and equipped perks. */
public class CharacterInstance {

    private static final int MAX_PERKS = 3;

    private final Character character;
    private final List<Ability> perks = new ArrayList<>();
    private int currentHP;
    private int currentEnergy;

    public CharacterInstance(Character character) {
        this.character = character;
        this.currentHP = character.baseHealth();
        this.currentEnergy = character.baseEnergy();
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage must not be negative: " + amount);
        }
        currentHP = Math.max(0, currentHP - amount);
    }

    public void useAbility(Ability ability) {
        if (!canUse(ability)) {
            throw new IllegalArgumentException("Ability cannot be used: " + ability);
        }
        currentEnergy -= ability.energyCost();
    }

    public boolean canUse(Ability ability) {
        return hasAbility(ability) && currentEnergy >= ability.energyCost();
    }

    public void addPerk(Ability perk) {
        if (perks.size() >= MAX_PERKS) {
            throw new IllegalStateException("All perk slots are taken");
        }
        perks.add(perk);
    }

    public boolean isDefeated() {
        return currentHP == 0;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    private boolean hasAbility(Ability ability) {
        return character.basicAttack().equals(ability) || perks.contains(ability);
    }
}
