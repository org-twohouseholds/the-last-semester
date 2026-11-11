package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

/** Register-Screen: Eingabe -> Server-Anfrage -> Ergebnis anzeigen. */
public class RegisterScreen extends BaseScreen {

    // Server-Adresse für die Registrierung (vom Server-Team)
    private static final String REGISTER_URL = "https://the-last-semester.twohouseholds.com/auth/register";

    // Muss exakt Protocol.VERSION auf dem Server entsprechen, sonst antwortet der Server mit 426
    private static final String PROTOCOL_VERSION = "1.0.0";

    // Feldnamen wie in Register.Request(String username, String password) auf dem Server
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";

    private TextField usernameField;
    private TextField passwordField;
    private TextButton registerButton;
    private Label statusLabel;

    public RegisterScreen(TheLastSemesterGame game) {
        super(game);
    }

    /** Titel, Eingabefelder, Buttons und Statusanzeige */
    @Override
    protected void buildUi() {
        Label title = new Label("Register", skin);
        title.setFontScale(1.6f);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username"); // Hinweistext im leeren Feld
        usernameField.setMaxLength(20);

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true); // Eingabe verdeckt
        passwordField.setPasswordCharacter('*');
        passwordField.setMaxLength(128);

        registerButton = button("Register", this::onRegisterPressed);

        statusLabel = new Label("", skin);
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        root.add(title).padBottom(30).row();
        root.add(usernameField).width(BUTTON_WIDTH).height(48).padBottom(12).row();
        root.add(passwordField).width(BUTTON_WIDTH).height(48).padBottom(24).row();
        root.add(registerButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(20).row();
        root.add(statusLabel).width(BUTTON_WIDTH).padBottom(30).row();
        root.add(button("Back", this::onBack)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).row();

        stage.setKeyboardFocus(usernameField); // Cursor direkt im Namensfeld
    }

    /** Prüft Eingaben und startet die Anfrage. */
    private void onRegisterPressed() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!username.matches("[A-Za-z0-9_]{3,20}")) {
            statusLabel.setText("Name: 3-20 Zeichen, nur Buchstaben, Zahlen und _");
            return;
        }
        if (password.length() < 15 || password.length() > 128) {
            statusLabel.setText("Passwort: mindestens 15 Zeichen");
            return;
        }

        statusLabel.setText("Bitte warten...");
        registerButton.setDisabled(true); // verhindert doppeltes Absenden
        sendRegisterRequest(username, password);
    }

    /** Schickt Name + Passwort als JSON an den Server und wertet die Antwort aus. */
    private void sendRegisterRequest(String username, String password) {
        // JSON-Inhalt bauen
        JsonValue body = new JsonValue(JsonValue.ValueType.object);
        body.addChild(FIELD_USERNAME, new JsonValue(username));
        body.addChild(FIELD_PASSWORD, new JsonValue(password));

        // Anfrage zusammenbauen: POST an die Register-Adresse
        Net.HttpRequest request = new HttpRequestBuilder().newRequest()
            .method(Net.HttpMethods.POST)
            .url(REGISTER_URL)
            .header("Content-Type", "application/json")
            .header("protocol-version", PROTOCOL_VERSION) // Server prüft die Protokoll-Version
            .content(body.toJson(JsonWriter.OutputType.json))
            .timeout(10000) // nach 10 Sekunden aufgeben
            .build();

        // Abschicken; die Antwort kommt später in einem anderen Thread an
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                int code = response.getStatus().getStatusCode();
                Gdx.app.log("Register", "Antwort " + code + ": " + response.getResultAsString());
                if (code == 200 || code == 201) {
                    onRegisterResult(true, "Konto erstellt!");
                } else if (code == 409) {
                    onRegisterResult(false, "Name schon vergeben");
                } else if (code == 400) {
                    onRegisterResult(false, "Ungültige Eingabe");
                } else if (code == 426) {
                    onRegisterResult(false, "Bitte Spiel aktualisieren (Protokoll-Version)");
                } else {
                    onRegisterResult(false, "Serverfehler (" + code + ")");
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("Register", "Keine Verbindung", t);
                onRegisterResult(false, "Keine Verbindung zum Server");
            }

            @Override
            public void cancelled() {
                onRegisterResult(false, "Anfrage abgebrochen");
            }
        });
    }

    /** Zeigt das Ergebnis an, darf aus jedem Thread aufgerufen werden */
    public void onRegisterResult(boolean success, String message) {
        Gdx.app.postRunnable(() -> { // zurück in den UI-Thread
            statusLabel.setText((success ? "Erfolg: " : "Fehler: ") + message);
            registerButton.setDisabled(false);
        });
    }

    /** Zurück zum Homescreen */
    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }
}
