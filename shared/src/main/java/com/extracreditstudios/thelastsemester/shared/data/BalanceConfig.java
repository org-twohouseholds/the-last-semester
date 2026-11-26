package com.extracreditstudios.thelastsemester.shared.data;

/**
 * Balancing numbers of the first game version that apply to every character, plus the values of
 * the passive and perk effects. Damage, energy cost and speed of each perk are in the perk
 * table of GameContent.
 *
 * <p>These are start values from the Game Logic team, not final rules. Everything is balanced
 * for {@value #MAX_HP} life points, so damage and healing can be read as percent of the HP. If
 * the HP change, all damage and healing values must change by the same factor. The perk names
 * and CP prices come from the UI prototype (ShopScreen). The values were tuned with
 * BalanceSimulationTest until fights were neither endless nor over after a few turns and no
 * character or perk was clearly better.
 */
public final class BalanceConfig {

    /** Life points of every character. */
    public static final int MAX_HP = 100;

    /** Size of the energy bar of every character (combat rules: bar of 5 to 10). */
    public static final int MAX_ENERGY = 10;

    /** Energy every fighter regains at the end of each turn (combat rules: 1 to 2). */
    public static final int ENERGY_REGEN_PER_TURN = 1;

    /** The basic attack is the same for every character and costs no energy. */
    public static final int BASIC_ATTACK_DAMAGE = 15;

    /** Speed of the basic attack. */
    public static final int BASIC_ATTACK_SPEED = 5;

    /** Passive of The Hacker: extra energy at the end of each turn (on top of the regeneration). */
    public static final int HACKER_PASSIVE_ENERGY = 1;

    /** Passive of The Bitter One: HP regained at the end of each turn. */
    public static final int BITTER_ONE_PASSIVE_HEAL = 4;

    /** Perk Mensa Special: HP regained once when the perk is used. */
    public static final int MENSA_SPECIAL_HEAL = 22;

    private BalanceConfig() {
    }
}
