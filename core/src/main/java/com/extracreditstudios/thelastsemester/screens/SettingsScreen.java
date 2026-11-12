package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class SettingsScreen extends BaseScreen {

    private static final String TAG = "Settings";

    // TODO: echte Adresse als URL einfügen
    private static final String PRIVACY_POLICY_URL = "https://example.org/datenschutz";

    public SettingsScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {
        Label title = new Label("Settings", skin);
        title.setFontScale(1.6f);
        title.setAlignment(Align.center);

        root.top().padTop(60);
        root.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(12);

        // Titel soll NICHT Button-Größe haben -> eigene Angaben
        root.add(title).width(BUTTON_WIDTH).height(60).padBottom(24).row();

        root.add(button("Privacy Policy", () -> Gdx.net.openURI(PRIVACY_POLICY_URL))).row();
        root.add(button("Log Out", this::logOut)).row();
        root.add(button("Delete Account", this::showDeleteAccountDialog)).padBottom(40).row();
        root.add(button("Back", this::onBack)).row();
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
        game.goTo(new HomeScreen(game));
    }
}
