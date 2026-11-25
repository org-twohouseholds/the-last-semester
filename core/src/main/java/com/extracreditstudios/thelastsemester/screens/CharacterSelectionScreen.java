package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class CharacterSelectionScreen extends BaseScreen {

    private static final String[] CHARACTERS = {
        "The Hacker",
        "The Esoteric",
        "The Bitter One",
        "The Alcoholic"
    };

    private static final String[] DESCRIPTIONS = {
        "Always buried in code and terminals. Finds shortcuts nobody else notices.",
        "Guided by crystals, horoscopes, and questionable campus wisdom.",
        "Fueled by cynicism and too many group projects gone wrong.",
        "Survives every lecture through sheer willpower and questionable drinks."
    };

    // Update these paths to match the exact .png filenames in your assets folder
    private static final String[] IMAGE_PATHS = {
        "hacker.png",
        "esoteric.png",
        "bitter.png",
        "alcoholic.png"
    };

    private static final String BACKGROUND_FILE =
        "finaldesign_data/setting_background.png";

    private static final String BUTTON_FILE =
        "finaldesign_data/button_new_yellow.png";

    private static final String BACK_BUTTON_FILE =
        "finaldesign_data/button_setting.png";

    private static final float BUTTON_WIDTH_FRAC = 0.76f;

    private static final float BUTTON_X = 0.51f;
    private static final float[] BUTTON_Y = {0.45f, 0.55f, 0.65f, 0.75f};

    private static final float BACK_X = 0.51f;
    private static final float BACK_Y = 0.87f;

    private final DesignKit kit = new DesignKit();

    private Texture bgTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;

    public CharacterSelectionScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {
        bgTexture = kit.texture(BACKGROUND_FILE);
        buttonTexture = kit.texture(BUTTON_FILE);
        backButtonTexture = kit.texture(BACK_BUTTON_FILE);

        float buttonAspect =
            buttonTexture.getHeight() / (float) buttonTexture.getWidth();

        float backButtonAspect =
            backButtonTexture.getHeight() / (float) backButtonTexture.getWidth();

        Board board = new Board(bgTexture, buttonAspect);

        // Charakterauswahl
        for (int i = 0; i < CHARACTERS.length; i++) {
            final int index = i;
            board.place(
                plateButton(
                    CHARACTERS[index],
                    () -> game.goTo(
                        new CharacterInfoScreen(
                            game,
                            CHARACTERS[index],
                            DESCRIPTIONS[index],
                            IMAGE_PATHS[index]
                        )
                    )
                ),
                BUTTON_X,
                BUTTON_Y[index],
                BUTTON_WIDTH_FRAC
            );
        }

        board.place(
            backButton(),
            BACK_X,
            BACK_Y,
            BUTTON_WIDTH_FRAC * 0.55f,
            backButtonAspect
        );

        stage.addActor(board);
        board.toBack();
    }

    private Table plateButton(String text, Runnable action) {
        return kit.imageButton(
            text,
            buttonTexture,
            DesignKit.TEXT_DARK,
            1.3f,
            action
        );
    }

    private Table backButton() {
        return kit.imageButton(
            "Back",
            backButtonTexture,
            Color.BLACK,
            1.2f,
            this::onBack
        );
    }

    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }

    @Override
    public void dispose() {
        super.dispose();
        kit.dispose();
    }

    private static class Board extends WidgetGroup {

        private final Image bg;
        private final float buttonAspect;

        private final Array<Actor> items = new Array<>();
        private final Array<float[]> pos = new Array<>();

        Board(Texture texture, float buttonAspect) {
            this.buttonAspect = buttonAspect;

            bg = new Image(texture);
            bg.setScaling(Scaling.stretch);

            addActor(bg);
            setFillParent(true);
        }

        void place(Actor actor, float fx, float fyFromTop, float widthFrac) {
            place(actor, fx, fyFromTop, widthFrac, buttonAspect);
        }

        void place(Actor actor, float fx, float fyFromTop, float widthFrac, float aspect) {
            addActor(actor);
            items.add(actor);
            pos.add(new float[]{fx, fyFromTop, widthFrac, aspect});
        }

        @Override
        public void layout() {
            float w = getWidth();
            float h = getHeight();

            bg.setBounds(0, 0, w, h);

            for (int i = 0; i < items.size; i++) {
                Actor actor = items.get(i);
                float[] p = pos.get(i);

                if (p[2] > 0f) {
                    float actorWidth = p[2] * w;
                    float actorHeight = actorWidth * p[3];
                    actor.setSize(actorWidth, actorHeight);
                }

                actor.setPosition(
                    p[0] * w - actor.getWidth() / 2f,
                    (1f - p[1]) * h - actor.getHeight() / 2f
                );
            }
        }
    }
}
