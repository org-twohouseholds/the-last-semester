package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

public class ChampionSelector extends Table implements Disposable {

    /** Welcher Chmapion ist gerade ausgewählt */
    public interface SelectionListener {
        void championSelected(int index, String championName);
    }

    private static final String ARROW_LEFT_PATH = "ui/arrow_left.png";
    private static final String ARROW_RIGHT_PATH = "ui/arrow_right.png";

    private static final float CHAMPION_WIDTH = 180f;
    private static final float CHAMPION_HEIGHT = 240f;
    private static final float ARROW_SIZE = 64f;
    private static final float ARROW_DISTANCE = 16f; // Abstand zwischen Pfeil und Champion

    private final Skin skin;
    private final String[] names;
    private final Texture[] championTextures; // ein Eintrag pro Champion, null wenn das PNG fehlt
    private final Array<Texture> ownTextures = new Array<>();

    private final Image championImage;   // zeigt das PNG
    private final Table placeholderBox;  // zeigt den Namen, falls kein PNG da ist
    private final Label placeholderLabel;
    private final Cell<Actor> championCell;

    private int selectedIndex = 0;
    private SelectionListener listener;

    public ChampionSelector(Skin skin, String[] names, String[] imagePaths) {
        this.skin = skin;
        this.names = names;

        // Alle Champion-Bilder einmal laden (wenn vorhanden)
        championTextures = new Texture[names.length];
        for (int i = 0; i < names.length; i++) {
            championTextures[i] = loadTextureIfExists(imagePaths[i]);
        }

        // Die Mitte vorbereiten: entweder Bild oder Platzhalter-Kasten mit Namen
        championImage = new Image();
        championImage.setScaling(Scaling.fit); // Bild passt ins Fach, ohne verzerrt zu werden

        placeholderLabel = new Label("", skin);
        placeholderLabel.setAlignment(Align.center);
        placeholderLabel.setWrap(true);
        placeholderBox = new Table();
        placeholderBox.setBackground(skin.newDrawable("white", 0f, 0f, 0f, 0.35f));
        placeholderBox.add(placeholderLabel).width(CHAMPION_WIDTH - 20f);

        // Pfeil links | Champion | Pfeil rechts
        Button left = arrowButton(ARROW_LEFT_PATH, "<", () -> select(selectedIndex - 1));
        Button right = arrowButton(ARROW_RIGHT_PATH, ">", () -> select(selectedIndex + 1));

        add(left).size(ARROW_SIZE);
        championCell = add((Actor) championImage)
            .size(CHAMPION_WIDTH, CHAMPION_HEIGHT)
            .padLeft(ARROW_DISTANCE).padRight(ARROW_DISTANCE);
        add(right).size(ARROW_SIZE);

        showChampion();
    }

    /** Später für Spiellogik */
    public void setSelectionListener(SelectionListener listener) {
        this.listener = listener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectedName() {
        return names[selectedIndex];
    }


    public void select(int index) {
        int count = names.length;
        selectedIndex = ((index % count) + count) % count; // z. B. -1 wird zum letzten Champion
        showChampion();
        if (listener != null) {
            listener.championSelected(selectedIndex, names[selectedIndex]);
        }
    }

    /** Zeigt aktuell gewählten Champion in der Mitte an. */
    private void showChampion() {
        Texture texture = championTextures[selectedIndex];
        if (texture != null) {
            championImage.setDrawable(new TextureRegionDrawable(texture));
            championCell.setActor(championImage);
        } else {
            placeholderLabel.setText(names[selectedIndex]);
            championCell.setActor(placeholderBox);
        }
    }

    /** Pfeil-Button: Bild, wenn vorhanden */
    private Button arrowButton(String imagePath, String fallbackText, Runnable action) {
        Texture texture = loadTextureIfExists(imagePath);
        Button button;
        if (texture != null) {
            TextureRegionDrawable up = new TextureRegionDrawable(texture);
            // Gedrückt = etwas dunkler, damit man sieht, dass der Klick angekommen ist
            button = new ImageButton(up, up.tint(new Color(0.7f, 0.7f, 0.7f, 1f)));
        } else {
            button = new TextButton(fallbackText, skin);
        }
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return button;
    }

    /** Lädt ein Bild aus dem assets-Ordner */
    private Texture loadTextureIfExists(String path) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            return null;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ownTextures.add(texture);
        return texture;
    }

    /** Alle geladenen Bilder freigeben */
    @Override
    public void dispose() {
        for (Texture texture : ownTextures) {
            texture.dispose();
        }
        ownTextures.clear();
    }
}
