package com.extracreditstudios.thelastsemester.shared.data;

import com.extracreditstudios.thelastsemester.shared.model.Ability;
import com.extracreditstudios.thelastsemester.shared.model.Character;
import com.extracreditstudios.thelastsemester.shared.model.CharacterInstance;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.ActiveEffect;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.Effect;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectSystem;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectTiming;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectType;
import java.util.List;

/**
 * The hardcoded game content of the first version: characters, the basic attack, the perk pool
 * and the effects behind them. Client and server both read it from here.
 *
 * <p>The character ids follow the order of the character selection screen. Only The Hacker and
 * The Bitter One are implemented so far.
 */
public final class GameContent {

    public static final int HACKER = 0;
    public static final int ESOTERIC = 1;
    public static final int BITTER_ONE = 2;
    public static final int ALCOHOLIC = 3;

    static final String EFFECT_ENERGY_REGEN = "Energy Regeneration";
    static final String EFFECT_SHORTCUTS = "Shortcuts";
    static final String EFFECT_CYNICISM = "Cynicism";
    static final String EFFECT_MENSA_HEAL = "Mensa Special Heal";

    /** Remaining turns of an effect that lasts the whole fight (see ActiveEffect). */
    private static final int WHOLE_FIGHT = -1;

    private static final Ability BASIC_ATTACK = new Ability(
        "Basic Attack", BalanceConfig.BASIC_ATTACK_DAMAGE, 0, BalanceConfig.BASIC_ATTACK_SPEED,
        List.of());

    // TODO: Ability gets "description" and "cpCost" later (see the TODO in Ability). Before
    // implementing, adapt all places that call new Ability(...). These are the values for it:
    //   Basic Attack    0 CP  "A simple hit. It costs no energy, so you can always use it."
    //   Copy & Paste   25 CP  "Copy a classmate's answers. Fast and cheap."
    //   Deadline Panic 50 CP  "Panic before the deadline unleashes a huge hit. Very fast, but
    //                          very expensive."
    //   Mensa Special  35 CP  "Hit slowly, then eat a hot meal to heal yourself."
    //   All-Nighter    40 CP  "Study through the night for a heavy hit. Strong, but slow."

    // Name, damage, energy cost, speed, effects.
    private static final List<Ability> PERKS = List.of(
        new Ability("Copy & Paste", 30, 4, 7, List.of()),
        new Ability("Deadline Panic", 50, 8, 8, List.of()),
        new Ability("Mensa Special", 15, 4, 3, List.of(EFFECT_MENSA_HEAL)),
        new Ability("All-Nighter", 45, 7, 4, List.of()));

    private GameContent() {
    }

    /** The basic attack of every character. It costs no energy, so it can always be chosen. */
    public static Ability basicAttack() {
        return BASIC_ATTACK;
    }

    /** All perks the shop can offer. */
    public static List<Ability> perkPool() {
        return PERKS;
    }

    /** An effect system that knows every effect used by the content below. */
    public static EffectSystem createEffectSystem() {
        EffectSystem effectSystem = new EffectSystem();
        effectSystem.getRegistry().register(new Effect(EFFECT_ENERGY_REGEN,
            "Regain energy at the end of every turn", EffectType.ENERGY_GAIN,
            EffectTiming.AFTER_TURN, WHOLE_FIGHT, BalanceConfig.ENERGY_REGEN_PER_TURN));
        effectSystem.getRegistry().register(new Effect(EFFECT_SHORTCUTS,
            "The Hacker finds shortcuts: extra energy at the end of every turn",
            EffectType.ENERGY_GAIN, EffectTiming.AFTER_TURN, WHOLE_FIGHT,
            BalanceConfig.HACKER_PASSIVE_ENERGY));
        effectSystem.getRegistry().register(new Effect(EFFECT_CYNICISM,
            "The Bitter One lives on spite: regain HP at the end of every turn",
            EffectType.HEAL, EffectTiming.AFTER_TURN, WHOLE_FIGHT,
            BalanceConfig.BITTER_ONE_PASSIVE_HEAL));
        effectSystem.getRegistry().register(new Effect(EFFECT_MENSA_HEAL,
            "Regain HP once after using the perk", EffectType.HEAL, EffectTiming.AFTER_ATTACK, 1,
            BalanceConfig.MENSA_SPECIAL_HEAL));
        return effectSystem;
    }

    /** True if the character with this id exists in the game logic yet. */
    public static boolean isImplemented(int characterId) {
        return characterId == HACKER || characterId == BITTER_ONE;
    }

    /** The fixed template of a character. All characters share HP, energy and basic attack. */
    public static Character character(int characterId) {
        return switch (characterId) {
            case HACKER -> new Character("The Hacker", BalanceConfig.MAX_HP,
                BalanceConfig.MAX_ENERGY, BASIC_ATTACK);
            case BITTER_ONE -> new Character("The Bitter One", BalanceConfig.MAX_HP,
                BalanceConfig.MAX_ENERGY, BASIC_ATTACK);
            default -> throw new IllegalArgumentException(
                "Character is not implemented yet: " + characterId);
        };
    }

    /**
     * A fresh fighter of this character with its passive. The passive is not bought and not
     * chosen: it is an effect that lasts the whole fight. Every fighter also gets the energy
     * regeneration.
     */
    public static CharacterInstance newFighter(int characterId) {
        CharacterInstance fighter = new CharacterInstance(character(characterId));
        fighter.putActiveEffect(new ActiveEffect(EFFECT_ENERGY_REGEN, WHOLE_FIGHT));
        fighter.putActiveEffect(new ActiveEffect(passiveOf(characterId), WHOLE_FIGHT));
        return fighter;
    }

    private static String passiveOf(int characterId) {
        return characterId == HACKER ? EFFECT_SHORTCUTS : EFFECT_CYNICISM;
    }
}
