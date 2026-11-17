package com.extracreditstudios.thelastsemester.shared.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.Effect;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectSystem;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectTiming;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * One complete fight, calculated by hand first. Only the two character names come from the PID;
 * ability names and all numbers are a proposal for the team.
 */
class MatchPlaythroughTest {

    private final EffectSystem effectSystem = new EffectSystem();
    private final Effect simpleHealEffect = new Effect("Basic Heal", "Dieser Effekt heilt dich einmalig um 8 Leben",EffectType.HEAL, EffectTiming.AFTER_ATTACK, 1, 8.0);
    private final Ability simpleHealAbility = new Ability("Bier Trinken", 0, 0, 9, List.of(simpleHealEffect.getName()));

    private final Ability tippen = new Ability("Tippen", 10, 0, 6, List.of());
    private final Ability exploit = new Ability("Exploit", 25, 15, 8, List.of());
    private final Ability aussitzen = new Ability("Aussitzen", 8, 0, 4, List.of());
    private final Ability altklausur = new Ability("Altklausur", 20, 10, 9, List.of());

    private final TournamentParticipation hacker = fighter("Hacker", 80, 30, tippen, exploit, simpleHealAbility);
    private final TournamentParticipation student = fighter("Ewiger Student", 100, 20, aussitzen, altklausur, simpleHealAbility);
    private final Match match = new Match(hacker, student, effectSystem);

    public void setUpGame(){
        effectSystem.getRegistry().register(simpleHealEffect);
    }

    private static TournamentParticipation fighter(String name, int hp, int energy, Ability basicAttack, Ability perk, Ability perk2) {
        CharacterInstance instance = new CharacterInstance(new Character(name, hp, energy, basicAttack));
        instance.addPerk(perk);
        instance.addPerk(perk2);
        return new TournamentParticipation(instance);
    }

    private void playTurn(Ability hackerChoice, Ability studentChoice) {
        assertTrue(match.submitAbility(hacker, hackerChoice));
        assertTrue(match.submitAbility(student, studentChoice));
        match.resolveTurn();
    }

    private void assertHpAndEnergy(int hackerHp, int hackerEnergy, int studentHp, int studentEnergy) {
        assertEquals(hackerHp, hacker.getCharacter().getCurrentHP());
        assertEquals(hackerEnergy, hacker.getCharacter().getCurrentEnergy());
        assertEquals(studentHp, student.getCharacter().getCurrentHP());
        assertEquals(studentEnergy, student.getCharacter().getCurrentEnergy());
    }

    @Test
    void completeFightMatchesTheHandCalculation() {

        setUpGame();

        assertHpAndEnergy(80, 30, 100, 20);

        // Turn 1: Exploit (speed 8) before Aussitzen (speed 4).
        playTurn(exploit, aussitzen);
        assertHpAndEnergy(72, 15, 75, 20);

        // Turn 2: Altklausur (speed 9) before Tippen (speed 6).
        playTurn(tippen, altklausur);
        assertHpAndEnergy(52, 15, 65, 10);

        // Turn 3: both use their perk, which empties both energy bars.
        playTurn(exploit, altklausur);
        assertHpAndEnergy(32, 0, 40, 0);

        // From now on the perks are too expensive and only the free basic attacks are left.
        assertFalse(match.submitAbility(hacker, exploit));
        assertFalse(match.submitAbility(student, altklausur));

        playTurn(tippen, aussitzen);
        assertHpAndEnergy(24, 0, 30, 0);

        playTurn(tippen, aussitzen);
        assertHpAndEnergy(16, 0, 20, 0);

        playTurn(tippen, aussitzen);
        assertHpAndEnergy(8, 0, 10, 0);

        //Here we are testing the effect system for the first time
        playTurn(simpleHealAbility, simpleHealAbility);
        assertHpAndEnergy(16, 0, 18, 0);

        playTurn(tippen, aussitzen);
        assertHpAndEnergy(8, 0, 8, 0);

        // Turn 9: Tippen is faster and defeats the student, who does not act any more.
        playTurn(tippen, aussitzen);
        assertHpAndEnergy(8, 0, 0, 0);

        assertEquals(9, match.getTurnNumber());
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertSame(hacker, match.determineWinner());
    }
}
