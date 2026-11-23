package com.extracreditstudios.thelastsemester.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.ChangePasswordScreen;
import com.extracreditstudios.thelastsemester.screens.DesignKit;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;
import com.extracreditstudios.thelastsemester.screens.Session;

public class SettingsScreen extends BaseScreen {

    private static final String TAG = "Settings";

    private static final String DELETE_ACCOUNT_URL =
        "https://the-last-semester.twohouseholds.com/api/account/delete";
    private static final String PROTOCOL_VERSION = "1.0.0";

    private static final String PRIVACY_POLICY_URL =
        "https://example.org/datenschutz";

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

    //private static final Color TEXT_DARK =
    // new Color(0.1f, 0.1f, 0.1f, 1f);

    private final DesignKit kit = new DesignKit();

    private Texture bgTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;

    public SettingsScreen(TheLastSemesterGame game) { super(game); }

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

        // Change Password
        board.place(
            plateButton(
                "Change Password",
                () -> game.goTo(new ChangePasswordScreen())
            ),
            BUTTON_X,
            BUTTON_Y[1],
            BUTTON_WIDTH_FRAC
        );

        // Log Out
        board.place(
            plateButton("Log Out", this::logOut),
            BUTTON_X,
            BUTTON_Y[2],
            BUTTON_WIDTH_FRAC
        );

        // Delete Account
        board.place(
            plateButton(
                "Delete Account",
                this::showDeleteAccountDialog
            ),
            BUTTON_X,
            BUTTON_Y[3],
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
        TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Enter your password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        Label statusLabel = new Label("", skin);
        statusLabel.setFontScale(1.0f);
        statusLabel.setWrap(true);

        TextButton deleteButton = kit.button("Delete", "second");
        TextButton cancelButton = kit.button("Cancel", "second");

        // Dialog weiterhin mit dem BaseScreen-Skin erstellen
        Dialog dialog = new Dialog(
            "Delete Account",
            skin,
            "dialog"
        ) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    cancel();
                    deleteAccount(this, passwordField, statusLabel, deleteButton);
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
        statusLabel.setStyle(warningStyle);

        Label warning = new Label(
            "Do you really want to delete your account?\nThis cannot be undone.",
            warningStyle
        );

        warning.setFontScale(1.2f);
        warning.setWrap(true);

        dialog.getContentTable()
            .pad(16)
            .add(warning)
            .width(280)
            .row();
        dialog.getContentTable()
            .add(passwordField)
            .width(280)
            .padTop(12)
            .row();
        dialog.getContentTable()
            .add(statusLabel)
            .width(280)
            .padTop(8);

        deleteButton.getLabel().setFontScale(1.2f);
        cancelButton.getLabel().setFontScale(1.2f);

        dialog.button(deleteButton, true);
        dialog.button(cancelButton, false);

        dialog.show(stage);
    }

    private void deleteAccount(
        Dialog dialog,
        TextField passwordField,
        Label statusLabel,
        TextButton deleteButton
    ) {
        if (deleteButton.isDisabled()) {
            return;
        }

        String password = passwordField.getText().trim();
        if (password.isEmpty()) {
            statusLabel.setText("Password is required.");
            return;
        }

        String token = Session.getToken();
        if (token == null || token.isEmpty()) {
            statusLabel.setText("Not logged in.");
            return;
        }

        deleteButton.setDisabled(true);
        statusLabel.setText("Deleting account...");

        String jsonBody = "{\"password\":\"" + escapeJson(password) + "\"}";

        Net.HttpRequest request = new HttpRequestBuilder().newRequest()
            .method(Net.HttpMethods.POST)
            .url(DELETE_ACCOUNT_URL)
            .header("protocol-version", PROTOCOL_VERSION)
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .content(jsonBody)
            .timeout(10000)
            .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                int code = response.getStatus().getStatusCode();
                String body = response.getResultAsString();
                Gdx.app.log(TAG, "Delete account status: " + code);

                Gdx.app.postRunnable(() -> {
                    if (code >= 200 && code < 300) {
                        dialog.hide();
                        Session.end();
                        game.goTo(new LoginScreen(game));
                    } else {
                        deleteButton.setDisabled(false);
                        statusLabel.setText("Failed (" + code + ")" + (body != null && !body.isEmpty() ? ": " + body : ""));
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error(TAG, "Delete account failed", t);
                Gdx.app.postRunnable(() -> {
                    deleteButton.setDisabled(false);
                    statusLabel.setText("Server unreachable.");
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    deleteButton.setDisabled(false);
                    statusLabel.setText("Request cancelled.");
                });
            }
        });
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void logOut() { Logout.run(() -> game.goTo(new LoginScreen(game))); }

    @Override
    protected void onBack() { game.goTo(new HomeScreen(game)); }

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
