package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class CharacterSelectionScreen extends ScreenAdapter{
    private Stage stage;
    private Skin skin;

    public CharacterSelectionScreen() {
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label titleLabel = new Label("Character", skin, "title");
        mainTable.add(titleLabel).colspan(2).padBottom(30).row();

        String[] characters = {
            "The Hacker", "The Forever Student",
            "The Ghost", "The Flirt",
            "Prof", "Sweet Grandma",
            "The Caffeine Addict", "The Overachiever",
            "The Party Animal", "The Commuter",
            "The Library Camper", "The Procrastinator"
        };

        Table gridTable = new Table();
        for (int i = 0; i < characters.length; i++) {
            //ImageTextButton charButton = new ImageTextButton(characters[i], skin, "default");
            TextButton charButton = new TextButton(characters[i], skin, "default");
            if (characters[i].equals("The Ghost")) {
                charButton.setChecked(true);
            }

            gridTable.add(charButton).width(240).height(80).pad(10);

            if ((i + 1) % 2 == 0) {
                gridTable.row();
            }
        }

        mainTable.add(gridTable).colspan(2).padBottom(40).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Navigating back to Home Screen...");
            }
        });

        mainTable.add(backButton).width(240).height(60).colspan(2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.14f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
