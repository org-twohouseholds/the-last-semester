
package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.lobby.JoinLobbyScreen;
import com.extracreditstudios.thelastsemester.screens.settings.SettingsScreen;

/**
 * Home-Screen nach Entwurf: Hintergrund, Titel, großer Champion mit Pfeilen,
 * zwei Zettel (Character grün, Join gelb).
 * Alle bisherigen Ziele bleiben erreichbar: Settings über das Zahnrad.
 */
public class HomeScreen extends BaseScreen {

    private static final boolean ARROW_POINTS_LEFT = true;

    private static final String[] CHAMPION_NAMES = {
        "Champion 1", "Champion 2", "Champion 3", "Champion 4"
    };

    // ---- Layout ----
    private static final float TOP_BAR_PAD = 10f;
    private static final float TITLE_WIDTH_FRAC = 0.55f;
    private static final float TITLE_TOP_PAD = 4f;
    private static final float GEAR_HIT_SIZE = 64f;
    private static final float BUTTON_GAP = 18f;
    private static final float BOTTOM_PAD = 40f;
    private static final float PLATE_FONT_SCALE = 1.5f;
    private static final float LINK_FONT_SCALE = 0.8f;

    private static final float CHAMPION_SCALE = 1.7f;
    private static final float CHAMPION_RAISE = 50f;
    private static final float CHAMPION_ASPECT = 0.38f;
    private static final float ARROW_SPACE = 100f;
    private static final float ARROW_TOP_FRAC = 0.30f;
    private static final float ARROW_SIZE = 44f;

    private final DesignKit kit = new DesignKit();

    private ChampionSelector championSelector;

    public HomeScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {
        addBackgroundIfAvailable();

        Texture greenTexture = kit.texture(DesignKit.BUTTON_GREEN);
        Texture yellowTexture = kit.texture(DesignKit.BUTTON_YELLOW);
        Texture titleTexture = kit.texture(DesignKit.TITLE);

        // Höhe der Zettel folgt dem Seitenverhältnis des Bildes
        float plateHeight = BUTTON_WIDTH * DesignKit.aspect(greenTexture);

        float stageW = stage.getWidth() > 0
            ? stage.getWidth()
            : Gdx.graphics.getWidth();

        float stageH = stage.getHeight() > 0
            ? stage.getHeight()
            : Gdx.graphics.getHeight();

        Table topBar = addTopBar();
        float titleH = addTitle(titleTexture, stageW);

        // Platz, den alles außer dem Champion belegt
        float used = TOP_BAR_PAD + topBar.getPrefHeight()
            + TITLE_TOP_PAD + titleH
            + 2 * plateHeight + BUTTON_GAP + BOTTOM_PAD;

        addChampionSelector(stageW, stageH - used);

        addPlateButton(
            "Character",
            greenTexture,
            DesignKit.TEXT_LIGHT,
            plateHeight,
            BUTTON_GAP,
            () -> game.goTo(new CharacterSelectionScreen(game))
        );

        addPlateButton(
            "Join",
            yellowTexture,
            DesignKit.TEXT_DARK,
            plateHeight,
            BOTTOM_PAD,
            () -> game.goTo(new JoinLobbyScreen(game, "Lobby-123"))
        );
    }


    private Table addTopBar() {
        Table topBar = new Table();

        topBar.add().expandX();
        topBar.add(createSettingsButton())
            .right()
            .size(GEAR_HIT_SIZE);

        root.add(topBar)
            .fillX()
            .padTop(TOP_BAR_PAD)
            .padLeft(12)
            .padRight(12)
            .row();

        return topBar;
    }

    private float addTitle(Texture titleTexture, float stageW) {
        Image title = new Image(titleTexture);
        title.setScaling(Scaling.fit);

        float titleW = stageW * TITLE_WIDTH_FRAC;
        float titleH = titleW * DesignKit.aspect(titleTexture);

        root.add(title)
            .width(titleW)
            .height(titleH)
            .padTop(TITLE_TOP_PAD)
            .row();

        return titleH;
    }

    private void addChampionSelector(float stageW, float visibleH) {
        championSelector = new ChampionSelector(
            kit.skin(),
            CHAMPION_NAMES,
            DesignKit.CHAMPION_IMAGES,
            DesignKit.ARROW,
            ARROW_POINTS_LEFT
        );

        championSelector.setSelectionListener(
            (index, name) ->
                Gdx.app.log("HomeScreen", "Champion gewählt: " + name)
        );

        float visible = Math.max(150f, visibleH);
        float championH = visible * CHAMPION_SCALE;
        float overlap = championH - visible;
        float championW = Math.min(
            stageW - ARROW_SPACE,
            championH * CHAMPION_ASPECT
        );

        championSelector.setChampionSize(championW, championH);

        championSelector.setArrowTopPadding(
            Math.max(
                0f,
                ARROW_TOP_FRAC * championH - ARROW_SIZE / 2f
            )
        );

        root.add(championSelector)
            .expandY()
            .bottom()
            .padTop(-CHAMPION_RAISE)
            .padBottom(-overlap + CHAMPION_RAISE)
            .row();
    }

    private void addPlateButton(
        String text,
        Texture texture,
        com.badlogic.gdx.graphics.Color textColor,
        float height,
        float padBottom,
        Runnable action
    ) {
        root.add(
                kit.imageButton(
                    text,
                    texture,
                    textColor,
                    PLATE_FONT_SCALE,
                    action
                )
            )
            .width(BUTTON_WIDTH)
            .height(height)
            .padBottom(padBottom)
            .row();
    }


    private Actor createSettingsButton() {
        Runnable openSettings =
            () -> game.goTo(new SettingsScreen(game));

        Texture gear = kit.textureOrNull(DesignKit.GEAR);

        return gear != null
            ? kit.imageOnly(gear, openSettings)
            : kit.link("Settings", LINK_FONT_SCALE, openSettings);
    }

    private void addBackgroundIfAvailable() {
        Texture texture = kit.textureOrNull(DesignKit.HOME_BACKGROUND);

        if (texture == null) {
            return;
        }

        Image background = new Image(texture);
        background.setScaling(Scaling.fill);
        background.setFillParent(true);

        stage.addActor(background);
        background.toBack();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (championSelector != null) {
            championSelector.dispose();
        }

        kit.dispose();
    }
}
