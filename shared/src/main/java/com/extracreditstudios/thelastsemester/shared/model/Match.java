package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.*;

import com.extracreditstudios.thelastsemester.shared.messages.game.AbilityDTO;
import com.extracreditstudios.thelastsemester.shared.messages.game.CombatantDTO;
import com.extracreditstudios.thelastsemester.shared.messages.game.MatchDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/** One 1v1 fight. Both sides choose in secret, then the turn is resolved. */
public class Match {

    private final String matchId = UUID.randomUUID().toString();
    private long stateVersion;
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
            stateVersion++;
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
        stateVersion++;
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


    public String getMatchId() {
        return matchId;
    }

    public long getStateVersion() {
        return stateVersion;
    }

    /**
     * Snapshot for one participant. The server must synchronize this read with combat changes.
     * Timers and turn-event generation are not implemented yet.
     */
    public MatchDTO getState(TournamentParticipation viewer) {
        if (viewer == null || !isParticipant(viewer)) {
            throw new IllegalArgumentException("Viewer is not a match participant");
        }
        TournamentParticipation opponent = viewer == participantA ? participantB : participantA;
        List<AbilityDTO> myAbilities = new ArrayList<>();
        for (Ability ability : viewer.getCharacter().getAbilities()) {
            myAbilities.add(ability.toDTO());
        }
        Ability mySelection = pendingActions.get(viewer);
        String winnerId = null;
        if (status == MatchStatus.FINISHED) {
            winnerId = determineWinner().getPlayer().getPlayerId().toString();
        }
        return new MatchDTO(matchId, stateVersion, status, turnNumber, System.currentTimeMillis(),
            null, null, toCombatantDTO(viewer), toCombatantDTO(opponent), myAbilities,
            mySelection == null ? null : mySelection.toDTO(), pendingActions.containsKey(opponent),
            List.of(), winnerId);
    }

    private CombatantDTO toCombatantDTO(TournamentParticipation participant) {
        CharacterInstance character = participant.getCharacter();
        return new CombatantDTO(participant.getPlayer().getPlayerId().toString(), participant.getPlayer().getUsername(),
            character.getMaxHP(), character.getCurrentHP(), character.getMaxEnergy(), character.getCurrentEnergy());
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
        if (this.status != status) {
            this.status = status;
            stateVersion++;
        }
    }

    public Map<TournamentParticipation, Ability> getPendingActions() {
        return Map.copyOf(pendingActions);
    }

    private boolean isParticipant(TournamentParticipation participant) {
        return participant == participantA || participant == participantB;
    }

}
