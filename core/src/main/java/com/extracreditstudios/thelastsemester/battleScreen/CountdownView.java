package com.extracreditstudios.thelastsemester.battleScreen;

// displays battle countdown and updates its appearance based on remaining time

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class CountdownView {

    private static final float PAD_TOP = 35f;
    private static final float FONT_SCALE = 2f;
    private static final int WARN_SECONDS = 3;
    private final Label label;
    private int lastSeconds = -1;

    public CountdownView(Stage stage, BitmapFont font) {

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        label = new Label("", style);
        label.setAlignment(Align.center);
        label.setFontScale(FONT_SCALE);

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(PAD_TOP);
        table.add(label);

        stage.addActor(table);
    }

    public void update(float timeLeft) {

        int seconds = (int) Math.ceil(timeLeft);

        // Nur bei Änderung neu setzen
        if (seconds == lastSeconds) {
            return;
        }
        lastSeconds = seconds;

        label.setText(String.valueOf(seconds));
        label.setColor(seconds <= WARN_SECONDS ? Color.RED : Color.WHITE);
    }

    public Label getLabel() { return label; }
}

