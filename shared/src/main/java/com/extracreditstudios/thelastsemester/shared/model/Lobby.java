package com.extracreditstudios.thelastsemester.shared.model;

public class Lobby {

    private String lobbyID;
    private int maxPlayers = 8;
    //private List<Player> playerList;
    private Tournament tournament;

    public Lobby(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    /* TODO
    public void addPlayer(Player player){
    if(maxPlayers > playerList.size()){
    playerList.add(player);
    }
    }

    public void createTournament(){
        this.tournament = new Tournament()
    }

    */

}
