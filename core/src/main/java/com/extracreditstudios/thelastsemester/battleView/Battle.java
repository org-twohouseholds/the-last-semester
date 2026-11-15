package com.extracreditstudios.thelastsemester.battleView;
public class Battle {
        private static final float ROUND_TIME = 10f;

        public final Fighter player;
        public final Fighter enemy;

        public float timeLeft = ROUND_TIME;
        public int playerChoice = -1;

        public Battle(Fighter player, Fighter enemy) {
            this.player = player;
            this.enemy = enemy;
        }

        //Bei 0 wird die Runde ausgewertet und der Countdown startet neu
        public void update(float delta) {
            timeLeft -= delta;
            if (timeLeft <= 0) {
                resolveRound();
            }
        }

        // Speichert den ausgewählten Button
        public void choose(int index) {
            if (index >= 0 && index < player.attacks.length) {
                playerChoice = index;
            }
        }

        private void resolveRound() {

        // null= Spieler hat nichts gewählt
        Attack a = playerChoice >= 0 ? player.attacks[playerChoice] : null;

        //Nur angreifen, wenn etwas gewählt wurde und das Mana reicht
        if (a != null && player.canUse(a)) {
                player.mana -= a.manaCost;
                enemy.hp = Math.max(0, enemy.hp - a.damage);
            }

        //Nächste Runde
        playerChoice = -1;
        timeLeft = ROUND_TIME;

        }

        //true, sobald einer der beiden Kämpfer 0 HP hat
        public boolean isOver() {

        return player.hp <= 0 || enemy.hp <= 0;

        }
    }

