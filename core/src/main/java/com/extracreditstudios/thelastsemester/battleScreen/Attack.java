package com.extracreditstudios.thelastsemester.battleScreen;

//attack and its basic information
// may be removed if attack data is handled directly by game/logic server

public class Attack {
    public final String name;
    public final int damage;
    public final int energyCost;

    public Attack(String name, int damage, int energyCost) {
        this.name = name;
        this.damage = damage;
        this.energyCost = energyCost;
    }
}
