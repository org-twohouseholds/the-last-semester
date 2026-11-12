package com.extracreditstudios.thelastsemester.shared.model;

/** One participant of one tournament. */
public class TournamentParticipation implements Combatant {

    private final CharacterInstance character;
    private boolean eliminated;

    public TournamentParticipation(CharacterInstance character) {
        this.character = character;
    }

    public void eliminate() {
        eliminated = true;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    @Override
    public CharacterInstance getCharacter() {
        return character;
    }
}
