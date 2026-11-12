package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

// Ab hier neu:
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Register-Screen */
public class CopyRegisterScreen extends BaseScreen {

    // Kopie von Yannis' code.


    private static final String SERVER_URL = "https://the-last-semester.twohouseholds.com";
    private String sessionToken;


    private TextField usernameField;
    private TextField passwordField;
    private TextButton registerButton;
    private Label statusLabel;

    public CopyRegisterScreen(TheLastSemesterGame game) {
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

    /** Prüft die Eingaben und startet die Anfrage. */
    private void onRegisterPressed() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!username.matches("[A-Za-z0-9_]{3,20}")) {
            statusLabel.setText("Username muss 3-20 Zeichen enthalten");
            return;
        }

        if (password.length() < 15 || password.length() > 128) {
            statusLabel.setText("Passwort muss 15-128 Zeichen lang sein");
            return;
        }

        statusLabel.setText("Bitte warten...");
        registerButton.setDisabled(true); // verhindert doppeltes Absenden
        sendRegisterRequest(username, password);
    }

    // TODO (Server-Team): echten API-Aufruf einbauen und danach onRegisterResult(...) aufrufen.
    private void sendRegisterRequest(String username, String password) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(SERVER_URL + "/auth/register");
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                    "Content-Type", "application/json; charset=UTF-8"
                );
                connection.setRequestProperty("protocol-version", "1.0.0");

                JsonValue requestJson = new JsonValue(JsonValue.ValueType.object);
                requestJson.addChild("username", new JsonValue(username));
                requestJson.addChild("password", new JsonValue(password));

                byte[] requestBody = requestJson
                    .toJson(JsonWriter.OutputType.json)
                    .getBytes(StandardCharsets.UTF_8);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(requestBody);
                }

                int responseCode = connection.getResponseCode();

                InputStream responseStream = responseCode >= 200
                    && responseCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();

                String responseBody = "";

                if (responseStream != null) {
                    try (InputStream input = responseStream) {
                        responseBody = new String(
                            input.readAllBytes(),
                            StandardCharsets.UTF_8
                        );
                    }
                }

                if (responseCode == 201) {
                    JsonValue responseJson = new JsonReader().parse(responseBody);
                    String token = responseJson.getString("session_token");

                    onRegisterResult(true, "Konto erfolgreich erstellt!", token);

                } else if (responseCode == 409) {
                    onRegisterResult(false, "Username bereits vergeben", null);

                } else if (responseCode == 400) {
                    onRegisterResult(false, "Ungültige Eingaben", null);

                } else if (responseCode == 426) {
                    onRegisterResult(false, "Spielversion nicht aktuell", null);

                } else {
                    onRegisterResult(false, "Serverfehler: " + responseCode, null);
                }

            } catch (Exception e) {
                Gdx.app.error("Register", "Registrierungsanfrage fehlgeschlagen", e);
                onRegisterResult(false, "Server nicht erreichbar", null);

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }, "register-request").start();
    }

    /** Zeigt das Ergebnis an */
    public void onRegisterResult(boolean success, String message, String token) {
        Gdx.app.postRunnable(() -> {
            statusLabel.setText(message);
            registerButton.setDisabled(false);

            if (success) {
                sessionToken = token;
                passwordField.setText("");
            }
        });
    }

    /** Zurück zum Homescreen. */
    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }
}
