package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class JoinLobbyScreen extends BaseScreen {

    private static final int TOTAL_SLOTS = 8;

    private final UIFactory ui;
    private final Skin lobbySkin;
    private final String lobbyId;

    private boolean tournamentStarted = false;
    private float timeRemaining = 10f; // testweise statt 60s
    private int lastDisplayedSecond = -1;

    private Label lblPlayersCount;
    private Label lblTimer;
    private Table playerListTable;

    private final Array<PlayerEntry> players = new Array<>();

    public static class PlayerEntry {
        public final String name;
        public final boolean isMe;

        public PlayerEntry(String name, boolean isMe) {
            this.name = name;
            this.isMe = isMe;
        }
    }

    public JoinLobbyScreen(TheLastSemesterGame game, String lobbyId) {
        super(game);
        this.lobbyId = lobbyId;

        this.lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        this.ui = new UIFactory(lobbySkin);

        // Testweise über schleife
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            boolean isMe;
            String playerName;

            if (i == 1) {
                playerName = "Mccyy";
                isMe = true;
            } else {
                playerName = "Player " + i;
                isMe = false;
            }

            players.add(new PlayerEntry(playerName, isMe));
        }
    }

    // Aufbau Aussehen
    @Override
    protected void buildUi() {
        root.pad(80, 15, 25, 15);

        playerListTable = new Table();
        updatePlayerListUI();

        TextButton btnLeave = ui.createFullWidthButton("Quit", "second");
        btnLeave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                leaveLobby();
            }
        });

        lblPlayersCount = ui.createLabel("", "small");
        lblTimer = ui.createLabel("", "small");
        updateStatusText();

        Table bottomStatusTable = new Table();
        bottomStatusTable.add(lblPlayersCount).padBottom(10).row();
        bottomStatusTable.add(lblTimer).row();

        root.add(createTitleTable()).width(UIFactory.FULL_BUTTON_WIDTH).padBottom(40).row();
        root.add(playerListTable).expandY().top().padTop(10).padBottom(20).row();
        root.add(btnLeave)
            .width(UIFactory.FULL_BUTTON_WIDTH)
            .height(UIFactory.FULL_BUTTON_HEIGHT)
            .padBottom(20).row();
        root.add(bottomStatusTable).bottom();
    }

    private Table createTitleTable() {
        Table header = new Table();

        Label title = ui.createLabel(lobbyId, "title");
        header.add(title).expandX().left().padLeft(5);

        return header;
    }

    private void updatePlayerListUI() {
        playerListTable.clear();
        for (PlayerEntry player : players) {
            Table row = new Table();
            row.setBackground(lobbySkin.getDrawable("button_back_2"));

            ImageButton pic = new ImageButton(lobbySkin, "profilepic");

            String displayName;
            if (player.isMe) {
                displayName = player.name + " (du)";
            } else {
                displayName = player.name;
            }

            Label nameLabel = ui.createLabel(displayName, "default");
            nameLabel.setFontScale(0.8f);
            nameLabel.setAlignment(Align.left);

            row.add(pic).size(UIFactory.SQUARE_SIZE).padLeft(25).padRight(12);
            row.add(nameLabel).expandX().left();

            playerListTable.add(row)
                .width(UIFactory.FULL_BUTTON_WIDTH)
                .height(UIFactory.FULL_BUTTON_HEIGHT)
                .padBottom(10)
                .row();
        }
    }

    private void updateStatusText() {
        lblPlayersCount.setText("Players: " + players.size + "/" + TOTAL_SLOTS);
    }

    private void leaveLobby() {
        Gdx.app.log("Lobby", "Leave Lobby");
        game.setScreen(new HomeScreen(game));
    }

    @Override
    protected void onBack() {
        leaveLobby();
    }

    private void updateTimer(float delta) {
        if (tournamentStarted) return;

        timeRemaining = Math.max(0f, timeRemaining - delta);
        int second = (int) Math.ceil(timeRemaining);
        if (second != lastDisplayedSecond) {
            lastDisplayedSecond = second;
            lblTimer.setText("Start in: " + second + "s");
        }

        if (timeRemaining <= 0f) {
            tournamentStarted = true;

            String[] playerNames = new String[players.size];
            for (int i = 0; i < players.size; i++) {
                playerNames[i] = players.get(i).name;
            }
            game.goTo(new TournamentScreen(game, playerNames));
        }
    }

    @Override
    public void render(float delta) {
        updateTimer(delta);
        super.render(delta);
    }
}
