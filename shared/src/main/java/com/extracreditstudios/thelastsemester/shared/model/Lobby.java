package com.extracreditstudios.thelastsemester.shared.model;

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


    public Lobby(String lobbyID, long startAfterMilis) {
        this.lobbyID = lobbyID;
        playerList = new ArrayList<>();
        phase = GamePhase.LOBBY;
        deadlineMillis = System.currentTimeMillis() + startAfterMilis;
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

    /* TODO
    public void addPlayer(Player player){
    if(maxPlayers > playerList.size()){
    playerList.add(player);
    }
    }*/

    public void startTournament(){
        List<TournamentParticipation> participants = this.playerList.stream()
            .map(player -> {
                Character character = player.getCharacter();
                CharacterInstance characterInstance = new CharacterInstance(character);
                TournamentParticipation participant = new TournamentParticipation(characterInstance);
                participant.setPlayer(player);
                return participant;
            }).toList();
        this.tournament = new Tournament(participants, null);
        this.tournament.start();
    }

}
