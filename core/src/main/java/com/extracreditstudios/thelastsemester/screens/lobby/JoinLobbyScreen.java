
package com.extracreditstudios.thelastsemester.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.DesignKit;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;

public class JoinLobbyScreen extends BaseScreen {

    private static final int TOTAL_SLOTS = 8;
    private static final int TOTAL_CHARACTERS = 5;

    private static final float LOBBY_TIME_SECONDS = 10f;

    private static final Color NAME_COLOR =
        new Color(0.32f, 0.27f, 0.17f, 1f);

    private final DesignKit kit = new DesignKit();

    private final String lobbyId;

    private final Texture backgroundTexture;
    private final Texture[] characterTextures =
        new Texture[TOTAL_CHARACTERS];

    private final Array<PlayerEntry> players = new Array<>();

    private boolean tournamentStarted = false;
    private float timeRemaining = LOBBY_TIME_SECONDS;
    private int lastDisplayedSecond = -1;

    private Label lblTimer;
    private Table playerListTable;



    public static class PlayerEntry {

        public final String name;
        public final boolean isMe;
        public final int characterId;

        public PlayerEntry(String name, boolean isMe, int characterId) {
            this.name = name;
            this.isMe = isMe;
            this.characterId = characterId;
        }
    }

    public JoinLobbyScreen(TheLastSemesterGame game, String lobbyId) {
        super(game);

        this.lobbyId = lobbyId;

        backgroundTexture = kit.texture(DesignKit.LOBBY_BACKGROUND);

        for (int i = 0; i < TOTAL_CHARACTERS; i++) {
            characterTextures[i] =
                kit.texture(DesignKit.PROFILE_PICS[i]);
        }

        createTestPlayers();
    }

    private void createTestPlayers() {
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            boolean isMe = (i == 1);

            String name;

            if (isMe) {
                name = "Mccyy";
            } else {
                name = "Player " + i;
            }

            int characterId =
                ((i - 1) % TOTAL_CHARACTERS) + 1;

            players.add(
                new PlayerEntry(name, isMe, characterId)
            );
        }
    }


    @Override
    protected void buildUi() {
        root.clear();
        root.pad(0);

        Image background = new Image(backgroundTexture);
        background.setScaling(Scaling.stretch);

        Table overlay = new Table();
        overlay.top().left();

        Stack stack = new Stack();
        stack.add(background);
        stack.add(overlay);

        playerListTable = new Table();
        playerListTable.top().left();

        updatePlayerListUI();

        lblTimer = kit.label(
            formatTime(LOBBY_TIME_SECONDS),
            "small"
        );


        Table btnLeave = new Table();
        DesignKit.onClick(btnLeave, this::leaveLobby);

        overlay.add(lblTimer)
            .width(Value.percentWidth(0.15f, overlay))
            .height(Value.percentHeight(0.04f, overlay))
            .padTop(Value.percentHeight(0.145f, overlay))
            .padLeft(Value.percentWidth(0.69f, overlay))
            .row();

        overlay.add(playerListTable)
            .width(Value.percentWidth(0.75f, overlay))
            .height(Value.percentHeight(0.57f, overlay))
            .padLeft(Value.percentWidth(0.09f, overlay))
            .row();

        overlay.add(btnLeave)
            .width(Value.percentWidth(0.42f, overlay))
            .height(Value.percentHeight(0.05f, overlay))
            .padTop(Value.percentHeight(0.095f, overlay))
            .padLeft(Value.percentWidth(0.09f, overlay))
            .row();

        root.add(stack).expand().fill();
    }

    private void updatePlayerListUI() {
        playerListTable.clearChildren();
        playerListTable.defaults().pad(0);

        for (int i = 0; i < players.size; i++) {
            playerListTable.add(
                    createPlayerRow(players.get(i), i)
                )
                .expandX()
                .fillX()
                .height(
                    Value.percentHeight(0.10965f, playerListTable)
                )
                .padBottom(
                    Value.percentHeight(0.01754f, playerListTable)
                )
                .row();
        }
    }

    private Table createPlayerRow(PlayerEntry player, int index) {
        Image profilePic =
            new Image(characterTextures[player.characterId - 1]);

        profilePic.setScaling(Scaling.fit);

        String displayName = player.isMe
            ? player.name + " (du)"
            : player.name;

        Label nameLabel = kit.label(
            displayName,
            NAME_COLOR,
            0.8f
        );

        nameLabel.setAlignment(Align.left | Align.center);

        Table row = new Table();

        row.add(profilePic)
            .size(48, 61)
            .padLeft(8)
            .padRight(12)
            .top()
            .padTop(profileImageOffset(index));

        row.add(nameLabel)
            .expandX()
            .fillX();

        return row;
    }


    private float profileImageOffset(int index) {
        if (index == 7) return -20f;
        if (index == 6) return -12f;

        return -1f;
    }



    private static String formatTime(float seconds) {
        return (int) Math.ceil(seconds) + "s";
    }

    private void leaveLobby() {
        Gdx.app.log("Lobby", "Leave Lobby");
        game.goTo(new HomeScreen(game));
    }

    @Override
    protected void onBack() {
        leaveLobby();
    }

    private void updateTimer(float delta) {
        if (tournamentStarted) {
            return;
        }

        timeRemaining = Math.max(0f, timeRemaining - delta);

        int second = (int) Math.ceil(timeRemaining);

        if (second != lastDisplayedSecond) {
            lastDisplayedSecond = second;
            lblTimer.setText(second + "s");
        }

        if (timeRemaining <= 0f) {
            startTournament();
        }
    }

    private void startTournament() {
        tournamentStarted = true;

        // Attacken für ein neues Turnier zurücksetzen
        game.resetAttacks();

        String[] playerNames = new String[players.size];

        for (int i = 0; i < players.size; i++) {
            playerNames[i] = players.get(i).name;
        }

        game.goTo(new TournamentScreen(game, playerNames));
    }

    @Override
    public void render(float delta) {
        updateTimer(delta);
        super.render(delta);
    }



    @Override
    public void dispose() {
        super.dispose();
        kit.dispose();
    }
}
