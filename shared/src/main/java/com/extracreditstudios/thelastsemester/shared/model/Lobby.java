package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectSystem;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private String lobbyID;

    private int maxPlayers = 8;

    private final List<Player> playerList;

    private Tournament tournament;

    private GamePhase phase;

    private long stateVersion = 0;

    private Long deadlineMillis;

    private final EffectSystem effectSystem;


    public Lobby(String lobbyID, long startAfterMilis, EffectSystem effectSystem) {
        this.lobbyID = lobbyID;
        playerList = new ArrayList<>();
        phase = GamePhase.LOBBY;
        deadlineMillis = System.currentTimeMillis() + startAfterMilis;
        this.effectSystem = effectSystem;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public long getStateVersion() {
        return stateVersion;
    }

    public void incrementStateVersion() {
        stateVersion++;
    }

    public Long getDeadlineMillis() {
        return deadlineMillis;
    }

    public void setDeadlineMillis(Long deadlineMillis) {
        this.deadlineMillis = deadlineMillis;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void addPlayer(Player player){
        if(maxPlayers > playerList.size()){
            playerList.add(player);
        }else{
            throw new IllegalArgumentException("The Lobby is already full.");
        }
    }

    public void startTournament(){
        List<TournamentParticipation> participants = this.playerList.stream()
            .map(player -> {
                Character character = player.getCharacter();
                CharacterInstance characterInstance = new CharacterInstance(character);
                return new TournamentParticipation(characterInstance, player);
            }).toList();
        this.tournament = new Tournament(participants, effectSystem);
        this.tournament.start();
    }

}
