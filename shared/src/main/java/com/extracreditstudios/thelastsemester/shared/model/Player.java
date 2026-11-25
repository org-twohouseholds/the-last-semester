package com.extracreditstudios.thelastsemester.shared.model;

public class Player {
    private String username;

    private Character character;
    private boolean bot;

    private Lobby lobby = null;


    //for Testing?
    public Player(String username) {
        this.username = username;
    }

    private Player(String username, boolean bot, Lobby lobby) {
        this.username = username;
        this.bot = bot;
        this.lobby = lobby;
    }

    public static Player bot(int botNumber, Lobby lobby) {
        return new Player("Bot " + botNumber, true, lobby);
    }

    public String getUsername() {
        return username;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    public boolean isBot() {
        return bot;
    }
}
