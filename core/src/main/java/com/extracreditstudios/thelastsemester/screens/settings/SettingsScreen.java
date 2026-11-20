
package com.extracreditstudios.thelastsemester.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.DesignKit;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;
import com.extracreditstudios.thelastsemester.screens.LoginScreen;

public class SettingsScreen extends BaseScreen {

    private static final String TAG = "Settings";

    private static final String PRIVACY_POLICY_URL =
        "https://example.org/datenschutz";

    private static final String BACKGROUND_FILE =
        "finaldesign_data/setting_background.png";

    private static final String BUTTON_FILE =
        "finaldesign_data/button_new_yellow.png";

    private static final String BACK_BUTTON_FILE =
        "finaldesign_data/button_setting.png";

    private static final float BUTTON_WIDTH_FRAC = 0.82f;

    private static final float BUTTON_X = 0.51f;
    private static final float[] BUTTON_Y = {0.51f, 0.62f, 0.73f};

    private static final float BACK_X = 0.51f;
    private static final float BACK_Y = 0.86f;

    //private static final Color TEXT_DARK =
      //  new Color(0.1f, 0.1f, 0.1f, 1f);

    private final DesignKit kit = new DesignKit();

    private Texture bgTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;

    public SettingsScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {

        // Texturen über DesignKit laden
        bgTexture = kit.texture(BACKGROUND_FILE);
        buttonTexture = kit.texture(BUTTON_FILE);
        backButtonTexture = kit.texture(BACK_BUTTON_FILE);

        float buttonAspect =
            buttonTexture.getHeight() / (float) buttonTexture.getWidth();

        float backButtonAspect =
            backButtonTexture.getHeight() / (float) backButtonTexture.getWidth();

        Board board = new Board(bgTexture, buttonAspect);

        // Privacy Policy
        board.place(
            plateButton(
                "Privacy Policy",
                () -> Gdx.net.openURI(PRIVACY_POLICY_URL)
            ),
            BUTTON_X,
            BUTTON_Y[0],
            BUTTON_WIDTH_FRAC
        );

        // Log Out
        board.place(
            plateButton("Log Out", this::logOut),
            BUTTON_X,
            BUTTON_Y[1],
            BUTTON_WIDTH_FRAC
        );

        // Delete Account
        board.place(
            plateButton(
                "Delete Account",
                this::showDeleteAccountDialog
            ),
            BUTTON_X,
            BUTTON_Y[2],
            BUTTON_WIDTH_FRAC
        );

        // Back-Button unter den drei gelben Buttons
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



    private void showDeleteAccountDialog() {

        // Dialog weiterhin mit dem BaseScreen-Skin erstellen
        Dialog dialog = new Dialog(
            "Delete Account",
            skin,
            "dialog"
        ) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    Gdx.app.log(
                        TAG,
                        "Konto löschen bestätigt (noch ohne Server)"
                    );
                }
            }
        };


        Label.LabelStyle titleStyle = new Label.LabelStyle(
            dialog.getTitleLabel().getStyle()
        );

        titleStyle.font = kit.font();

        dialog.getTitleLabel().setStyle(titleStyle);
        dialog.getTitleLabel().setFontScale(1.3f);

        // Warnhinweis mit Patrick Hand
        Label.LabelStyle warningStyle = new Label.LabelStyle(
            kit.skin().get("default", Label.LabelStyle.class)
        );

        warningStyle.font = kit.font();

        Label warning = new Label(
            "Do you really want to delete your account?\nThis cannot be undone.",
            warningStyle
        );

        warning.setFontScale(1.2f);
        warning.setWrap(true);

        dialog.getContentTable()
            .pad(16)
            .add(warning)
            .width(280);


        TextButton deleteButton = kit.button("Delete", "second");
        TextButton cancelButton = kit.button("Cancel", "second");

        deleteButton.getLabel().setFontScale(1.2f);
        cancelButton.getLabel().setFontScale(1.2f);

        dialog.button(deleteButton, true);
        dialog.button(cancelButton, false);

        dialog.show(stage);
    }



    private void logOut() {
        Logout.run(() -> game.goTo(new LoginScreen(game)));
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

        void place(
            Actor actor,
            float fx,
            float fyFromTop,
            float widthFrac
        ) {
            place(
                actor,
                fx,
                fyFromTop,
                widthFrac,
                buttonAspect
            );
        }

        void place(
            Actor actor,
            float fx,
            float fyFromTop,
            float widthFrac,
            float aspect
        ) {
            addActor(actor);

            items.add(actor);
            pos.add(
                new float[]{fx, fyFromTop, widthFrac, aspect}
            );
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
