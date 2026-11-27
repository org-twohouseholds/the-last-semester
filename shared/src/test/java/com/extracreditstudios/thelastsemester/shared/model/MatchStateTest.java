package com.extracreditstudios.thelastsemester.shared.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.extracreditstudios.thelastsemester.shared.messages.game.MatchDTO;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectSystem;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MatchStateTest {
    private final Ability basic = new Ability("Basic", 10, 0, 5, List.of(), "description", 2 );
    private final Ability perk = new Ability("Own Perk", 20, 5, 9, List.of(), "description", 2 );
    private final TournamentParticipation alice = fighter("Alice");
    private final TournamentParticipation bob = fighter("Bob");
    private final Match match = new Match(alice, bob, new EffectSystem());

    private TournamentParticipation fighter(String name) {
        return new TournamentParticipation(
            new CharacterInstance(new Character("Test", 100, 50, basic)), new Player(name));
    }

    @Test
    void eachParticipantSeesOnlyTheirOwnAbilitiesAndSelection() {
        alice.getCharacter().addPerk(perk);
        List<Ability> abilitiesBefore = alice.getCharacter().getAbilities();
        assertEquals(List.of(basic, perk), abilitiesBefore);
        assertThrows(UnsupportedOperationException.class, () -> abilitiesBefore.clear());
        assertTrue(match.submitAbility(alice, perk));

        MatchDTO forAlice = match.getState(alice);
        MatchDTO forBob = match.getState(bob);
        assertEquals("Alice", forAlice.self().name());
        assertEquals("Bob", forAlice.opponent().name());
        assertEquals("Bob", forBob.self().name());
        assertEquals("Alice", forBob.opponent().name());
        assertEquals(alice.getPlayer().getPlayerId().toString(), forAlice.self().combatantId());
        assertEquals(bob.getPlayer().getPlayerId().toString(), forAlice.opponent().combatantId());
        assertEquals(100, forAlice.self().maxHP());
        assertEquals(100, forAlice.self().currentHP());
        assertEquals(50, forAlice.self().maxEnergy());
        assertEquals(50, forAlice.self().currentEnergy());
        assertEquals(List.of(basic.toDTO(), perk.toDTO()), forAlice.myAbilities());
        assertEquals(List.of(basic.toDTO()), forBob.myAbilities());
        assertEquals(perk.toDTO(), forAlice.mySubmittedAbility());
        assertNull(forBob.mySubmittedAbility());
        assertFalse(forAlice.opponentHasSubmitted());
        assertTrue(forBob.opponentHasSubmitted());
        assertNull(forAlice.winnerId());
        assertNull(forAlice.turnDeadlineMillis());
        assertNull(forAlice.matchDeadlineMillis());
        assertTrue(forAlice.lastTurnEvents().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> forAlice.myAbilities().clear());

        alice.getCharacter().addPerk(new Ability("Later", 1, 0, 1, List.of(), "description", 2 ));
        assertEquals(2, abilitiesBefore.size());
        assertEquals(2, forAlice.myAbilities().size());
        assertTrue(match.submitAbility(bob, basic));
        match.resolveTurn();
        assertEquals(100, forAlice.self().currentHP());
        assertEquals(50, forAlice.self().currentEnergy());
        assertEquals(90, match.getState(alice).self().currentHP());
        assertEquals(45, match.getState(alice).self().currentEnergy());
        assertEquals(80, match.getState(alice).opponent().currentHP());
    }

    @Test
    void outsidersCannotReadTheMatchState() {
        assertThrows(IllegalArgumentException.class, () -> match.getState(fighter("Outsider")));
        assertThrows(IllegalArgumentException.class, () -> match.getState(null));
        assertEquals(0, match.getStateVersion());
    }

    @Test
    void matchIdStaysStableAndOnlySuccessfulChangesIncreaseTheVersion() {
        String matchId = match.getMatchId();
        UUID.fromString(matchId);
        UUID playerId = alice.getPlayer().getPlayerId();
        assertEquals(playerId, alice.getPlayer().getPlayerId());
        Player botA = Player.bot(1, null);
        Player botB = Player.bot(1, null);
        assertTrue(botA.isBot());
        assertFalse(botA.getPlayerId().equals(botB.getPlayerId()));
        assertFalse(playerId.equals(bob.getPlayer().getPlayerId()));
        long beforeRead = System.currentTimeMillis();
        MatchDTO initial = match.getState(alice);
        assertTrue(initial.serverTimeMillis() >= beforeRead);
        assertTrue(initial.serverTimeMillis() <= System.currentTimeMillis());
        assertEquals(playerId.toString(), initial.self().combatantId());
        assertEquals(0, initial.stateVersion());
        assertEquals(0, match.getState(alice).stateVersion());
        assertFalse(match.submitAbility(fighter("Outsider"), basic));
        assertFalse(match.submitAbility(alice, perk));
        assertThrows(IllegalStateException.class, match::resolveTurn);
        assertEquals(0, match.getStateVersion());

        Ability finisher = new Ability("Finisher", 100, 5, 9, List.of(), "description", 2 );
        bob.getCharacter().addPerk(finisher);
        assertTrue(match.submitAbility(alice, basic));
        assertFalse(match.submitAbility(alice, basic));
        assertEquals(1, match.getStateVersion());
        assertThrows(UnsupportedOperationException.class, () -> match.getPendingActions().clear());
        assertTrue(match.submitAbility(bob, finisher));
        assertEquals(2, match.getStateVersion());
        match.resolveTurn();
        MatchDTO finished = match.getState(alice);
        assertEquals(3, finished.stateVersion());
        assertEquals(matchId, finished.matchId());
        assertEquals(playerId.toString(), finished.self().combatantId());
        assertEquals(MatchStatus.FINISHED, finished.status());
        assertEquals(bob.getPlayer().getPlayerId().toString(), finished.winnerId());
        assertEquals(bob, match.determineWinner());
        assertNull(finished.mySubmittedAbility());
        assertFalse(finished.opponentHasSubmitted());
        assertFalse(match.submitAbility(alice, basic));
        match.setStatus(MatchStatus.FINISHED);
        assertEquals(3, match.getStateVersion());

        Match other = new Match(alice, bob, new EffectSystem());
        other.setStatus(MatchStatus.FINISHED);
        assertEquals(1, other.getStateVersion());
        other.setStatus(MatchStatus.FINISHED);
        assertEquals(1, other.getStateVersion());
    }
}
