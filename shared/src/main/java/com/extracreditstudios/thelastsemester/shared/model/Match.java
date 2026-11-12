package com.extracreditstudios.thelastsemester.shared.model;

import java.util.HashMap;
import java.util.Map;

/** One 1v1 fight. Both sides choose in secret, then the turn is resolved. */
public class Match {

    private final Combatant combatantA;
    private final Combatant combatantB;
    private final Map<Combatant, Ability> pendingActions = new HashMap<>();
    private int turnNumber = 1;
    private MatchStatus status = MatchStatus.IN_PROGRESS;

    public Match(Combatant combatantA, Combatant combatantB) {
        this.combatantA = combatantA;
        this.combatantB = combatantB;
    }

    /** Stores the secret choice of a combatant. Returns false if the action is not allowed. */
    public boolean submitAbility(Combatant combatant, Ability ability) {
        boolean allowed = status == MatchStatus.IN_PROGRESS
            && isParticipant(combatant)
            && !pendingActions.containsKey(combatant)
            && combatant.getCharacter().canUse(ability);
        if (allowed) {
            pendingActions.put(combatant, ability);
        }
        return allowed;
    }

    public boolean bothSubmitted() {
        return pendingActions.size() == 2;
    }

    /** Executes both chosen actions, the faster one first. */
    public void resolveTurn() {
        if (!bothSubmitted()) {
            throw new IllegalStateException("Both combatants must submit an action first");
        }
        Combatant first = findFasterCombatant();
        Combatant second = first == combatantA ? combatantB : combatantA;

        executeAction(first, second);
        // TODO team decision: does a fighter defeated by the first action still act?
        if (!second.getCharacter().isDefeated()) {
            executeAction(second, first);
        }
        pendingActions.clear();

        if (first.getCharacter().isDefeated() || second.getCharacter().isDefeated()) {
            status = MatchStatus.FINISHED;
        } else {
            turnNumber++;
        }
    }

    public Combatant determineWinner() {
        if (status != MatchStatus.FINISHED) {
            throw new IllegalStateException("The match is not finished yet");
        }
        return combatantA.getCharacter().isDefeated() ? combatantB : combatantA;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public MatchStatus getStatus() {
        return status;
    }

    private boolean isParticipant(Combatant combatant) {
        return combatant == combatantA || combatant == combatantB;
    }

    private Combatant findFasterCombatant() {
        // TODO team decision: what happens at equal speed? For now combatant A goes first.
        if (pendingActions.get(combatantB).speed() > pendingActions.get(combatantA).speed()) {
            return combatantB;
        }
        return combatantA;
    }

    private void executeAction(Combatant actor, Combatant target) {
        Ability ability = pendingActions.get(actor);
        actor.getCharacter().useAbility(ability);
        target.getCharacter().takeDamage(ability.baseDamage());
    }
}
