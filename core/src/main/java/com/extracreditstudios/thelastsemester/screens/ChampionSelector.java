package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

    // Standard-Pfeile (alter Konstruktor): zwei getrennte Bilder
    private static final String ARROW_LEFT_PATH = "ui/arrow_left.png";
    private static final String ARROW_RIGHT_PATH = "ui/arrow_right.png";

    private static final float CHAMPION_WIDTH = 280f;   // Startwerte, mit setChampionSize() änderbar
    private static final float CHAMPION_HEIGHT = 260f;
    private static final float ARROW_SIZE = 44f;
    private static final float ARROW_DISTANCE = 2f; // Abstand zwischen Pfeil und Champion

    private final Skin skin;
    private final String[] names;
    private final Texture[] championTextures; // ein Eintrag pro Champion, null wenn das PNG fehlt
    private final Array<Texture> ownTextures = new Array<>();

    private final Image championImage;   // zeigt das PNG
    private final ChampionView championView; // sorgt dafür, dass das Bild nach Höhe skaliert wird
    private final Table placeholderBox;  // zeigt den Namen, falls kein PNG da ist
    private final Label placeholderLabel;
    private final Cell<Actor> championCell;
    private final Cell<Button> leftCell;
    private final Cell<Button> rightCell;

    /** Wie viele Pixel des Champions hinter den Zetteln verschwinden (größer = weiter unten) */
    private static final float CHAMPION_OVERLAP = 220f;

    private int selectedIndex = 0;
    private SelectionListener listener;

    /** Wie bisher: zwei getrennte Pfeil-Bilder (ui/arrow_left.png, ui/arrow_right.png). */
    public ChampionSelector(Skin skin, String[] names, String[] imagePaths) {
        this(skin, names, imagePaths, null, true);
    }

    /**
     * Mit EINEM Pfeil-Bild: das zweite wird gespiegelt.
     * @param arrowPath       Pfad zum Pfeil-Bild (null = die zwei Standard-Pfeile)
     * @param arrowPointsLeft true, wenn der Pfeil im Bild nach links zeigt (sonst nach rechts)
     */
    public ChampionSelector(Skin skin, String[] names, String[] imagePaths,
                            String arrowPath, boolean arrowPointsLeft) {
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
        championImage.setTouchable(Touchable.disabled);
        championView = new ChampionView(championImage);
        championView.setTouchable(Touchable.disabled); // damit das breite Bild die Pfeile nicht blockiert

        placeholderLabel = new Label("", skin);
        placeholderLabel.setAlignment(Align.center);
        placeholderLabel.setWrap(true);
        placeholderBox = new Table();
        placeholderBox.add(placeholderLabel).width(CHAMPION_WIDTH - 20f);

        // Pfeil links | Champion | Pfeil rechts
        Button left;
        Button right;
        if (arrowPath == null) {
            left = arrowButton(ARROW_LEFT_PATH, false, "<", () -> select(selectedIndex - 1));
            right = arrowButton(ARROW_RIGHT_PATH, false, ">", () -> select(selectedIndex + 1));
        } else {
            left = arrowButton(arrowPath, !arrowPointsLeft, "<", () -> select(selectedIndex - 1));
            right = arrowButton(arrowPath, arrowPointsLeft, ">", () -> select(selectedIndex + 1));
        }

        leftCell = add(left).size(ARROW_SIZE);
        championCell = add((Actor) championView)
            .size(CHAMPION_WIDTH, CHAMPION_HEIGHT)
            .padLeft(ARROW_DISTANCE).padRight(ARROW_DISTANCE);
        rightCell = add(right).size(ARROW_SIZE);

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

    /** Größe des Champions von außen setzen (z. B. passend zur Bildschirmhöhe). */
    public void setChampionSize(float width, float height) {
        championCell.size(width, height);
        invalidateHierarchy();
    }

    /** Pfeile oben ausrichten, mit diesem Abstand von der Oberkante des Champions (statt mittig). */
    public void setArrowTopPadding(float pad) {
        leftCell.top().padTop(pad);
        rightCell.top().padTop(pad);
        invalidateHierarchy();
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
            championView.setAspect(texture.getWidth() / (float) texture.getHeight());
            championCell.setActor(championView);
        } else {
            placeholderLabel.setText(names[selectedIndex]);
            championCell.setActor(placeholderBox);
        }
    }

    /** Pfeil-Button: Bild (optional horizontal gespiegelt), sonst Text als Ersatz */
    private Button arrowButton(String imagePath, boolean flip, String fallbackText, Runnable action) {
        Texture texture = loadTextureIfExists(imagePath);
        Button button;
        if (texture != null) {
            TextureRegion region = new TextureRegion(texture);
            if (flip) {
                region.flip(true, false);
            }
            TextureRegionDrawable up = new TextureRegionDrawable(region);
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

    /**
     * Zeigt das Champion-Bild immer so groß, dass es die volle HÖHE der Zelle füllt, auch wenn das Bild
     * viel transparenten Rand links und rechts hat. Die Breite der Zelle bestimmt nur, wie weit die Pfeile
     * auseinander stehen. Der transparente Rand darf über die Zelle hinausragen.
     */
    private static class ChampionView extends WidgetGroup {
        private final Image image;
        private float aspect = 0.5f;   // Breite / Höhe des Bildes

        ChampionView(Image image) {
            this.image = image;
            addActor(image);
        }

        void setAspect(float aspect) {
            this.aspect = aspect;
            invalidate();
        }

        @Override
        public void layout() {
            float h = getHeight();
            float w = h * aspect;
            image.setBounds((getWidth() - w) / 2f, 0f, w, h);
        }
    }
}
