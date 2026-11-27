package com.extracreditstudios.thelastsemester.battleScreen;

// manages the battle state, fighters, round timers and attack selection
// battle will later be handled by gamelogic/server
public class Battle {
    private static final float ROUND_TIME = 10f;
    public final Fighter player;
    public final Fighter enemy;
    public float timeLeft = ROUND_TIME;
    public int playerChoice = -1;

    // last executed attack, read by the BattleScreen to play its animation
    public Attack lastAttack;
    public int lastDamage;
    public int attackCount = 0;

    public Battle(Fighter player, Fighter enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    // updates round timer, resolves round when time runs out
    public void update(float delta) {
        timeLeft -= delta;
        if (timeLeft <= 0) {
            resolveRound();
        }
    }
    // stores selected attack if the attack index is valid
    public void choose(int attackIndex) {
        if (attackIndex >= 0 && attackIndex < player.attacks.length) {
            playerChoice = attackIndex;
        }
    }
    private void resolveRound() {

        Attack a = playerChoice >= 0 ? player.attacks[playerChoice] : null;

        if (a != null && player.canUse(a)) {
            player.energy -= a.energyCost;
            float hpBefore = enemy.hp;
            enemy.hp = Math.max(0, enemy.hp - a.damage);

            lastAttack = a;
            lastDamage = Math.round(hpBefore - enemy.hp);
            attackCount++;
        }

        //next round
        playerChoice = -1;
        timeLeft = ROUND_TIME;

    }

    //true, as soon as one fighter has 0 hp
    public boolean isOver() {
        return player.hp <= 0 || enemy.hp <= 0;
    }
}


