
package com.extracreditstudios.thelastsemester.screens.lobby;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.screens.BaseScreen;
import com.extracreditstudios.thelastsemester.screens.DesignKit;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;

public class FinalWinner extends BaseScreen {

    private final boolean isWinner;
    private final String playerName;
    private final int rewardPoints;

    private final DesignKit kit = new DesignKit();

    public FinalWinner(
        TheLastSemesterGame game,
        boolean isWinner,
        String playerName,
        int rewardPoints
    ) {
        super(game);

        this.isWinner = isWinner;
        this.playerName = playerName;
        this.rewardPoints = rewardPoints;
    }

    @Override
    protected void buildUi() {

        // Außenabstand für den gesamten Screen
        root.pad(40, 20, 30, 20);

        String titleText;

        if (isWinner) {
            titleText = "Gratulation, " + playerName;
        } else {
            titleText = "Niederlage";
        }

        Label titleLabel = kit.label(titleText, "default");
        titleLabel.setFontScale(1.3f);
        titleLabel.setAlignment(Align.center);

        // Restlicher Inhalt
        Table centerGroup = new Table();

        ImageButton characterPic = new ImageButton(
            kit.skin(),
            "characters"
        );

        String descText;

        if (isWinner) {
            descText = "Du hast als\nJahrgangs-bester\nabgeschlossen.";
        } else {
            descText = "Du bist im Turnier\nausgeschieden.";
        }

        Label descLabel = kit.label(descText, "default");
        descLabel.setFontScale(0.85f);
        descLabel.setAlignment(Align.center);
        descLabel.setWrap(true);


        String rewardText;

        if (isWinner) {
            rewardText = "+" + rewardPoints + " CP";
        } else {
            rewardText = "+0 CP";
        }

        Label rewardLabel = kit.label(rewardText, "default");
        rewardLabel.setFontScale(1.1f);
        rewardLabel.setAlignment(Align.center);

        centerGroup.add(characterPic)
            .size(140, 180)
            .padBottom(20)
            .row();

        centerGroup.add(descLabel)
            .width(320f)
            .padBottom(20)
            .row();

        centerGroup.add(rewardLabel);


        TextButton btnMenu = kit.button("Main Menu", "second");

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });


        root.add(titleLabel)
            .top()
            .padBottom(20)
            .row();

        root.add(centerGroup)
            .expandY()
            .center()
            .row();

        root.add(btnMenu)
            .width(BUTTON_WIDTH)
            .height(BUTTON_HEIGHT)
            .bottom();
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
}
