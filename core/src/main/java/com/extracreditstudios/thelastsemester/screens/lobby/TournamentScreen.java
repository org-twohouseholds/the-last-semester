
package com.extracreditstudios.thelastsemester.screens.lobby;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.DesignKit;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;

public class TournamentScreen extends BaseScreen {


    private static final String[] BACKGROUNDS = {
        "finaldesign_data/round1.png",
        "finaldesign_data/round2.png",
        "finaldesign_data/round3.png"
    };

    private static final String NOTE_FILE = "finaldesign_data/note.png";

    //private static final float NOTE_SIZE = 80f;


    /**
     * Positionen der Zettel: {x, y} = Mittelpunkt als Anteil (0..1)
     * der Bildbreite/-höhe. y wird von OBEN gemessen.
     */
    private static final float[][] SLOTS_8 = {
        {0.15f, 0.25f}, {0.37f, 0.25f}, {0.63f, 0.25f}, {0.85f, 0.25f},
        {0.15f, 0.76f}, {0.37f, 0.76f}, {0.63f, 0.76f}, {0.85f, 0.76f}
    };

    private static final float[][] SLOTS_4 = {
        {0.25f, 0.25f}, {0.75f, 0.25f},
        {0.25f, 0.75f}, {0.75f, 0.75f}
    };

    private static final float[][] SLOTS_2 = {
        {0.50f, 0.30f}, {0.50f, 0.70f}
    };

    private final String[] players;
    private final DesignKit kit;

    private Texture bgTexture;
    private Texture noteTexture;

    // Platzhalter zum Testen
    public TournamentScreen(TheLastSemesterGame game) {
        this(game, new String[]{
            "Tom", "Tim", "Anna", "Ben",
            "Dennis", "David", "Johann", "Felix"
        });
    }

    public TournamentScreen(TheLastSemesterGame game, String[] players) {
        super(game);

        this.players = players;
        this.kit = new DesignKit();
    }

    @Override
    protected void buildUi() {
        root.pad(25, 10, 20, 10);


        int round;

        if (players.length >= 8) {
            round = 0;
        } else if (players.length >= 4) {
            round = 1;
        } else {
            round = 2;
        }

        float[][] slots;

        if (round == 0) {
            slots = SLOTS_8;
        } else if (round == 1) {
            slots = SLOTS_4;
        } else {
            slots = SLOTS_2;
        }


        bgTexture = kit.texture(BACKGROUNDS[round]);
        noteTexture = kit.texture(NOTE_FILE);

        // Hintergrund + Zettel
        Board board = new Board(bgTexture);

        for (int i = 0; i < players.length && i < slots.length; i++) {
            board.place(createPlayerCard(), slots[i][0], slots[i][1]);
        }

        stage.addActor(board);
        board.toBack();

        // Menü-Button oben rechts
        TextButton btnBack = kit.button("Menu", "second");

        btnBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });

        root.add(btnBack).width(100).height(34).right().row();

        root.add().grow().row();


        if (players.length >= 2) {
            TextButton btnNext = kit.button("->", "default");

            btnNext.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    nextRound();
                }
            });

            root.add(btnNext).width(30).height(22).padBottom(12).row();
        }
    }


    private Actor createPlayerCard() {
        Table card = new Table();
        card.setBackground(new TextureRegionDrawable(new TextureRegion(noteTexture)));
        return card;
    }

    private void nextRound() {
        if (players.length <= 2) {

            boolean isUserWinner = true;
            String winnerName = players[0];
            int earnedPoints = 30;

            game.goTo(new FinalWinner(
                game, isUserWinner, winnerName, earnedPoints
            ));

        } else {

            String[] winners = new String[players.length / 2];

            for (int i = 0; i < winners.length; i++) {
                winners[i] = players[i * 2];
            }

            game.goTo(new TournamentScreen(game, winners));
        }
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


    private static class Board extends WidgetGroup {

        private final Image bg;
        private final Array<Actor> cards = new Array<>();
        private final Array<float[]> pos = new Array<>();

        Board(Texture texture) {
            bg = new Image(texture);
            bg.setScaling(Scaling.stretch);

            addActor(bg);
            setFillParent(true);
        }


        void place(Actor card, float fx, float fyFromTop) {
            addActor(card);
            cards.add(card);
            pos.add(new float[]{fx, fyFromTop});
        }

        @Override
        public void layout() {
            bg.setBounds(0, 0, getWidth(), getHeight());

            float noteSize = Math.min(getWidth() * 0.20f, getHeight() * 0.10f);

            for (int i = 0; i < cards.size; i++) {
                Actor card = cards.get(i);
                float[] p = pos.get(i);

                card.setSize(noteSize, noteSize);
                card.setPosition(
                    p[0] * getWidth() - noteSize / 2f,
                    getHeight() - p[1] * getHeight() - noteSize / 2f
                );
            }
        }
    }
}
