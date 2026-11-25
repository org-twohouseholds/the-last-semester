package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.settings.SettingsScreen;

public class ChangePasswordScreen extends BaseScreen {

    private static final String CHANGE_PASSWORD_URL =
        "https://the-last-semester.twohouseholds.com/api/account/password";
    private static final String PROTOCOL_VERSION = "1.0.0";

    private static final String BACKGROUND_FILE =
        "finaldesign_data/setting_background.png";
    private static final String BUTTON_FILE =
        "finaldesign_data/button_new_yellow.png";
    private static final String BACK_BUTTON_FILE =
        "finaldesign_data/button_setting.png";

    private static final float CENTER_X = 0.51f;
    private static final float FIELD_WIDTH_FRAC = 0.78f;
    private static final float FIELD_ASPECT = 0.18f;
    private static final float BUTTON_WIDTH_FRAC = 0.76f;

    private static final float OLD_PASS_Y = 0.43f;
    private static final float NEW_PASS_Y = 0.52f;
    private static final float CONFIRM_PASS_Y = 0.61f;
    private static final float SUBMIT_Y = 0.715f;
    private static final float STATUS_Y = 0.795f;
    private static final float BACK_Y = 0.87f;

    private final DesignKit kit = new DesignKit();

    private Texture bgTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;
    private Texture whiteTex;
    private Texture cursorTex;
    private Texture selectionTex;

    private TextField oldPass;
    private TextField newPass;
    private TextField confirmPass;
    private Label statusLabel;
    private boolean submitting = false;

    public ChangePasswordScreen() {
        this((TheLastSemesterGame) Gdx.app.getApplicationListener());
    }

    public ChangePasswordScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {
        bgTexture = kit.texture(BACKGROUND_FILE);
        buttonTexture = kit.texture(BUTTON_FILE);
        backButtonTexture = kit.texture(BACK_BUTTON_FILE);

        whiteTex = solidTexture(Color.WHITE);
        cursorTex = solidTexture(DesignKit.TEXT_DARK);
        selectionTex = solidTexture(new Color(0.65f, 0.80f, 1f, 0.7f));

        float buttonAspect =
            buttonTexture.getHeight() / (float) buttonTexture.getWidth();
        float backButtonAspect =
            backButtonTexture.getHeight() / (float) backButtonTexture.getWidth();

        Board board = new Board(bgTexture, buttonAspect);

        TextField.TextFieldStyle fieldStyle = createFieldStyle();

        oldPass = createPasswordField("Old password", fieldStyle);
        newPass = createPasswordField("New password", fieldStyle);
        confirmPass = createPasswordField("Repeat new password", fieldStyle);

        Label.LabelStyle statusStyle = new Label.LabelStyle(
            kit.skin().get("default", Label.LabelStyle.class)
        );
        statusStyle.font = kit.font();
        statusStyle.fontColor = DesignKit.TEXT_DARK;

        statusLabel = new Label("", statusStyle);
        statusLabel.setAlignment(Align.center);
        statusLabel.setFontScale(1.0f);
        statusLabel.setWrap(true);

        board.place(oldPass, CENTER_X, OLD_PASS_Y, FIELD_WIDTH_FRAC, FIELD_ASPECT);
        board.place(newPass, CENTER_X, NEW_PASS_Y, FIELD_WIDTH_FRAC, FIELD_ASPECT);
        board.place(confirmPass, CENTER_X, CONFIRM_PASS_Y, FIELD_WIDTH_FRAC, FIELD_ASPECT);

        board.place(
            kit.imageButton(
                "Submit",
                buttonTexture,
                DesignKit.TEXT_DARK,
                1.3f,
                this::submitPasswordChange
            ),
            CENTER_X,
            SUBMIT_Y,
            BUTTON_WIDTH_FRAC
        );

        board.place(statusLabel, CENTER_X, STATUS_Y, FIELD_WIDTH_FRAC, 0.08f);

        board.place(
            kit.imageButton(
                "Back",
                backButtonTexture,
                Color.BLACK,
                1.2f,
                this::onBack
            ),
            CENTER_X,
            BACK_Y,
            BUTTON_WIDTH_FRAC * 0.55f,
            backButtonAspect
        );

        stage.addActor(board);
        board.toBack();
    }

    private TextField.TextFieldStyle createFieldStyle() {
        TextureRegionDrawable bg = new TextureRegionDrawable(new TextureRegion(whiteTex));
        bg.setLeftWidth(12f);
        bg.setRightWidth(12f);

        TextureRegionDrawable cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));
        cursor.setMinWidth(2f);

        TextureRegionDrawable selection = new TextureRegionDrawable(new TextureRegion(selectionTex));

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = kit.font();
        style.fontColor = DesignKit.TEXT_DARK;
        style.messageFont = kit.font();
        style.messageFontColor = new Color(0.35f, 0.35f, 0.35f, 1f);
        style.background = bg;
        style.cursor = cursor;
        style.selection = selection;
        return style;
    }

    private TextField createPasswordField(String placeholder, TextField.TextFieldStyle style) {
        TextField field = new TextField("", style);
        field.setMessageText(placeholder);
        field.setPasswordCharacter('*');
        field.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                field.setPasswordMode(focused || !field.getText().isEmpty());
            }
        });
        return field;
    }

    private void submitPasswordChange() {
        if (submitting) {
            return;
        }

        String oldPassword = oldPass.getText().trim();
        String newPassword = newPass.getText().trim();
        String confirmPassword = confirmPass.getText().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("New passwords do not match.");
            return;
        }

        String token = Session.getToken();
        if (token == null || token.isEmpty()) {
            statusLabel.setText("Not logged in.");
            return;
        }

        submitting = true;
        statusLabel.setText("Updating password...");

        String jsonBody = "{"
            + "\"oldPassword\":\"" + escapeJson(oldPassword) + "\","
            + "\"newPassword\":\"" + escapeJson(newPassword) + "\""
            + "}";

        Net.HttpRequest request = new HttpRequestBuilder().newRequest()
            .method(Net.HttpMethods.PUT)
            .url(CHANGE_PASSWORD_URL)
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
                Gdx.app.log("ChangePassword", "Status: " + code);

                Gdx.app.postRunnable(() -> {
                    submitting = false;
                    if (code >= 200 && code < 300) {
                        oldPass.setText("");
                        newPass.setText("");
                        confirmPass.setText("");
                        statusLabel.setText("Password changed!");
                    } else {
                        statusLabel.setText("Error (" + code + ")" + (body != null && !body.isEmpty() ? ": " + body : ""));
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("ChangePassword", "Request failed", t);
                Gdx.app.postRunnable(() -> {
                    submitting = false;
                    statusLabel.setText("Server unreachable.");
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    submitting = false;
                    statusLabel.setText("Request cancelled.");
                });
            }
        });
    }

    private static Texture solidTexture(Color color) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fill();
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    protected void onBack() {
        game.goTo(new SettingsScreen(game));
    }

    @Override
    public void dispose() {
        super.dispose();
        kit.dispose();
        if (whiteTex != null) whiteTex.dispose();
        if (cursorTex != null) cursorTex.dispose();
        if (selectionTex != null) selectionTex.dispose();
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
