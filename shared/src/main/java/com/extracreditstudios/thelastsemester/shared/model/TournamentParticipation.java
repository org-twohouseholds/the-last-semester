package com.extracreditstudios.thelastsemester.shared.model;
import java.util.List;

/** One participant of one tournament. */
public class TournamentParticipation implements Combatant {

    private Player player;
    private final CharacterInstance character;
    private boolean eliminated;

    public TournamentParticipation(CharacterInstance character) {
        this.character = character;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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
