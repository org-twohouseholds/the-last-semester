package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class SettingsScreen extends BaseScreen {

    private static final String TAG = "Settings";

    // TODO: echte Adresse als URL einfügen
    private static final String PRIVACY_POLICY_URL = "https://example.org/datenschutz";

    // Namen der Einträge im Gerätespeicher
    private static final String KEY_MUSIC = "musicVolume";
    private static final String KEY_SOUND = "soundVolume";
    private static final String KEY_NAME = "playerName";

    private final Preferences prefs;
    private Label nameLabel;

    public SettingsScreen(TheLastSemesterGame game) {
        super(game);
        this.prefs = game.getPrefs();
    }

    @Override
    protected void buildUi() {
        Label title = new Label("Settings", skin);
        title.setFontScale(1.6f);
        title.setAlignment(Align.center);

        nameLabel = new Label("Name: " + currentName(), skin);
        nameLabel.setAlignment(Align.center);

        // Ein kleines Regal im großen Regal: 3 Spalten (Text | Schieberegler | Prozent)
        Table volumes = new Table();
        volumes.defaults().pad(8);
        addVolumeRow(volumes, "Music", KEY_MUSIC, 80);
        addVolumeRow(volumes, "Sounds", KEY_SOUND, 70);

        root.top().padTop(60);
        root.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(12);

        // Titel, Name und Lautstärke sollen NICHT Button-Größe haben -> eigene Angaben
        root.add(title).width(BUTTON_WIDTH).height(60).padBottom(24).row();
        root.add(nameLabel).height(30).row();
        root.add(volumes).width(BUTTON_WIDTH + 80).height(110).padBottom(24).row();

        root.add(button("Change Name", this::showChangeNameDialog)).row();
        root.add(button("Privacy Policy", () -> Gdx.net.openURI(PRIVACY_POLICY_URL))).row();
        root.add(button("Log Out", this::logOut)).row();
        root.add(button("Delete Account", this::showDeleteAccountDialog)).padBottom(40).row();
        root.add(button("Back", this::onBack)).row();
    }

    private void addVolumeRow(Table table, String text, String prefKey, int defaultValue) {
        Label rowLabel = new Label(text, skin);
        Label valueLabel = new Label("", skin);
        Slider slider = new Slider(0, 100, 1, false, skin);

        // Erst den gespeicherten Wert setzen, DANN den Listener anhängen
        slider.setValue(prefs.getInteger(prefKey, defaultValue));
        valueLabel.setText(Math.round(slider.getValue()) + " %");

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = Math.round(slider.getValue());
                valueLabel.setText(value + " %");
                prefs.putInteger(prefKey, value);
            }
        });

        table.add(rowLabel).left().width(80);
        table.add(slider).width(200);
        table.add(valueLabel).right().width(60);
        table.row();
    }

    /** Dialog mit Textfeld zum Ändern des Namens (funktioniert auf PC und Handy). **/
    private void showChangeNameDialog() {
        TextField nameField = new TextField(currentName(), skin);
        nameField.setMaxLength(20);

        Dialog dialog = new Dialog("Change Name", skin, "dialog") {
            @Override
            protected void result(Object object) {
                if (!Boolean.TRUE.equals(object)) {
                    return; // "Cancel" gedrückt
                }
                String newName = nameField.getText().trim();
                if (newName.isEmpty()) {
                    cancel(); // Dialog offen lassen, leerer Name ist nicht erlaubt
                    return;
                }
                prefs.putString(KEY_NAME, newName);
                prefs.flush();
                nameLabel.setText("Name: " + newName);
            }
        };
        dialog.getContentTable().pad(16).add(nameField).width(260);
        dialog.button("Save", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
        stage.setKeyboardFocus(nameField);
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

    private String currentName() {
        return prefs.getString(KEY_NAME, "Player");
    }

    /** Zurück-Taste oder "Back"-Button: zum Homescreen. */
    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }

    /** Screen wird verlassen*/
    @Override
    public void hide() {
        prefs.flush();
    }

    /** App geht in den Hintergrund */
    @Override
    public void pause() {
        prefs.flush();
    }
}
