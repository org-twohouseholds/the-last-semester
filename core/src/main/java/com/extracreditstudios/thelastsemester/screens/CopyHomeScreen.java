package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class CopyHomeScreen extends BaseScreen {

    //private static final String BACKGROUND_PATH = "backgrounds/campus.png";
    private static final String BACKGROUND_PATH = "backgrounds/campus_2.png";
    private static final String[] CHAMPION_NAMES = {"Champion 1", "Champion 2", "Champion 3"};
    private static final String[] CHAMPION_IMAGES = {
        "champions/champion_1.png",
        "champions/champion_2.png",
        "champions/champion_3.png"
    };

    // Style-Name aus design_sammlung.json (ggf. anpassen)
    private static final String BUTTON_STYLE = "second";

    private final UIFactory ui;
    private final Skin lobbySkin;

    private Texture backgroundTexture; // Bilder belegen Grafikspeicher -> in dispose() freigeben!
    private ChampionSelector championSelector; // lädt eigene Bilder -> ebenfalls in dispose() freigeben

    public CopyHomeScreen(TheLastSemesterGame game) {
        super(game);
        this.lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        this.ui = new UIFactory(lobbySkin);
    }

    @Override
    protected void buildUi() {
        addBackgroundIfAvailable();

        Label title = new Label("The Last Semester", skin);
        title.setFontScale(2f);
        title.setColor(1f, 0.96f, 0.86f, 1f); // Creme-Weiß

        // Titel "Table"
        Table banner = new Table();
        banner.setBackground(skin.newDrawable("white", 0.12f, 0.08f, 0.05f, 0.6f));
        banner.add(title).pad(10, 24, 10, 24); // Abstand innen

        root.add(banner).padTop(80).row();

        // Champion-Auswahl
        championSelector = new ChampionSelector(skin, CHAMPION_NAMES, CHAMPION_IMAGES);
        // TODO : Spiellogik hier um später Champion fürs Spiel übernehmen
        championSelector.setSelectionListener(
            (index, name) -> Gdx.app.log("HomeScreen", "Champion gewählt: " + name));

        // Das Fach schluckt allen übrigen Platz (expandY) und schiebt so die Buttons nach unten.
        // bottom() = Champion steht unten im Fach, also direkt über den Buttons.
        root.add(championSelector).expandY().bottom().padBottom(24).row();

        // TODO: Register Button später woanders platzieren
       // root.add(lobbyButton("Register (Test)", () -> game.goTo(new RegisterScreen(game))))
            // Testklasse von D.
            root.add(lobbyButton("Register (Test)", () -> game.goTo(new CopyRegisterScreen(game))))
            .width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).padBottom(14).row();

        root.add(lobbyButton("Join / Create Game",
                // () -> game.goTo(new PlaceholderScreen(game, "Join / Create Game"))))
                () -> game.goTo(new JoinLobbyScreen(game, "Lobby-123"))))
            .width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).padBottom(14).row();

        root.add(lobbyButton("Character",
                () -> game.goTo(new CharacterSelectionScreen())))
            .width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).padBottom(14).row();

        root.add(lobbyButton("Settings",
                //() -> game.goTo(new SettingsScreen(game))))
                () -> game.goTo(new CopySettingsScreen(game))))
            .width(UIFactory.FULL_BUTTON_WIDTH).height(UIFactory.FULL_BUTTON_HEIGHT).padBottom(70).row();
    }

    /** Button im Lobby-Design, löst beim Klick dieselbe Aktion aus wie vorher. */
    private TextButton lobbyButton(String text, Runnable action) {
        TextButton btn = ui.createFullWidthButton(text, BUTTON_STYLE);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return btn;
    }

    /** Lädt Hintergrundbild */
    private void addBackgroundIfAvailable() {
        FileHandle file = Gdx.files.internal(BACKGROUND_PATH);
        if (!file.exists()) {
            return;
        }
        backgroundTexture = new Texture(file);
        Image background = new Image(backgroundTexture);
        background.setScaling(Scaling.fill); // Bild füllt alles, Seitenverhältnis bleibt
        background.setFillParent(true);
        stage.addActor(background);
        background.toBack();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (championSelector != null) {
            championSelector.dispose();
        }
        lobbySkin.dispose(); // eigener Skin -> freigeben
    }
}
