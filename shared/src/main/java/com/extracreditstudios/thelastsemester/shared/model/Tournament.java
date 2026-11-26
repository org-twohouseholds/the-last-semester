package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectSystem;

import java.util.ArrayList;
import java.util.List;

/** The participants, the current round and the status of one tournament. */
public class Tournament {

    private final List<TournamentParticipation> participants;
    private int currentRound;
    private TournamentStatus status = TournamentStatus.CREATED;
    private ArrayList<Match> bracket;

    public Tournament(List<TournamentParticipation> participants, EffectSystem effectSystem) {
        if (participants.size() < 2) {
            throw new IllegalArgumentException("A tournament needs at least 2 participants");
        }
        this.participants = List.copyOf(participants);
        List<TournamentParticipation> copyParticipants = new ArrayList<>(participants);
        int tournamntSize = copyParticipants.size();
        this.bracket = new ArrayList<>();

        //TODO error handling and bot fill up
        //Fill the Bracket with the participants in a random order
        for(int i = 0; i < (tournamntSize/2); i++){
            int randomIndex = (int) Math.floor(Math.random() * copyParticipants.size());
            TournamentParticipation participantA = copyParticipants.get(randomIndex);
            copyParticipants.remove(randomIndex);
            randomIndex = (int) Math.floor(Math.random() * copyParticipants.size());
            TournamentParticipation participantB = copyParticipants.get(randomIndex);
            copyParticipants.remove(randomIndex);
            this.bracket.add(new Match(participantA, participantB, effectSystem));
        }
    }

    public void start() {
        if (status != TournamentStatus.CREATED) {
            throw new IllegalStateException("The tournament was already started");
        }
        status = TournamentStatus.RUNNING;
        currentRound = 1;
    }

    public void advanceRound() {
        requireRunning();
        currentRound++;
    }

    public void finishTournament() {
        requireRunning();
        status = TournamentStatus.FINISHED;
    }

    public List<TournamentParticipation> getParticipants() {
        return participants;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    private void requireRunning() {
        if (status != TournamentStatus.RUNNING) {
            throw new IllegalStateException("The tournament is not running");
        }
    }
}
