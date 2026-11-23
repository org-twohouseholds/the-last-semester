package com.extracreditstudios.thelastsemester.battleScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.extracreditstudios.thelastsemester.screens.lobby.FinalWinner;
import com.extracreditstudios.thelastsemester.screens.lobby.TournamentScreen;

import com.extracreditstudios.thelastsemester.TheLastSemesterGame;


public class BattleScreen extends ScreenAdapter {

    // minimum visible world size. ExtendViewport grows the world beyond this size.
    private static final float WORLD_W = 400f;
    private static final float WORLD_H = 700f;


    private static final String FONT_PATH =
        "finaldesign_data/PatrickHand-Regular.fnt";
    private static final String BUTTON_PATH =
        "finaldesign_data/button_yellow.png";
    private static final String FONT_PNG_PATH =
        "finaldesign_data/PatrickHand-Regular.png";

    private Texture fontTexture;

    private static final float ATTACK_FONT_SCALE = 0.7f;
    private static final float ROUND_DURATION = 120f;


    private final TheLastSemesterGame game;

    // Scene2D (one shared viewport)
    private Viewport viewport;
    private Stage stage;
    private UiDrawableFactory drawables;
    private BitmapFont attackFont;
    private Texture attackButtonTexture;
    private SceneView sceneView;
    private RoundTimerView roundTimerView;
    private StatusBarsView statusBarsView;
    private CountdownView countdownView;
    private AttackButtonsView attackButtonsView;
    private Battle battle;
    private float roundTimeLeft = ROUND_DURATION;
    private boolean roundOver = false;

    private boolean battleFinished = false;
    private final String[] players;


    public BattleScreen(TheLastSemesterGame game, String[] players) {

        this.game = game;
        this.players = players;
    }

    @Override
    public void show() {

        viewport = new ExtendViewport(WORLD_W, WORLD_H);
        stage = new Stage(viewport);
        drawables = new UiDrawableFactory();

        loadResources();
        createTestBattle();   // replace with real logic later

        sceneView = new SceneView(stage);
        sceneView.layout(viewport.getWorldWidth(), viewport.getWorldHeight());

        roundTimerView = new RoundTimerView(stage, drawables, ROUND_DURATION);
        statusBarsView = new StatusBarsView(stage, drawables, attackFont);
        countdownView = new CountdownView(stage, attackFont);

        attackButtonsView = new AttackButtonsView(
            stage, attackButtonTexture, attackFont, battle.player.attacks,
            attackIndex -> {
                if (!battle.isOver() && !roundOver) {
                    battle.choose(attackIndex);
                }
            }
        );
        Gdx.input.setInputProcessor(stage);
    }

    private void loadResources() {

        // Load the font page, make the letters white
        Pixmap pixmap = new Pixmap(Gdx.files.internal(FONT_PNG_PATH));

        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                int alpha = pixmap.getPixel(x, y) & 0xFF;
                pixmap.drawPixel(x, y, 0xFFFFFF00 | alpha);
            }
        }

        // loads font and button textures used by battle screen
        fontTexture = new Texture(pixmap);
        pixmap.dispose();
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        attackFont = new BitmapFont(
            Gdx.files.internal(FONT_PATH),
            new TextureRegion(fontTexture),
            false
        );
        attackFont.getData().setScale(ATTACK_FONT_SCALE);

        attackButtonTexture = new Texture(Gdx.files.internal(BUTTON_PATH));
    }

    // values for tests - delete later
    private void createTestBattle() {

        Fighter bitter = new Fighter(new Attack[] {
            new Attack("Attacke 1", 20, 10),
            new Attack("Attacke 2", 35, 25),
            new Attack("Attacke 3", 10, 0),
            new Attack("Attacke 4", 50, 60)
        });

        Fighter hackerFighter = new Fighter(new Attack[] {
            new Attack("Hack 1", 15, 5),
            new Attack("Hack 2", 30, 20)
        });

        bitter.hp = 50;
        bitter.energy = 100;

        battle = new Battle(bitter, hackerFighter);
    }

    // updates battle state and UI, then renders the screen
    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 0, 0, 1);

        delta = Math.min(delta, 0.05f);

        updateLogic(delta);
        updateUi();

        stage.act(delta);
        stage.draw();
    }
    // updates local battle logic and round timer
    private void updateLogic(float delta) {

        if (!battle.isOver() && !roundOver) {
            battle.update(delta);
        }

        // Check if the battle has ended
        if (!battleFinished && battle.isOver()) {
            battleFinished = true;
            finishBattle();
            return;
        }

        if (!roundOver && !battle.isOver()) {
            roundTimeLeft = Math.max(0f, roundTimeLeft - delta);

            if (roundTimeLeft <= 0f) {
                roundOver = true;
                // end screen missing?
            }
        }
    }

    private void finishBattle() {
        if (players.length > 2) {

            String[] nextPlayers = new String[players.length / 2];

            for (int i = 0; i < nextPlayers.length; i++) {
                nextPlayers[i] = players[i * 2];
            }

            game.goTo(new TournamentScreen(game, nextPlayers));

        } else {

            boolean isUserWinner = true;
            String winnerName = players[0];
            int earnedPoints = 30;

            game.goTo(new FinalWinner(
                game,
                isUserWinner,
                winnerName,
                earnedPoints
            ));
        }
    }

    // updates all UI elements with current battle data
    private void updateUi() {

        roundTimerView.update(roundTimeLeft);
        statusBarsView.update(battle);
        countdownView.update(battle.timeLeft);
        attackButtonsView.update(battle, roundOver);
    }

    // updates viewport and scene layout when screen is resized
    @Override
    public void resize(int width, int height) {

        if (viewport != null) {
            viewport.update(width, height, true);

            if (sceneView != null) {
                sceneView.layout(
                    viewport.getWorldWidth(),
                    viewport.getWorldHeight()
                );
            }
        }
    }

    @Override
    public void dispose() {

        if (stage != null) {
            stage.dispose();
        }

        if (sceneView != null) {
            sceneView.dispose();
        }

        if (drawables != null) {
            drawables.dispose();
        }

        if (attackFont != null) {
            attackFont.dispose();
        }

        if (attackButtonTexture != null) {
            attackButtonTexture.dispose();
        }

        if (fontTexture != null) {
            fontTexture.dispose();
        }
    }
}

