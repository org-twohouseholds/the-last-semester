
package com.extracreditstudios.thelastsemester.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.DesignKit;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;

/** Register-Screen: Eingabe -> Server-Anfrage -> Ergebnis anzeigen. */
public class RegisterScreen extends BaseScreen {

    private static final String REGISTER_URL =
        "https://the-last-semester.twohouseholds.com/auth/register";

    private static final String PROTOCOL_VERSION = "1.0.0";

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";

    private static final Color TEXT_DARK =
        new Color(0.1f, 0.1f, 0.1f, 1f);

    private static final Color TEXT_LIGHT =
        new Color(0.88f, 0.70f, 0.70f, 0.70f);

    private static final Color PLACEHOLDER_COLOR =
        new Color(0.85f, 0.83f, 0.78f, 1f);

    private static final Color PASSWORD_PLACEHOLDER_COLOR =
        new Color(0.85f, 0.85f, 0.85f, 1f);

    private final DesignKit kit = new DesignKit();

    private Texture fieldTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;
    private Texture backgroundTexture;

    private TextField usernameField;
    private TextField passwordField;
    private TextField confirmPasswordField;

    private Table registerButton;
    private Label statusLabel;

    private boolean busy = false;

    public RegisterScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {

        // Hintergrund und Texturen über DesignKit laden
        backgroundTexture = kit.texture(
            "finaldesign_data/setting_background.png"
        );

        fieldTexture = kit.texture(
            "finaldesign_data/Rectangle 259.png"
        );

        buttonTexture = kit.texture(
            "finaldesign_data/button_new_yellow.png"
        );

        backButtonTexture = kit.texture(
            "finaldesign_data/button_setting.png"
        );

        root.setBackground(
            new TextureRegionDrawable(
                new TextureRegion(backgroundTexture)
            )
        );

        // Eingabefelder
        usernameField =
            createTextField("BENUTZERNAME", false, 20);

        passwordField =
            createTextField("PASSWORT", true, 128);

        confirmPasswordField =
            createTextField("PASSWORT BESTÄTIGEN", true, 128);

        // Gelber Register-Button
        registerButton = kit.imageButton(
            "Konto erstellen",
            buttonTexture,
            TEXT_DARK,
            1.3f,
            () -> {
                if (!busy) {
                    onRegisterPressed();
                }
            }
        );

        // Statusanzeige
        statusLabel = kit.label("", TEXT_LIGHT, 1.1f);
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        // Seitenverhältnis des gelben Buttons
        float buttonHeight =
            BUTTON_WIDTH
                * buttonTexture.getHeight()
                / buttonTexture.getWidth();

        // Seitenverhältnis des Back-Buttons
        float backButtonHeight =
            BUTTON_WIDTH
                * backButtonTexture.getHeight()
                / backButtonTexture.getWidth();

        // Layout
        root.add(usernameField)
            .width(BUTTON_WIDTH)
            .height(48)
            .padBottom(12)
            .row();

        root.add(passwordField)
            .width(BUTTON_WIDTH)
            .height(48)
            .padBottom(12)
            .row();

        root.add(confirmPasswordField)
            .width(BUTTON_WIDTH)
            .height(48)
            .padBottom(24)
            .row();

        root.add(registerButton)
            .width(BUTTON_WIDTH)
            .height(buttonHeight)
            .padBottom(20)
            .row();

        root.add(statusLabel)
            .width(BUTTON_WIDTH)
            .padBottom(30)
            .row();

        // Back-Button
        Table backButton = kit.imageButton(
            "Back",
            backButtonTexture,
            TEXT_DARK,
            1.2f,
            this::onBack
        );

        root.add(backButton)
            .width(BUTTON_WIDTH)
            .height(backButtonHeight)
            .row();

        stage.setKeyboardFocus(usernameField);
    }



    private TextField createTextField(
        String hint,
        boolean password,
        int maxLength
    ) {

        TextField.TextFieldStyle style =
            new TextField.TextFieldStyle(
                kit.skin().get(TextField.TextFieldStyle.class)
            );

        TextureRegionDrawable drawable =
            new TextureRegionDrawable(
                new TextureRegion(fieldTexture)
            );

        style.background = drawable;
        style.focusedBackground = drawable;

        style.font = kit.font();
        style.messageFont = kit.font();

        // Eingegebener Text
        style.fontColor = TEXT_DARK;

        // Platzhalterfarbe abhängig vom Eingabefeld
        style.messageFontColor = password
            ? PASSWORD_PLACEHOLDER_COLOR
            : PLACEHOLDER_COLOR;

        // Innenabstand links und rechts
        style.background.setLeftWidth(22f);
        style.background.setRightWidth(12f);

        TextField field = new TextField("", style);

        field.setMessageText(hint);
        field.setMaxLength(maxLength);

        if (password) {
            field.setPasswordMode(true);
            field.setPasswordCharacter('*');
        }

        return field;
    }



    private void onRegisterPressed() {

        if (busy) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!username.matches("[A-Za-z0-9_]{3,20}")) {
            statusLabel.setText(
                "Name: 3-20 Zeichen, nur Buchstaben, Zahlen und _"
            );
            return;
        }

        if (password.length() < 15 || password.length() > 128) {
            statusLabel.setText(
                "Passwort: mindestens 15 Zeichen"
            );
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText(
                "Die Passwörter stimmen nicht überein"
            );
            return;
        }

        statusLabel.setText("Bitte warten...");

        setBusy(true);

        sendRegisterRequest(username, password);
    }

    private void setBusy(boolean value) {

        busy = value;

        registerButton.setTouchable(
            value ? Touchable.disabled : Touchable.enabled
        );

        registerButton.setColor(
            value
                ? new Color(0.65f, 0.65f, 0.65f, 1f)
                : Color.WHITE
        );
    }

    private void sendRegisterRequest(
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
            new HttpRequestBuilder()
                .newRequest()
                .method(Net.HttpMethods.POST)
                .url(REGISTER_URL)
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

                    Gdx.app.log(
                        "Register",
                        "Antwort " + code + ": "
                            + response.getResultAsString()
                    );

                    if (code == 200 || code == 201) {

                        onRegisterResult(true, "Konto erstellt!");

                    } else if (code == 409) {

                        onRegisterResult(false, "Name schon vergeben");

                    } else if (code == 400) {

                        onRegisterResult(false, "Ungültige Eingabe");

                    } else if (code == 426) {

                        onRegisterResult(
                            false,
                            "Bitte Spiel aktualisieren (Protokoll-Version)"
                        );

                    } else {

                        onRegisterResult(
                            false,
                            "Serverfehler (" + code + ")"
                        );
                    }
                }

                @Override
                public void failed(Throwable t) {

                    Gdx.app.error(
                        "Register",
                        "Keine Verbindung",
                        t
                    );

                    onRegisterResult(
                        false,
                        "Keine Verbindung zum Server"
                    );
                }

                @Override
                public void cancelled() {

                    onRegisterResult(
                        false,
                        "Anfrage abgebrochen"
                    );
                }
            }
        );
    }


    public void onRegisterResult(
        boolean success,
        String message
    ) {

        Gdx.app.postRunnable(() -> {

            statusLabel.setText(
                (success ? "Erfolg: " : "Fehler: ") + message
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
}
