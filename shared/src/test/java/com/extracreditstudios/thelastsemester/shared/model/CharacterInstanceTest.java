package com.extracreditstudios.thelastsemester.shared.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.ActiveEffect;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Ein Kämpfer allein: HP, Energie, Fähigkeiten und laufende Effekte. */
class CharacterInstanceTest {

    private final Ability attack = new Ability("Attack", 10, 0, 5, List.of());
    private final EffectRegistry registry = new EffectRegistry();
    private CharacterInstance instance;

    @BeforeEach
    void setUp() {
        instance = new CharacterInstance(new Character("Test", 100, 50, attack));
    }

    // --- HP ---

    // Ein neuer Kämpfer startet mit vollen HP und voller Energie und lebt noch.
    @Test
    void startsWithFullHpAndEnergy() {
        assertEquals(100, instance.getCurrentHP());
        assertEquals(50, instance.getCurrentEnergy());
        assertFalse(instance.isDefeated());
    }

    // Schaden zieht HP ab. Bei zu viel Schaden bleibt es bei 0 (nie minus), dann ist er besiegt.
    @Test
    void damageReducesHpButNeverBelowZero() {
        instance.takeDamage(30);
        assertEquals(70, instance.getCurrentHP());

        // Sonderfall: viel mehr Schaden als HP übrig
        instance.takeDamage(500);
        assertEquals(0, instance.getCurrentHP());
        assertTrue(instance.isDefeated());
    }

    // Heilung gibt HP zurück, aber nie mehr als die maximalen HP.
    @Test
    void healIsCappedAtMaxHp() {
        instance.takeDamage(20);
        instance.flatHeal(5);
        assertEquals(85, instance.getCurrentHP());

        // Sonderfall: Heilung ist größer als das, was fehlt
        instance.flatHeal(50);
        assertEquals(100, instance.getCurrentHP());
    }

    // Ein toter Kämpfer bleibt tot, egal wie groß die Heilung ist.
    @Test
    void aDefeatedCharacterIsNotRevivedByHealing() {
        instance.takeDamage(100);

        instance.flatHeal(10);
        assertTrue(instance.isDefeated());

        // Sonderfall: sehr große Heilung
        instance.flatHeal(500);
        assertTrue(instance.isDefeated());
    }

    // --- Energie ---

    // Eine Fähigkeit benutzen kostet Energie (50 - 20 = 30).
    @Test
    void usingAnAbilityPaysItsEnergy() {
        Ability perk = new Ability("Perk", 5, 20, 5, List.of());
        instance.addPerk(perk);
        instance.useAbility(perk, registry);
        assertEquals(30, instance.getCurrentEnergy());
    }

    // Wenn die Energie nicht reicht, darf man die Fähigkeit nicht benutzen (Fehler).
    @Test
    void anAbilityWithoutEnoughEnergyCannotBeUsed() {
        Ability expensive = new Ability("Expensive", 5, 51, 5, List.of());
        instance.addPerk(expensive);
        assertFalse(instance.canUse(expensive));
        assertThrows(IllegalArgumentException.class,
            () -> instance.useAbility(expensive, registry));
    }

    // Energie-Gewinn gibt Energie, aber nie mehr als das Maximum. Die HP bleiben gleich.
    @Test
    void energyGainIsCappedAtMaxEnergy() {
        Ability perk = new Ability("Perk", 5, 20, 5, List.of());
        instance.addPerk(perk);
        instance.useAbility(perk, registry); // Energie 50 -> 30

        instance.flatEnergyGain(5);
        assertEquals(35, instance.getCurrentEnergy());
        assertEquals(100, instance.getCurrentHP());

        // Sonderfall: Gewinn ist größer als das, was fehlt
        instance.flatEnergyGain(100);
        assertEquals(50, instance.getCurrentEnergy());
    }

    // --- Laufende Effekte ---

    // Ein Effekt mit Dauer 2 verschwindet nach 2 Runden. Die Passive (-1) bleibt immer.
    @Test
    void anEffectEndsAfterItsDurationButAPassiveStays() {
        instance.putActiveEffect(new ActiveEffect("Buff", 2));
        instance.putActiveEffect(new ActiveEffect("Passive", -1)); // -1 = ganzer Kampf

        instance.tickActiveEffects();
        assertEquals(2, instance.getActiveEffects().size());

        instance.tickActiveEffects(); // Buff ist jetzt bei 0 und wird entfernt
        assertEquals(1, instance.getActiveEffects().size());
        assertEquals("Passive", instance.getActiveEffects().get(0).getEffectName());
    }
}
