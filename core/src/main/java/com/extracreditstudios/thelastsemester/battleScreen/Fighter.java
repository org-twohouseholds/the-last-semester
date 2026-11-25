package com.extracreditstudios.thelastsemester.battleScreen;

// stores a fighters health, energy and available attacks.
//  later: game logic will be responsible for this

public class Fighter {
    public float hp = 100;
    public float energy = 100;
    public final Attack[] attacks;

    public Fighter(Attack[] attacks) {
        this.attacks = attacks;
    }

    public boolean canUse(Attack a) {
        return energy >= a.energyCost;
    }
}

