package com.extracreditstudios.thelastsemester.shared.model;
import java.util.List;

/** One participant of one tournament. */
public class TournamentParticipation implements Combatant {

    private final Player player;
    private final CharacterInstance character;
    private boolean eliminated;

    public TournamentParticipation(CharacterInstance character, Player player) {
        this.character = character;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
