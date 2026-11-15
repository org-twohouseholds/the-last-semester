package com.extracreditstudios.thelastsemester.battleView;
public class Fighter {
        public float hp = 100;
        public float mana = 100;
        public final Attack[] attacks;

        public Fighter(Attack[] attacks) {
            this.attacks = attacks;
        }

        public boolean canUse(Attack a) {
            return mana >= a.manaCost;
        }
    }
