package com.extracreditstudios.thelastsemester.battleScreen;

// displays round timer as a progress bar, updates bar based on remaining time

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class RoundTimerView {

    private static final float HEIGHT = 25f;
    private static final float PAD_TOP = 5f;
    private static final float PAD_SIDE = 5f;

    private static final Color BG_COLOR = new Color(0.20f, 0.20f, 0.20f, 0.85f);
    private static final String FILL_COLOR = "F5A623";

    private final ProgressBar bar;

    public RoundTimerView(Stage stage, UiDrawableFactory drawables, float duration) {

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = drawables.solid(BG_COLOR, HEIGHT);
        style.knob = null;
        style.knobBefore = drawables.solid(Color.valueOf(FILL_COLOR), HEIGHT);

        bar = new ProgressBar(0, duration, 0.01f, false, style);
        bar.setValue(duration);
        bar.setAnimateDuration(0f);

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(PAD_TOP);
        table.add(bar)
            .growX()
            .height(HEIGHT)
            .padLeft(PAD_SIDE)
            .padRight(PAD_SIDE);

        stage.addActor(table);
    }

    public void update(float timeLeft) {
        bar.setValue(timeLeft);
    }
}

