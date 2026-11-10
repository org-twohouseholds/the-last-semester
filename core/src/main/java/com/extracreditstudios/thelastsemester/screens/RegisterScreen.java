package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

/** Register-Screen */
public class RegisterScreen extends BaseScreen {

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

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Bitte Name und Passwort eingeben");
            return;
        }

        statusLabel.setText("Bitte warten...");
        registerButton.setDisabled(true); // verhindert doppeltes Absenden
        sendRegisterRequest(username, password);
    }

    // TODO (Server-Team): echten API-Aufruf einbauen und danach onRegisterResult(...) aufrufen.
    private void sendRegisterRequest(String username, String password) {
        // Platzhalter: simuliert nach 1 Sekunde eine erfolgreiche Server-Antwort
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                onRegisterResult(true, "Konto erstellt! (Test ohne Server)");
            }
        }, 1f);
    }

    /** Zeigt das Ergebnis an */
    public void onRegisterResult(boolean success, String message) {
        Gdx.app.postRunnable(() -> { // zurück in den UI-Thread
            statusLabel.setText((success ? "Erfolg: " : "Fehler: ") + message);
            registerButton.setDisabled(false);
        });
    }

    /** Zurück zum Homescreen. */
    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }
}
