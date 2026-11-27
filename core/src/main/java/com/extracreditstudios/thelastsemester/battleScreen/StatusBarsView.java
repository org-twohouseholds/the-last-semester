package com.extracreditstudios.thelastsemester.battleScreen;

// displays and updates health and energy bars of both fighters

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class StatusBarsView {

    // dimensions and maximum value of the status bars
    private static final float BAR_WIDTH = 160f;
    private static final float BAR_HEIGHT = 12f;
    private static final float BAR_MAX = 100f;

    // positioning and spacing of the status bars
    private static final float PAD_TOP = 200f;    // bigger = closer to character
    private static final float PAD_SIDE = 20f;
    private static final float GAP_HP = 5f;
    private static final float GAP_ENERGY = 4f;

    // colors used for backgrounds and status bars
    private static final Color BG_COLOR = new Color(0.20f, 0.20f, 0.20f, 0.85f);
    private static final String HP_COLOR = "55C878";
    private static final String ENERGY_COLOR = "489BEE";

    // stores the status bars of both fighters
    private final ProgressBar playerHpBar;
    private final ProgressBar playerEnergyBar;
    private final ProgressBar enemyHpBar;
    private final ProgressBar enemyEnergyBar;

    private static final float LABEL_FONT_SCALE = 1.2f;

    // creates and positions status bars for both fighters
    public StatusBarsView(Stage stage, UiDrawableFactory drawables, BitmapFont font) {

        // creates the background, health and energy bar textures
        TextureRegionDrawable bg = drawables.solid(BG_COLOR, BAR_HEIGHT);
        TextureRegionDrawable hp = drawables.solid(Color.valueOf(HP_COLOR), BAR_HEIGHT);
        TextureRegionDrawable energy = drawables.solid(Color.valueOf(ENERGY_COLOR), BAR_HEIGHT);

        // creates the players bars, filled from left to right
        playerHpBar = createBar(bg, hp, false);
        playerEnergyBar = createBar(bg, energy, false);

        // creates the enemies bars, filled from right to left
        enemyHpBar = createBar(bg, hp, true);
        enemyEnergyBar = createBar(bg, energy, true);

        // creates labels for both fighters
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label playerLabel = new Label("PLAYER", labelStyle);
        playerLabel.setAlignment(Align.left);
        playerLabel.setFontScale(LABEL_FONT_SCALE);

        Label enemyLabel = new Label("ENEMY", labelStyle);
        enemyLabel.setAlignment(Align.right);
        enemyLabel.setFontScale(LABEL_FONT_SCALE);

        // arranges the players label and status bars
        Table playerTable = new Table();
        playerTable.add(playerLabel).width(BAR_WIDTH).left().row();
        playerTable.add(playerHpBar)
            .width(BAR_WIDTH).height(BAR_HEIGHT).left().padTop(GAP_HP).row();
        playerTable.add(playerEnergyBar)
            .width(BAR_WIDTH).height(BAR_HEIGHT).left().padTop(GAP_ENERGY);

        // arranges the enemies label and status bars
        Table enemyTable = new Table();
        enemyTable.add(enemyLabel).width(BAR_WIDTH).right().row();
        enemyTable.add(enemyHpBar)
            .width(BAR_WIDTH).height(BAR_HEIGHT).right().padTop(GAP_HP).row();
        enemyTable.add(enemyEnergyBar)
            .width(BAR_WIDTH).height(BAR_HEIGHT).right().padTop(GAP_ENERGY);

        // positions both status bar groups at the top of the screen
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(PAD_TOP);
        table.add(playerTable).expandX().left().padLeft(PAD_SIDE);
        table.add(enemyTable).expandX().right().padRight(PAD_SIDE);

        stage.addActor(table);
    }

    // creates a progress bar with the specified fill direction
    private ProgressBar createBar(
        TextureRegionDrawable background,
        TextureRegionDrawable fill,
        boolean reverse
    ) {

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = background;
        style.knob = null;

        if (reverse) {
            // fills the bar from right to left
            style.knobBefore = null;
            style.knobAfter = fill;
        } else {
            // fills the bar from left to right
            style.knobBefore = fill;
            style.knobAfter = null;
        }

        ProgressBar bar = new ProgressBar(0, BAR_MAX, 1, false, style);
        bar.setValue(BAR_MAX);
        bar.setAnimateDuration(0.15f);
        return bar;
    }

    // updates health and energy bars using the current battle data
    public void update(Battle battle) {

        // updates the players status bars
        playerHpBar.setValue(battle.player.hp);
        playerEnergyBar.setValue(battle.player.energy);

        // updates the enemies status bars in reverse direction
        enemyHpBar.setValue(enemyHpBar.getMaxValue() - battle.enemy.hp);
        enemyEnergyBar.setValue(enemyEnergyBar.getMaxValue() - battle.enemy.energy);
    }
}

