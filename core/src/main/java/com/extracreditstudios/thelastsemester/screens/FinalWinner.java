package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;


public class FinalWinner extends BaseScreen {

    private final boolean isWinner;
    private final String playerName;
    private final int rewardPoints;

    private final Skin lobbySkin;
    private final UIFactory ui;

    public FinalWinner(TheLastSemesterGame game, boolean isWinner, String playerName, int rewardPoints) {
        super(game);
        this.isWinner = isWinner;
        this.playerName = playerName;
        this.rewardPoints = rewardPoints;

        lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        ui = new UIFactory(lobbySkin);
    }


    @Override
    protected void buildUi() {
        // Außenabstand für den gesamten Screen
        root.pad(40, 20, 30, 20);


        String titleText = isWinner ? "Gratulation, " + playerName : "Niederlage";
        Label titleLabel = ui.createLabel(titleText, "default");
        titleLabel.setFontScale(1.3f);
        titleLabel.setAlignment(Align.center);

        // Restlicher Inhalt (Charakter, Beschreibung, Punkte ...)
        Table centerGroup = new Table();

        ImageButton characterPic = new ImageButton(lobbySkin, "characters");

        String descText;
        if (isWinner) {
            descText = "Du hast als\nJahrgangs-bester\nabgeschlossen.";
        } else {
            descText = "Du bist im Turnier\nausgeschieden.";
        }

        Label descLabel = ui.createLabel(descText, "default");
        descLabel.setFontScale(0.85f);
        descLabel.setAlignment(Align.center);
        descLabel.setWrap(true);

        // Cps
        String rewardText;
        if (isWinner) {
            rewardText = "+" + rewardPoints + " PTS";
        } else {
            rewardText = "+0 PTS";
        }

        Label rewardLabel = ui.createLabel(rewardText, "default");
        rewardLabel.setFontScale(1.1f);
        rewardLabel.setAlignment(Align.center);

        centerGroup.add(characterPic).size(140, 180).padBottom(20).row();
        centerGroup.add(descLabel).width(320f).padBottom(20).row();
        centerGroup.add(rewardLabel);
        // ------------------------------------------------------------------------

        // Main Menu Button (bleibt unten)
        TextButton btnMenu = ui.createFullWidthButton("Main Menu", "second");
        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });

        // Haupt-Layout: Oben Titel, Mitte die Gruppe, Unten der Button
        root.add(titleLabel).top().padBottom(20).row();
        root.add(centerGroup).expandY().center().row();
        root.add(btnMenu)
            .width(UIFactory.FULL_BUTTON_WIDTH)
            .height(UIFactory.FULL_BUTTON_HEIGHT)
            .bottom();
    }

    @Override
    protected void onBack() {
        // Zurück zum Hauptmenü
        game.goTo(new HomeScreen(game));
    }

    @Override
    public void dispose() {
        super.dispose();
        lobbySkin.dispose();
    }
}
