
package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SerializationException;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.settings.RegisterScreen;

public class LoginScreen extends BaseScreen {

    private static final String LOGIN_URL =
        "https://the-last-semester.twohouseholds.com/auth/login";

    private static final String PROTOCOL_VERSION = "1.0.0";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_TOKEN = "session_token";

    private static final int STATUS_OK = 200;
    private static final int STATUS_WRONG_LOGIN = 401;

    private static final String FOLDER = "finaldesign_data/";

    private static final String BACKGROUND_FILE =
        FOLDER + "setting_background.png";

    private static final String FIELD_FILE =
        FOLDER + "Rectangle 259.png";

    private static final String LOGIN_BUTTON_FILE =
        FOLDER + "button_new_yellow.png";

    private static final String BACK_BUTTON_FILE =
        FOLDER + "button_setting.png";

    private static final float FIELD_WIDTH_FRAC = 0.82f;
    private static final float BUTTON_WIDTH_FRAC = 0.82f;

    private static final float FIELD_X = 0.51f;

    private static final float Y_USERNAME = 0.44f;
    private static final float Y_PASSWORD = 0.54f;
    private static final float Y_LOGIN = 0.64f;
    private static final float Y_REGISTER = 0.74f;

    private static final float BACK_X = 0.51f;
    private static final float Y_BACK = 0.94f;
    private static final float BACK_WIDTH_FRAC = 0.45f;

    private static final float[] STATUS_POS = {0.33f, 0.84f};

    private static final Color TEXT_DARK =
        new Color(0.1f, 0.1f, 0.1f, 1f);

    private static final Color TEXT_LIGHT =
        new Color(0.95f, 0.93f, 0.82f, 1f);

    private final DesignKit kit = new DesignKit();

    private Texture bgTexture;
    private Texture fieldTexture;
    private Texture loginButtonTexture;
    private Texture backButtonTexture;

    private TextField usernameField;
    private TextField passwordField;

    private Table loginButton;
    private Label statusLabel;

    private boolean busy = false;

    public LoginScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {

        bgTexture = kit.texture(BACKGROUND_FILE);
        fieldTexture = kit.texture(FIELD_FILE);
        loginButtonTexture = kit.texture(LOGIN_BUTTON_FILE);
        backButtonTexture = kit.texture(BACK_BUTTON_FILE);

        Board board = new Board(bgTexture);

        // Username
        usernameField = kit.textField(
            fieldTexture,
            "Username",
            false,
            20
        );

        board.place(
            usernameField,
            FIELD_X,
            Y_USERNAME,
            FIELD_WIDTH_FRAC,
            0f,
            fieldTexture.getHeight() / (float) fieldTexture.getWidth()
        );

        // Password
        passwordField = kit.textField(
            fieldTexture,
            "Password",
            true,
            128
        );

        board.place(
            passwordField,
            FIELD_X,
            Y_PASSWORD,
            FIELD_WIDTH_FRAC,
            0f,
            fieldTexture.getHeight() / (float) fieldTexture.getWidth()
        );

        // Login-Button
        loginButton = kit.imageButton(
            "Login",
            loginButtonTexture,
            TEXT_DARK,
            0.9f,
            () -> {
                if (!busy) {
                    onLoginPressed();
                }
            }
        );

        board.place(
            loginButton,
            FIELD_X,
            Y_LOGIN,
            BUTTON_WIDTH_FRAC,
            0f,
            loginButtonTexture.getHeight()
                / (float) loginButtonTexture.getWidth()
        );

        // Registrieren
        Label registerLink = kit.link(
            "Noch kein Konto? Registrieren",
            0.85f,
            () -> game.goTo(new RegisterScreen(game))
        );

        board.place(
            registerLink,
            FIELD_X,
            Y_REGISTER,
            0f,
            0f,
            0f
        );

        // Statusanzeige
        statusLabel = kit.label(
            "",
            TEXT_LIGHT,
            1f
        );

        statusLabel.setWrap(true);

        board.place(
            statusLabel,
            STATUS_POS[0],
            STATUS_POS[1],
            0.55f,
            0.08f,
            0f
        );

        // Back-Button
        Table backButton = kit.imageButton(
            "Back",
            backButtonTexture,
            Color.BLACK,
            1f,
            this::onBack
        );

        board.place(
            backButton,
            BACK_X,
            Y_BACK,
            BACK_WIDTH_FRAC,
            0f,
            backButtonTexture.getHeight()
                / (float) backButtonTexture.getWidth()
        );

        stage.addActor(board);
        board.toBack();

        stage.setKeyboardFocus(usernameField);
    }

    private void onLoginPressed() {

        if (busy) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Bitte Name und Passwort eingeben");
            return;
        }

        statusLabel.setText("Bitte warten...");
        setBusy(true);

        sendLoginRequest(username, password);
    }

    private void setBusy(boolean value) {

        busy = value;

        if (loginButton != null) {
            loginButton.setColor(
                value
                    ? new Color(0.65f, 0.65f, 0.65f, 1f)
                    : Color.WHITE
            );
        }
    }

    private void sendLoginRequest(
        String username,
        String password
    ) {

        JsonValue body =
            new JsonValue(JsonValue.ValueType.object);

        body.addChild(
            FIELD_USERNAME,
            new JsonValue(username)
        );

        body.addChild(
            FIELD_PASSWORD,
            new JsonValue(password)
        );

        Net.HttpRequest request =
            new com.badlogic.gdx.net.HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.POST)
                .url(LOGIN_URL)
                .header("Content-Type", "application/json")
                .header("protocol-version", PROTOCOL_VERSION)
                .content(body.toJson(JsonWriter.OutputType.json))
                .timeout(10000)
                .build();

        Gdx.net.sendHttpRequest(
            request,
            new Net.HttpResponseListener() {

                @Override
                public void handleHttpResponse(
                    Net.HttpResponse response
                ) {

                    int code =
                        response.getStatus().getStatusCode();

                    String answer =
                        response.getResultAsString();

                    Gdx.app.log(
                        "Login",
                        "Antwort-Code: " + code
                    );

                    if (code == STATUS_OK) {

                        String token = readToken(answer);

                        if (token != null) {
                            onLoginSuccess(token);
                        } else {
                            onLoginFailed(
                                "Unerwartete Antwort vom Server"
                            );
                        }

                    } else if (code == STATUS_WRONG_LOGIN) {

                        onLoginFailed(
                            "Name oder Passwort falsch"
                        );

                    } else if (code == 400) {

                        onLoginFailed("Ungültige Eingabe");

                    } else if (code == 404) {

                        onLoginFailed(
                            "Login-Adresse nicht gefunden (URL prüfen)"
                        );

                    } else if (code == 426) {

                        onLoginFailed(
                            "Bitte Spiel aktualisieren (Protokoll-Version)"
                        );

                    } else {

                        onLoginFailed(
                            "Serverfehler (" + code + ")"
                        );
                    }
                }

                @Override
                public void failed(Throwable t) {

                    Gdx.app.error(
                        "Login",
                        "Keine Verbindung",
                        t
                    );

                    onLoginFailed(
                        "Keine Verbindung zum Server"
                    );
                }

                @Override
                public void cancelled() {
                    onLoginFailed(
                        "Anfrage abgebrochen"
                    );
                }
            }
        );
    }

    private String readToken(String answer) {

        try {
            JsonValue json =
                new JsonReader().parse(answer);

            return json == null
                ? null
                : json.getString(FIELD_TOKEN, null);

        } catch (SerializationException e) {
            return null;
        }
    }

    private void onLoginSuccess(String token) {

        Gdx.app.postRunnable(() -> {

            Session.start(token);

            game.goTo(
                new HomeScreen(game)
            );
        });
    }

    private void onLoginFailed(String message) {

        Gdx.app.postRunnable(() -> {

            statusLabel.setText(
                "Fehler: " + message
            );

            setBusy(false);
        });
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
        private final Array<Actor> items = new Array<>();
        private final Array<float[]> pos = new Array<>();

        Board(Texture texture) {

            bg = new Image(texture);
            bg.setScaling(Scaling.stretch);

            addActor(bg);
            setFillParent(true);
        }

        void place(
            Actor actor,
            float fx,
            float fyFromTop,
            float widthFrac,
            float heightFrac,
            float aspect
        ) {

            addActor(actor);
            items.add(actor);

            pos.add(new float[]{
                fx,
                fyFromTop,
                widthFrac,
                heightFrac,
                aspect
            });
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

                    float actorHeight =
                        p[3] > 0f
                            ? p[3] * h
                            : actorWidth * p[4];

                    actor.setSize(
                        actorWidth,
                        actorHeight
                    );
                }

                actor.setPosition(
                    p[0] * w - actor.getWidth() / 2f,
                    (1f - p[1]) * h - actor.getHeight() / 2f
                );
            }
        }
    }
}
