
package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class CharacterSelectionScreen extends BaseScreen {

    private static final String[] CHARACTERS = {
        "The Hacker",
        "The Esoteric",
        "The Bitter One",
        "The Alcoholic"
    };

    private final DesignKit kit = new DesignKit();

    public CharacterSelectionScreen(TheLastSemesterGame game) {
        super(game);
    }

    @Override
    protected void buildUi() {

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        root.add(mainTable).grow();

        Label titleLabel = kit.label("Character", "title");
        mainTable.add(titleLabel)
            .colspan(2)
            .padBottom(30)
            .row();

        // Charakterauswahl
        Table gridTable = new Table();
        for (int i = 0; i < CHARACTERS.length; i++) {

            TextButton charButton = kit.button(
                CHARACTERS[i],
                "default"
            );

            if (CHARACTERS[i].equals("The Ghost")) {
                charButton.setChecked(true);
            }

            gridTable.add(charButton)
                .width(240)
                .height(80)
                .pad(10);

            if ((i + 1) % 2 == 0) {
                gridTable.row();
            }
        }

        mainTable.add(gridTable)
            .colspan(2)
            .padBottom(40)
            .row();


        TextButton backButton = kit.button("Back", "default");

        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event,
                                com.badlogic.gdx.scenes.scene2d.Actor actor) {
                onBack();
            }
        });

        mainTable.add(backButton)
            .width(240)
            .height(60)
            .colspan(2);
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
