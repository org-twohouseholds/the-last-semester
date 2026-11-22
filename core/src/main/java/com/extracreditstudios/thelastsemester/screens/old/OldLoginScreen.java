
package com.extracreditstudios.thelastsemester.screens.old;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;
import com.extracreditstudios.thelastsemester.screens.settings.RegisterScreen;
import com.extracreditstudios.thelastsemester.screens.Session;

/** Login-Screen: Name + Passwort -> Server prüft -> bei Erfolg Token merken und zum Homescreen. */
public class OldLoginScreen extends BaseScreen {

    private static final String LOGIN_URL =
        "https://the-last-semester.twohouseholds.com/auth/login";

    private static final String PROTOCOL_VERSION = "1.0.0";

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_TOKEN = "session_token";

    private static final int STATUS_OK = 200;
    private static final int STATUS_WRONG_LOGIN = 401;

    private static final Color TEXT_DARK =
        new Color(0.1f, 0.1f, 0.1f, 1f);

    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;
    private Label statusLabel;

    private TextureAtlas fontAtlas;
    private BitmapFont patrickFont;

    public OldLoginScreen(TheLastSemesterGame game) {
        super(game);
    }

    /** Baut Titel, Eingabefelder, Buttons und Statusanzeige auf */
    @Override
    protected void buildUi() {

        // Patrick-Hand-Schrift laden
        fontAtlas = new TextureAtlas(
            Gdx.files.internal("font_data/font_1.atlas")
        );

        TextureRegion fontRegion =
            fontAtlas.findRegion("PatrickHand-Regular");

        if (fontRegion == null) {
            throw new IllegalStateException(
                "PatrickHand-Regular wurde im Font-Atlas nicht gefunden."
            );
        }

        patrickFont = new BitmapFont(
            Gdx.files.internal("font_data/PatrickHand-Regular.fnt"),
            fontRegion,
            false
        );

        patrickFont.setUseIntegerPositions(false);

        Label title = new Label("Login", skin);
        title.setFontScale(1.6f);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        usernameField.setMaxLength(20);

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMaxLength(128);

        loginButton = button("Login", this::onLoginPressed);

        statusLabel = new Label("", skin);
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        // Registrierungslink mit Patrick Hand
        Label.LabelStyle registerStyle =
            new Label.LabelStyle(skin.get(Label.LabelStyle.class));

        registerStyle.font = patrickFont;
        registerStyle.fontColor = TEXT_DARK;

        Label registerLink = new Label(
            "Noch kein Konto? Registrieren",
            registerStyle
        );

        registerLink.setFontScale(1.15f);
        registerLink.setAlignment(Align.center);
        registerLink.setTouchable(Touchable.enabled);

        registerLink.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {
                game.goTo(new RegisterScreen(game));
            }
        });

        // Layout
        root.add(title).padBottom(30).row();

        root.add(usernameField)
            .width(BUTTON_WIDTH)
            .height(48)
            .padBottom(12)
            .row();

        root.add(passwordField)
            .width(BUTTON_WIDTH)
            .height(48)
            .padBottom(24)
            .row();

        root.add(loginButton)
            .width(BUTTON_WIDTH)
            .height(BUTTON_HEIGHT)
            .padBottom(20)
            .row();

        root.add(statusLabel)
            .width(BUTTON_WIDTH)
            .padBottom(20)
            .row();

        root.add(registerLink)
            .width(BUTTON_WIDTH)
            .height(32)
            .padBottom(12)
            .row();

        /** TODO: zum Testen... wird später entfernt */
        root.add(button("Back", this::onBack))
            .width(BUTTON_WIDTH)
            .height(BUTTON_HEIGHT)
            .row();

        stage.setKeyboardFocus(usernameField);
    }

    /** Prüft, ob beide Felder ausgefüllt sind */
    private void onLoginPressed() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Bitte Name und Passwort eingeben");
            return;
        }

        statusLabel.setText("Bitte warten...");
        loginButton.setDisabled(true);

        sendLoginRequest(username, password);
    }

    /** Name + Passwort wird als JSON an den Server geschickt und Antwort wird ausgewertet */
    private void sendLoginRequest(String username, String password) {

        JsonValue body =
            new JsonValue(JsonValue.ValueType.object);

        body.addChild(FIELD_USERNAME, new JsonValue(username));
        body.addChild(FIELD_PASSWORD, new JsonValue(password));

        Net.HttpRequest request =
            new HttpRequestBuilder().newRequest()
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
                public void handleHttpResponse(Net.HttpResponse response) {

                    int code = response.getStatus().getStatusCode();
                    String answer = response.getResultAsString();

                    Gdx.app.log("Login", "Antwort-Code: " + code);

                    if (code == STATUS_OK) {

                        String token = readToken(answer);

                        if (token != null) {
                            onLoginSuccess(token);
                        } else {
                            onLoginFailed("Unerwartete Antwort vom Server");
                        }

                    } else if (code == STATUS_WRONG_LOGIN) {

                        onLoginFailed("Name oder Passwort falsch");

                    } else if (code == 400) {

                        onLoginFailed("Ungültige Eingabe");

                    } else if (code == 404) {

                        onLoginFailed("Login-Adresse nicht gefunden (URL prüfen)");

                    } else if (code == 426) {

                        onLoginFailed("Bitte Spiel aktualisieren (Protokoll-Version)");

                    } else {

                        onLoginFailed("Serverfehler (" + code + ")");
                    }
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.error("Login", "Keine Verbindung", t);
                    onLoginFailed("Keine Verbindung zum Server");
                }

                @Override
                public void cancelled() {
                    onLoginFailed("Anfrage abgebrochen");
                }
            }
        );
    }

    /** Holt den Token aus der Server-Antwort */
    private String readToken(String answer) {

        try {
            JsonValue json = new JsonReader().parse(answer);
            return json == null ? null : json.getString(FIELD_TOKEN, null);

        } catch (SerializationException e) {
            return null;
        }
    }

    /** Login erfolgreich, Token merken -> zurück zum Homescreen */
    private void onLoginSuccess(String token) {

        Gdx.app.postRunnable(() -> {
            Session.start(token);
            game.goTo(new HomeScreen(game));
        });
    }

    /** Login hat nicht geklappt, Meldung anzeigen */
    private void onLoginFailed(String message) {

        Gdx.app.postRunnable(() -> {
            statusLabel.setText("Fehler: " + message);
            loginButton.setDisabled(false);
        });
    }

    /** TODO: Entfernen, sobald Navigation klar ist mit Login, Register usw. */
    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }

    @Override
    public void dispose() {

        super.dispose();

        if (patrickFont != null) {
            patrickFont.dispose();
        }

        if (fontAtlas != null) {
            fontAtlas.dispose();
        }
    }
}
