package com.extracreditstudios.thelastsemester.shared.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * One complete fight, calculated by hand first. Only the two character names come from the PID;
 * ability names and all numbers are a proposal for the team.
 */
class MatchPlaythroughTest {

    private final Ability tippen = new Ability("Tippen", 10, 0, 6);
    private final Ability exploit = new Ability("Exploit", 25, 15, 8);
    private final Ability aussitzen = new Ability("Aussitzen", 8, 0, 4);
    private final Ability altklausur = new Ability("Altklausur", 20, 10, 9);

    private final TournamentParticipation hacker = fighter("Hacker", 80, 30, tippen, exploit);
    private final TournamentParticipation student =
        fighter("Ewiger Student", 100, 20, aussitzen, altklausur);
    private final Match match = new Match(hacker, student);

    private static TournamentParticipation fighter(String name, int hp, int energy,
                                                   Ability basicAttack, Ability perk) {
        CharacterInstance instance =
            new CharacterInstance(new Character(name, hp, energy, basicAttack));
        instance.addPerk(perk);
        return new TournamentParticipation(instance);
    }

    private void playTurn(Ability hackerChoice, Ability studentChoice) {
        assertTrue(match.submitAbility(hacker, hackerChoice));
        assertTrue(match.submitAbility(student, studentChoice));
        match.resolveTurn();
    }

    private void assertHpAndEnergy(int hackerHp, int hackerEnergy, int studentHp,
                                   int studentEnergy) {
        assertEquals(hackerHp, hacker.getCharacter().getCurrentHP());
        assertEquals(hackerEnergy, hacker.getCharacter().getCurrentEnergy());
        assertEquals(studentHp, student.getCharacter().getCurrentHP());
        assertEquals(studentEnergy, student.getCharacter().getCurrentEnergy());
    }

    @Test
    void completeFightMatchesTheHandCalculation() {
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

        // Turn 7: Tippen is faster and defeats the student, who does not act any more.
        playTurn(tippen, aussitzen);
        assertHpAndEnergy(8, 0, 0, 0);

        assertEquals(7, match.getTurnNumber());
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertSame(hacker, match.determineWinner());
    }
}
