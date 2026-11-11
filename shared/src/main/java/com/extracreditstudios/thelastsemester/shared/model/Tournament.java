package com.extracreditstudios.thelastsemester.shared.model;

import java.util.List;

/** The participants, the current round and the status of one tournament. */
public class Tournament {

    private final List<TournamentParticipation> participants;
    private int currentRound;
    private TournamentStatus status = TournamentStatus.CREATED;

    public Tournament(List<TournamentParticipation> participants) {
        if (participants.size() < 2) {
            throw new IllegalArgumentException("A tournament needs at least 2 participants");
        }
        this.participants = List.copyOf(participants);
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
