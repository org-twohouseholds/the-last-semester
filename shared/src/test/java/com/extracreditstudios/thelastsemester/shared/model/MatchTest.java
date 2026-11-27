package com.extracreditstudios.thelastsemester.shared.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.Effect;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectSystem;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectTiming;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Ein Kampf: Reihenfolge, Schadensberechnung und Effekte im Zug.
 * Alle Zahlen sind Beispielwerte. Alice ist immer schneller (Speed 5) als Bob (Speed 3),
 * also greift sie zuerst an, dann sind die erwarteten Werte leicht nachzurechnen.
 */
class MatchTest {

    private final Ability attack = new Ability("Attack", 10, 0, 5, List.of(), "description", 2 );
    private final Ability slowAttack = new Ability("Slow Attack", 10, 0, 3, List.of(), "description", 2 );
    private final EffectSystem effectSystem = new EffectSystem();
    private final static Player enrico = new Player("Enrico");
    private TournamentParticipation alice;
    private TournamentParticipation bob;
    private Match match;

    private static TournamentParticipation newFighter(Ability basicAttack) {
        return new TournamentParticipation(
            new CharacterInstance(new Character("Test", 100, 50, basicAttack)), enrico);
    }

    @BeforeEach
    void setUp() {
        alice = newFighter(attack);
        bob = newFighter(slowAttack);
        match = new Match(alice, bob, effectSystem);
    }

    private void playTurn(Ability aliceAbility, Ability bobAbility) {
        match.submitAbility(alice, aliceAbility);
        match.submitAbility(bob, bobAbility);
        match.resolveTurn();
    }

    private void assertHp(int aliceHp, int bobHp) {
        assertEquals(aliceHp, alice.getCharacter().getCurrentHP());
        assertEquals(bobHp, bob.getCharacter().getCurrentHP());
    }

    // --- Aktionen abgeben ---

    // Pro Zug darf jeder nur eine Aktion abgeben. Die zweite wird abgelehnt.
    @Test
    void anActionIsAcceptedOnlyOncePerTurn() {
        assertTrue(match.submitAbility(alice, attack));
        assertFalse(match.submitAbility(alice, attack));
    }

    // Wer gar nicht im Kampf ist, darf auch nichts abgeben.
    @Test
    void anOutsiderIsRejected() {
        assertFalse(match.submitAbility(newFighter(attack), attack));
    }

    // Der Zug startet erst, wenn beide eine Aktion haben. Vorher gibt es einen Fehler.
    @Test
    void aTurnNeedsBothActions() {
        match.submitAbility(alice, attack);
        assertFalse(match.bothSubmitted());
        assertThrows(IllegalStateException.class, match::resolveTurn);
    }

    // --- Reihenfolge und Ende vom Kampf ---

    // Bob ist schneller und macht Alice mit einem Schlag fertig, also greift sie nicht mehr an.
    // Danach ist der Kampf vorbei, Bob hat gewonnen und es wird nichts mehr angenommen.
    @Test
    void theFasterFighterWinsAndTheDefeatedOneDoesNotAct() {
        Ability finisher = new Ability("Finisher", 100, 5, 9, List.of(), "description", 2 );
        bob.getCharacter().addPerk(finisher);

        playTurn(attack, finisher);

        // Bob hat noch alle HP, weil Alice nicht mehr zum Zug kam
        assertHp(0, 100);
        assertEquals(45, bob.getCharacter().getCurrentEnergy());
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertSame(bob, match.determineWinner());

        // Sonderfall: nach dem Ende wird nichts mehr angenommen
        assertFalse(match.submitAbility(bob, slowAttack));
    }

    // Solange der Kampf läuft, gibt es noch keinen Sieger (Fehler, wenn man fragt).
    @Test
    void thereIsNoWinnerWhileTheFightRuns() {
        assertThrows(IllegalStateException.class, match::determineWinner);
    }

    // --- Schaden: baseDamage * Verstärkung vom Angreifer * Reduktion vom Ziel ---

    // Mit Verstärkung 1,5 macht Alice 15 statt 10 Schaden.
    @Test
    void amplificationRaisesTheDamage() {
        alice.getCharacter().setDmgAmplification(1.5);
        playTurn(attack, slowAttack);
        assertHp(90, 85);
    }

    // Wenn Bob Reduktion 0,5 hat, bekommt er nur 5 statt 10 Schaden.
    @Test
    void reductionLowersTheDamage() {
        bob.getCharacter().setDmgReduction(0.5);
        playTurn(attack, slowAttack);
        assertHp(90, 95);
    }

    // 10 * 1,5 * 0,5 = 7,5. Es wird abgerundet, also 7 Schaden.
    @Test
    void damageWithADecimalResultIsRoundedDown() {
        alice.getCharacter().setDmgAmplification(1.5);
        bob.getCharacter().setDmgReduction(0.5);
        playTurn(attack, slowAttack);
        assertHp(90, 93);
    }

    // --- Effekte im Kampf ---

    // Regen heilt 5 nach jedem Angriff und hält 2 Züge. Danach heilt es nicht mehr.
    @Test
    void anEffectActsExactlyAsLongAsItsDuration() {
        effectSystem.getRegistry().register(new Effect("Regen", "Heilt 5 nach jedem Angriff",
            EffectType.HEAL, EffectTiming.AFTER_ATTACK, 2, 5.0));
        Ability rest = new Ability("Rest", 0, 0, 5, List.of("Regen"), "description", 2 );
        alice.getCharacter().addPerk(rest);

        playTurn(attack, slowAttack);
        assertHp(90, 90);
        playTurn(rest, slowAttack); // Regen startet: 90 + 5 Heilung - 10 Schaden
        assertHp(85, 90);
        playTurn(attack, slowAttack); // Regen heilt ein zweites Mal: 85 + 5 - 10
        assertHp(80, 80);
        playTurn(attack, slowAttack); // Regen ist vorbei, nur noch Schaden
        assertHp(70, 70);
    }
}
