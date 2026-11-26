package com.extracreditstudios.thelastsemester.screens.old;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;

/** Register-Screen: Eingabe -> Server-Anfrage -> Ergebnis anzeigen. */
public class OldCopyRegisterScreen extends BaseScreen {

    // Server-Adresse für die Registrierung (vom Server-Team)
    private static final String REGISTER_URL = "https://the-last-semester.twohouseholds.com/auth/register";

    // Muss exakt Protocol.VERSION auf dem Server entsprechen, sonst antwortet der Server mit 426
    private static final String PROTOCOL_VERSION = "1.0.0";

    // Feldnamen wie in Register.Request(String username, String password) auf dem Server
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";

    // Style-Name aus design_sammlung.json (ggf. anpassen)
    private static final String BUTTON_STYLE = "second";

    private final UIFactory ui;
    private final Skin lobbySkin;

    private TextField usernameField;
    private TextField passwordField;
    private TextButton registerButton;
    private Label statusLabel;

    public OldCopyRegisterScreen(TheLastSemesterGame game) {
        super(game);
        this.lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        this.ui = new UIFactory(lobbySkin);
    }

    /** Titel, Eingabefelder, Buttons und Statusanzeige */
    @Override
    protected void buildUi() {
        Label title = ui.createLabel("Register", "title");

        usernameField = ui.createTextField("Username"); // Hinweistext im leeren Feld
        usernameField.setMaxLength(20);

        passwordField = ui.createTextField("Password");
        passwordField.setPasswordMode(true); // Eingabe verdeckt
        passwordField.setPasswordCharacter('*');
        passwordField.setMaxLength(128);

        registerButton = lobbyButton("Register", this::onRegisterPressed);

        statusLabel = ui.createLabel("", "small");
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        root.add(title).padBottom(30).row();
        root.add(usernameField).width(UIFactory.FULL_BUTTON_WIDTH).height(48).padBottom(12).row();
        root.add(passwordField).width(UIFactory.FULL_BUTTON_WIDTH).height(48).padBottom(24).row();
        root.add(registerButton).width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).padBottom(20).row();
        root.add(statusLabel).width(UIFactory.FULL_BUTTON_WIDTH).padBottom(30).row();
        root.add(lobbyButton("Back", "default", this::onBack))
            .width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).row();


        stage.setKeyboardFocus(usernameField); // Cursor direkt im Namensfeld
    }


    /** Button im Lobby-Design, löst beim Klick dieselbe Aktion aus wie vorher. */
    private TextButton lobbyButton(String text, Runnable action) {
        return lobbyButton(text, BUTTON_STYLE, action);
    }

    private TextButton lobbyButton(String text, String style, Runnable action) {
        TextButton btn = ui.createFullWidthButton(text, style);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return btn;
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
        // game.goTo(new HomeScreen(game));
        game.goTo(new HomeScreen(game));
    }

    @Override
    public void dispose() {
        super.dispose();
        lobbySkin.dispose(); // eigener Skin -> freigeben
    }
}
