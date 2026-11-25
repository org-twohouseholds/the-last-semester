package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.battleScreen.Attack;
import com.extracreditstudios.thelastsemester.battleScreen.BattleScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopScreen extends BaseScreen {

    private Texture backgroundTexture;
    private Texture buttonTexture;
    private Texture cpBarTexture;

    private final String[] players;
    private Attack selectedPerk = null;
    private TextButton selectedButton = null;

    private Label timerLabel;

    private float timeLeft = 15f;
    private boolean selectionMade = false;

    // UI positions
    private static final float TIMER_X = 15f;
    private static final float TIMER_Y = 750f;

    private static final float BUTTON_X = 110f;
    private static final float BUTTON_Y = 240f;

    private static final float CP_X = 115f;
    private static final float CP_Y = 610f;

    // Available perks
    private static final Attack[] ALL_PERKS = {
        new Attack("Coffee Rush", 60, 30),
        new Attack("All-Nighter", 75, 70),
        new Attack("Copy & Paste", 50, 15),
        new Attack("Group Project", 45, 10),
        new Attack("Deadline Panic", 60, 40),
        new Attack("Office Hours", 80, 80),
        new Attack("Mensa Special", 70, 50),
        new Attack("Plagiarism", 65, 50)
    };

    private final List<Attack> offers = new ArrayList<>();

    public ShopScreen(TheLastSemesterGame game, String[] players) {
        super(game);
        this.players = players;

        // randomly selects two perks
        offers.addAll(Arrays.asList(ALL_PERKS));
        Collections.shuffle(offers);

        while (offers.size() > 2) {
            offers.remove(offers.size() - 1);
        }
    }

    @Override
    protected void buildUi() {

        backgroundTexture = new Texture(
            Gdx.files.internal("finaldesign_data/setting_background.png")
        );

        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);
        background.toBack();

        buttonTexture = new Texture(
            Gdx.files.internal("finaldesign_data/button_yellow.png")
        );

        // title
        Label.LabelStyle titleStyle =
            new Label.LabelStyle(skin.get(Label.LabelStyle.class));

        titleStyle.fontColor = Color.BLACK;

        Label title = new Label("Choose Your Attack", titleStyle);
        title.setFontScale(2.5f);
        title.pack();

        title.setPosition((WORLD_WIDTH - title.getWidth()) / 2, 810);
        stage.addActor(title);

        // countdown timer
        Label.LabelStyle timerStyle =
            new Label.LabelStyle(skin.get(Label.LabelStyle.class));

        timerStyle.fontColor = Color.RED;

        timerLabel = new Label("15", timerStyle);
        timerLabel.setFontScale(3.0f);
        timerLabel.pack();

        timerLabel.setPosition((WORLD_WIDTH - timerLabel.getWidth()) / 2 + TIMER_X, TIMER_Y);
        stage.addActor(timerLabel);

        // create CP background, hide "settings"
        Pixmap pixmap = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(
            new Color(0.25f, 0.39f, 0.32f, 1f));
        pixmap.fill();

        cpBarTexture = new Texture(pixmap);
        pixmap.dispose();

        // CP display
        Label.LabelStyle cpStyle =
            new Label.LabelStyle(skin.get(Label.LabelStyle.class));

        cpStyle.fontColor = Color.WHITE;

        Label cpLabel = new Label("CP: " + game.getCP(), cpStyle);
        cpLabel.setFontScale(1.8f);

        Table cpTable = new Table();

        cpTable.setBackground(
            new TextureRegionDrawable(new TextureRegion(cpBarTexture)));

        cpTable.add(cpLabel).expand().center();

        cpTable.setSize(160, 50);
        cpTable.setPosition(CP_X, CP_Y);

        stage.addActor(cpTable);

        // Button style
        TextButton.TextButtonStyle buttonStyle =
            new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));

        TextureRegionDrawable yellowButton =
            new TextureRegionDrawable(new TextureRegion(buttonTexture));

        buttonStyle.up = yellowButton;
        buttonStyle.down = yellowButton;
        buttonStyle.over = yellowButton;

        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.downFontColor = Color.BLACK;
        buttonStyle.overFontColor = Color.BLACK;

        // Create perk buttons
        Table buttonTable = new Table();

        for (Attack perk : offers) {

            String text =
                perk.name
                    + "\nDamage: " + perk.damage
                    + "     Energy: " + perk.energyCost;

            TextButton perkButton = new TextButton(text, buttonStyle);

            perkButton.getLabel().setFontScale(1.1f);

            perkButton.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    if (selectionMade) {
                        return;
                    }

                    // reset previous selection
                    if (selectedButton != null) {
                        selectedButton.setColor(
                            1f, 1f, 1f, 1f
                        );
                    }
                    // save new selection
                    selectedPerk = perk;
                    selectedButton = perkButton;

                    // highlight selected button
                    selectedButton.setColor(
                        0.55f, 0.55f, 0.55f, 1f
                    );
                }
            });

            buttonTable.add(perkButton)
                .width(230)
                .height(85)
                .padBottom(30)
                .row();
        }

        buttonTable.pack();

        buttonTable.setPosition(
            BUTTON_X, BUTTON_Y);
        stage.addActor(buttonTable);
    }

    // countdown and screen transition
    @Override
    public void render(float delta) {

        super.render(delta);

        if (selectionMade) {
            return;
        }

        timeLeft -= delta;

        if (timeLeft <= 0) {
            timeLeft = 0;

            // Select first perk if none was chosen
            if (selectedPerk == null) {
                selectedPerk = offers.get(0);
            }

            selectionMade = true;
            game.addAttack(selectedPerk);

            game.goTo(
                new BattleScreen(game, players));
        }

        if (timerLabel != null) {
            timerLabel.setText((int) Math.ceil(timeLeft));
        }
    }

    @Override
    public void dispose() {

        super.dispose();

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        if (buttonTexture != null) {
            buttonTexture.dispose();
        }

        if (cpBarTexture != null) {
            cpBarTexture.dispose();
        }
    }
}

