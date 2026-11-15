package com.extracreditstudios.thelastsemester.battleView;
/**
 * Beschreibt eine Attacke
 * Felder sind final, damit sich mehrere Kämpfer später dieselbe Attacke teilen können
 */
public class Attack {
        public final String name;
        public final int damage;
        public final int manaCost;

        public Attack(String name, int damage, int manaCost) {
            this.name = name;
            this.damage = damage;
            this.manaCost = manaCost;
        }
    }

