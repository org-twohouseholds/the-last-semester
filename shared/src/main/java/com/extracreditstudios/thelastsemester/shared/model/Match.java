package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.*;

import java.util.HashMap;
import java.util.Map;

/** One 1v1 fight. Both sides choose in secret, then the turn is resolved. */
public class Match {

    private final TournamentParticipation participantA;
    private final TournamentParticipation participantB;
    private final Map<TournamentParticipation, Ability> pendingActions = new HashMap<TournamentParticipation, Ability>();
    private int turnNumber = 1;
    private MatchStatus status = MatchStatus.IN_PROGRESS;
    private final EffectSystem effectSystem;

    public Match(TournamentParticipation combatantA, TournamentParticipation combatantB, EffectSystem effectSystem) {
        this.participantA = combatantA;
        this.participantB = combatantB;
        this.effectSystem = effectSystem;
    }

    /** Stores the secret choice of a combatant. Returns false if the action is not allowed. */
    public boolean submitAbility(TournamentParticipation participant, Ability ability) {
        boolean allowed = status == MatchStatus.IN_PROGRESS
            && isParticipant(participant)
            && !pendingActions.containsKey(participant)
            && participant.getCharacter().canUse(ability);
        if (allowed) {
            pendingActions.put(participant, ability);
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
        TournamentParticipation first = findFasterCombatant();
        TournamentParticipation second = first == participantA ? participantB : participantA;

        //For every point in the Match for which a EventTiming exists, there has to be a call to apply the Effect through the effectSystem
        effectSystem.applyEffects(first.getCharacter(), second.getCharacter(), EffectTiming.BEFORE_ATTACK);
        executeAction(first, second);
        effectSystem.applyEffects(first.getCharacter(), second.getCharacter(), EffectTiming.AFTER_ATTACK);

        if (!second.getCharacter().isDefeated()) {
            effectSystem.applyEffects(second.getCharacter(), first.getCharacter(), EffectTiming.BEFORE_ATTACK);
            executeAction(second, first);
            effectSystem.applyEffects(second.getCharacter(), first.getCharacter(), EffectTiming.AFTER_ATTACK);
        }
        pendingActions.clear();

        if (first.getCharacter().isDefeated() || second.getCharacter().isDefeated()) {
            status = MatchStatus.FINISHED;
        } else {
            turnNumber++;
        }
        effectSystem.applyEffects(first.getCharacter(), second.getCharacter(), EffectTiming.AFTER_TURN);
        effectSystem.applyEffects(second.getCharacter(), first.getCharacter(), EffectTiming.AFTER_TURN);
    }


    public TournamentParticipation determineWinner() {
        if (status != MatchStatus.FINISHED) {
            throw new IllegalStateException("The match is not finished yet");
        }
        return participantA.getCharacter().isDefeated() ? participantB : participantA;
    }

    private TournamentParticipation findFasterCombatant() {

        //If both have the same speed its randowm who comes first TODO maybe implement seed
        if (pendingActions.get(participantB).speed() > pendingActions.get(participantA).speed()) {
            return participantB;
        }else if(pendingActions.get(participantB).speed() == pendingActions.get(participantA).speed()){
            return Math.random() < 0.5 ? participantA : participantB;
        }
        return participantA;
    }

    private void executeAction(TournamentParticipation actor, TournamentParticipation target) {
        Ability ability = pendingActions.get(actor);
        actor.getCharacter().useAbility(ability, effectSystem.getRegistry());
        target.getCharacter().takeDamage((int) (ability.baseDamage() * actor.getCharacter().getDmgAmplification() * target.getCharacter().getDmgReduction()));
    }


    public int getTurnNumber() {
        return turnNumber;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public TournamentParticipation getParticipantA() {
        return participantA;
    }

    public TournamentParticipation getParticipantB() {
        return participantB;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Map<TournamentParticipation, Ability> getPendingActions() {
        return pendingActions;
    }

    private boolean isParticipant(TournamentParticipation participant) {
        return participant == participantA || participant == participantB;
    }

}
