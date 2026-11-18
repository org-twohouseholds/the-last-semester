package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

/** Login-Screen: Name + Passwort -> Server prüft -> bei Erfolg Token merken und zum Homescreen. */
public class LoginScreen extends BaseScreen {

    /** TODO: LOGIN_URL nachfragen und ändern */
    private static final String LOGIN_URL = "https://the-last-semester.twohouseholds.com/auth/login";

    /** TODO: Prüft der Server beim Login auch die Protokoll-Version? Wenn ja: welche? (ANNAHME: "1.0.0") */
    private static final String PROTOCOL_VERSION = "1.0.0";

    /** TODO: Nachfrage ob Felder im JSON gleich heissen wie bei Register also wie unten... */
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";

    /** TODO: Wie heisst das Feld mit dem Token in der Antwort? "session_token" wie unten? */
    private static final String FIELD_TOKEN = "session_token";

    /** TODO: Welcher Statuscode kommt bei erfolgreichem Login und welcher bei falscher Eingabe von
            Passwort oder Benutzername? */
    private static final int STATUS_OK = 200;
    private static final int STATUS_WRONG_LOGIN = 401;

    // ====================================================================================

    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;
    private Label statusLabel;

    public LoginScreen(TheLastSemesterGame game) {
        super(game);
    }

    /** Baut Titel, Eingabefelder, Buttons und Statusanzeige auf */
    @Override
    protected void buildUi() {
        Label title = new Label("Login", skin);
        title.setFontScale(1.6f);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username"); // Hinweistext im leeren Feld
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

        root.add(title).padBottom(30).row();
        root.add(usernameField).width(BUTTON_WIDTH).height(48).padBottom(12).row();
        root.add(passwordField).width(BUTTON_WIDTH).height(48).padBottom(24).row();
        root.add(loginButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(20).row();
        root.add(statusLabel).width(BUTTON_WIDTH).padBottom(30).row();

        // Button für Registrierung
        root.add(button("Noch kein Konto? Registrieren", () -> game.goTo(new CopyRegisterScreen(game))))
            .width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(12).row();

        /** TODO: zum Testen... wird später entfernt */
        root.add(button("Back", this::onBack)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).row();

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
        loginButton.setDisabled(true); // lockt den Button
        sendLoginRequest(username, password);
    }

    /** Name + Passwort wird als JSON an den Server geschickt und Antwort wird ausgewertet */
    private void sendLoginRequest(String username, String password) {
        // JSON-Inhalt
        JsonValue body = new JsonValue(JsonValue.ValueType.object);
        body.addChild(FIELD_USERNAME, new JsonValue(username));
        body.addChild(FIELD_PASSWORD, new JsonValue(password));

        // Anfrage zusammenbauen
        Net.HttpRequest request = new HttpRequestBuilder().newRequest()
            .method(Net.HttpMethods.POST)
            .url(LOGIN_URL)
            .header("Content-Type", "application/json")
            .header("protocol-version", PROTOCOL_VERSION) // Server prüft die Protokoll-Version
            .content(body.toJson(JsonWriter.OutputType.json))
            .timeout(10000)
            .build();

        // Abschicken... die Antwort kommt später in einem anderen Thread an
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                int code = response.getStatus().getStatusCode();
                String answer = response.getResultAsString(); // darf nur EINMAL gelesen werden
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
        });
    }

    /** Holt den Token aus der Server-Antwort */
    private String readToken(String answer) {
        try {
            JsonValue json = new JsonReader().parse(answer);
            return json == null ? null : json.getString(FIELD_TOKEN, null);
        } catch (SerializationException e) {
            return null; // Antwort war kein gültiges JSON
        }
    }

    /** Login erfolgreich, Token merken -> zurück zum Homescreen */
    private void onLoginSuccess(String token) {
        Gdx.app.postRunnable(() -> {
            Session.start(token);
            game.goTo(new CopyHomeScreen(game));
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
        game.goTo(new CopyHomeScreen(game));
    }
}
