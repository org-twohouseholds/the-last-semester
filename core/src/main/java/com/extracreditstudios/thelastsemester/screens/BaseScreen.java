package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

/** gemeinsame Grundgerüst für alle Menü-Screens */
public abstract class BaseScreen extends ScreenAdapter {

    /** Bildschirmgröße im HOCHFORMAT. */
    public static final float WORLD_WIDTH = 450f;
    public static final float WORLD_HEIGHT = 800f;

    /** Einheitliche Button-Größe für alle Menüs */
    public static final float BUTTON_WIDTH = 300f;
    public static final float BUTTON_HEIGHT = 56f;

    /** Hintergrundfarbe, falls kein Bild da ist (dunkles Blau) */
    private static final Color CLEAR_COLOR = new Color(0.12f, 0.13f, 0.20f, 1f);

    protected final TheLastSemesterGame game;
    protected final Skin skin;
    protected final Stage stage; // die Bühne
    protected final Table root;  // das unsichtbare Regal, das den ganzen Bildschirm füllt

    private boolean built = false;

    protected BaseScreen(TheLastSemesterGame game) {
        this.game = game;
        this.skin = game.getSkin();
        this.stage = new Stage(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));

        this.root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Android-Zurück-Taste (und ESC am PC) abfangen
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    onBack();
                    return true;
                }
                return false;
            }
        });
    }

    /** Hier baut jeder Screen seine Buttons, Labels usw. auf. Wird nur einmal aufgerufen. */
    protected abstract void buildUi();

    /** Bei Zurück-Taste passiert nichts. Screens können das überschreiben. */
    protected void onBack() {

    }

    /** Button mit Text bauen, der beim Antippen "action" ausführt.
     *     button("Settings", () -> game.goTo(new SettingsScreen(game)))
     */
    protected TextButton button(String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return button;
    }

    /** Screen wird sichtbar. */
    @Override
    public void show() {
        if (!built) {
            buildUi();
            built = true;
        }
        // Ohne diese Zeile reagiert NICHTS auf Klicks. Häufigster Anfängerfehler!
        Gdx.input.setInputProcessor(stage);
    }

    /** Wird ca. 60-mal pro Sekunde aufgerufen: alles löschen, dann neu malen. */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR);
        stage.getViewport().apply();
        stage.act(delta); // Animationen/Zustände weiterlaufen lassen
        stage.draw();     // alles zeichnen
    }

    /** Handy gedreht oder Fenstergröße geändert. */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /** Speicher freigeben (der Skin gehört dem Game, nicht dem Screen!). */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
