package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class CopySettingsScreen extends BaseScreen {

    private static final String TAG = "Settings";

    // TODO: echte Adresse als URL einfügen
    private static final String PRIVACY_POLICY_URL = "https://example.org/datenschutz";

    // Style-Name aus design_sammlung.json
    private static final String BUTTON_STYLE = "second";

    private final UIFactory ui;
    private final Skin lobbySkin;

    public CopySettingsScreen(TheLastSemesterGame game) {
        super(game);
        this.lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        this.ui = new UIFactory(lobbySkin);
    }

    @Override
    protected void buildUi() {
        Label title = ui.createLabel("Settings", "title");
        title.setAlignment(Align.center);

        root.top().padTop(60);
        root.defaults().width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).padBottom(12);

        // Titel soll NICHT Button-Größe haben -> eigene Angaben
        root.add(title).width(UIFactory.FULL_BUTTON_WIDTH).height(60).padBottom(24).row();

        root.add(lobbyButton("Privacy Policy", () -> Gdx.net.openURI(PRIVACY_POLICY_URL))).row();
        root.add(lobbyButton("Log Out", this::logOut)).row();
        root.add(lobbyButton("Delete Account", this::showDeleteAccountDialog)).padBottom(40).row();
        root.add(lobbyButton("Back", "default", this::onBack)).row(); // <- geändert: grüner Style
    }

    /** Button im Lobby-Design (Standard-Style). */
    private TextButton lobbyButton(String text, Runnable action) {
        return lobbyButton(text, BUTTON_STYLE, action);
    }

    /** Wie oben, aber mit frei wählbarem Style. */
    private TextButton lobbyButton(String text, String style, Runnable action) { // <- neu
        TextButton btn = ui.createFullWidthButton(text, style);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return btn;
    }

    /** Sicherheitsabfrage **/
    private void showDeleteAccountDialog() {
        Dialog dialog = new Dialog("Delete Account", skin, "dialog") {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    Gdx.app.log(TAG, "Konto löschen bestätigt (noch ohne Server)");
                }
            }
        };
        Label warning = new Label("Do you really want to delete your account?\nThis cannot be undone.", skin);
        warning.setWrap(true);
        dialog.getContentTable().pad(16).add(warning).width(280);
        dialog.button("Delete", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
    }

    private void logOut() {
        // TODO: Sitzung beim Server beenden und zum Login-Screen wechseln
        Gdx.app.log(TAG, "Log Out gedrückt (noch ohne Server)");
    }

    /** Zurück-Taste oder "Back"-Button: zum Homescreen. */
    @Override
    protected void onBack() {
        // game.goTo(new HomeScreen(game));
        game.goTo(new CopyHomeScreen(game));
    }

    @Override
    public void dispose() {
        super.dispose();
        lobbySkin.dispose(); // eigener Skin -> freigeben
    }
}
