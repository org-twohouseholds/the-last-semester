package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class TournamentScreen extends BaseScreen {

    private static final float PIC_SIZE = 40f;
    private static final float LINE_W = 2f;
    private static final float MIN_LINE = 15f;  // Mindesthöhe der senkrechten Linien
    private static final float GAP = 4f;        // Abstand zwischen Karte und Linie

    private final String[] players;
    private final Skin lobbySkin;
    private final UIFactory ui;
    private final Texture lineTexture;

    // PLatzhalter zum Testen
    public TournamentScreen(TheLastSemesterGame game) {
        this(game, new String[]{"Tom", "Tim", "Anna", "Ben",
            "Dennis", "David", "Johann", "Felix"});
    }

    public TournamentScreen(TheLastSemesterGame game, String[] players) {
        super(game);
        this.players = players;

        lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        ui = new UIFactory(lobbySkin);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        lineTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    protected void buildUi() {
        root.pad(25, 10, 20, 10);

        String titleText;
        if (players.length >= 8) {
            titleText = "TOURNAMENT";
        } else if (players.length >= 4) {
            titleText = "SEMIFINAL";
        } else {
            titleText = "FINAL";
        }

        Label title = ui.createLabel(titleText, "default");
        title.setFontScale(1.4f);

        Table bracket = new Table();

        if (players.length >= 8) {
            // Runde 1: 8 Spieler
            Table topHalf = createBracketNode(
                createBracketNode(createPlayerCard(players[0]), createPlayerCard(players[1]),
                    "Winner 1", true, true),
                createBracketNode(createPlayerCard(players[2]), createPlayerCard(players[3]),
                    "Winner 2", true, true),
                "Winner 1", true, false);
            Table bottomHalf = createBracketNode(
                createBracketNode(createPlayerCard(players[4]), createPlayerCard(players[5]),
                    "Winner 3", false, true),
                createBracketNode(createPlayerCard(players[6]), createPlayerCard(players[7]),
                    "Winner 4", false, true),
                "Winner SF", false, false);

            bracket.add(topHalf).grow().row();
           // bracket.add(ui.createLabel("FINAL", "default")).padTop(14).padBottom(14).row();
            bracket.add(bottomHalf).grow().padTop(15).row();

        } else if (players.length >= 4) {
            // Runde 2:
            Table topHalf = createBracketNode(createPlayerCard(players[0]),
                createPlayerCard(players[1]), "Winner x", true, true);
            Table bottomHalf = createBracketNode(createPlayerCard(players[2]),
                createPlayerCard(players[3]), "Winner y", false, true);

            bracket.add(topHalf).grow().row();
            //bracket.add(ui.createLabel("FINAL", "default")).padTop(14).padBottom(14).row();
            bracket.add(bottomHalf).grow().row();

        } else {
            // Finale:
            bracket.add(createPlayerCard(players[0])).expandY().bottom().row();
            bracket.add(ui.createLabel("VS", "default")).pad(20).row();
            bracket.add(createPlayerCard(players[1])).expandY().top().row();
        }

        //root.add(title).padBottom(10).row();
       // root.add(bracket).grow().padTop(25).padBottom(25).row();
        TextButton btnBack = ui.createFullWidthButton("Menu", "second");
        btnBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });

        root.add(btnBack).width(100).height(34).right().padBottom(30).row();
        root.add(title).padBottom(15).row();
        root.add(bracket).grow().padTop(15).padBottom(20).row();

        if (players.length >=2) {
            TextButton btnNext = ui.createFullWidthButton("->", "default");
            btnNext.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    nextRound();
                }
            });
            root.add(btnNext)
                .width(30)
                .height(22)
                .padBottom(12).row();
        }
    }

    private Image line() {
        Image line = new Image(lineTexture);
        line.setColor(0.91f, 0.89f, 0.77f, 1f); // text_cream
        return line;
    }



//
    private Table createBracketNode(Table left, Table right, String winnerName,
                                    boolean down, boolean leaves) {

        Table children = new Table();
        if (leaves) {
            children.add(left).growX().uniformX(); // dadurch gleichbreite Breite der Tabellenspalten
            children.add(right).growX().uniformX();
        } else {
            children.add(left).grow().uniformX();
            children.add(right).grow().uniformX();
        }

        // Padding
        float gapTop;
        float gapBottom;

        if (down) {
            gapTop = 0f;
            gapBottom = GAP;
        } else {
            gapTop = GAP;
            gapBottom = 0f;
        }

        // senkrechte Linien unter/über den beiden "child"-nodes
        Table verticals = new Table();
        verticals.add(line()).width(LINE_W).growY().expandX().uniformX()
            .minHeight(MIN_LINE).padTop(gapTop).padBottom(gapBottom);
        verticals.add(line()).width(LINE_W).growY().expandX().uniformX()
            .minHeight(MIN_LINE).padTop(gapTop).padBottom(gapBottom);

        // waagerechte Verbindung
        Table leftHalf = new Table();
        leftHalf.add().growX();
        leftHalf.add(line()).growX().height(LINE_W);

        Table rightHalf = new Table();
        rightHalf.add(line()).growX().height(LINE_W);
        rightHalf.add().growX();

        Table bar = new Table();
        bar.add(leftHalf).growX().uniformX();
        bar.add(rightHalf).growX().uniformX();

        //Linie Gewinner
        Table toWinner = new Table();
        toWinner.add(line()).width(LINE_W).growY().minHeight(MIN_LINE);

        Table winner = createPlayerCard(winnerName);

        Table node = new Table();
        if (down) {
            node.add(children).growX().row();
            node.add(verticals).grow().row();
            node.add(bar).growX().row();
            node.add(toWinner).grow().row();
            node.add(winner).padTop(GAP).row();
        } else {
            node.add(winner).padBottom(GAP).row();
            node.add(toWinner).grow().row();
            node.add(bar).growX().row();
            node.add(verticals).grow().row();
            node.add(children).growX().row();
        }
        return node;
    }

    private Table createPlayerCard(String playerName) {
        Table card = new Table();

        ImageButton pic = new ImageButton(lobbySkin, "profilepic");

        Label name = ui.createLabel(playerName, "default");
        name.setFontScale(0.8f);
        name.setAlignment(Align.center);
        name.setWrap(true);

        card.add(pic).size(PIC_SIZE).row();
        card.add(name).width(90).padTop(10);

        return card;
    }


    private void nextRound() {
        if (players.length <= 2) {
            // Das Turnier ist vorbei!
            boolean isUserWinner = true; // True für Sieg, False für Niederlage
            String winnerName = players[0]; // hier testweise immer erste pos.
            int earnedPoints = 30;

            game.goTo(new FinalWinner(game, isUserWinner, winnerName, earnedPoints));
        } else {
            // Normale nächste Runde berechnen...
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
        lobbySkin.dispose();
        lineTexture.dispose();
    }
}
