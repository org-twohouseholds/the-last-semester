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

        String[] characters = {"The hacker", "The esoteric", "The bitter one", "The alcoholic"};
        String[] images = {"The hacker.png", "The esoteric.png", "The bitter one.png", "The alcoholic.png"};
        String[] descriptions = {
            "He doesn't care about anything other than his laptop and IT security. He's always hoarding chargers and power outlets. He's not much of a talker, but you know he's your guy when you need something fixed. But don't even think about talking to him if you don't have Arch Linux installed.",
            "She's young and new to teaching and she still hasn't figured out what kind of professor she wants to be. On one hand, she remembers exactly what it was like to be a student, so she wants to be liked and tries to be easygoing. On the other hand, the moment she realizes she's not being taken seriously, she flips a switch and becomes the strictest professor you've ever met. Talking to her is like trying to regulate a broken thermostat.",
            "She hates her job. She only took it because she had to. Her subject is something she couldn't care less about, just like her students or anyone's personal circumstances. Yet somehow, she's always got her nose in everyone's business and never shuts up about it. She loves talking about winning the lottery and quitting her job. Because why be miserable alone when we can make it a group activity?",
            "Nothing in his life has gone according to plan. He's a caring guy who tries his best. But he's an absolute hot mess, just like his lectures. Drinking his beloved liquor, during working hours is the only thing keeping him afloat. He never answers emails of any kind, but you can always find him on campus, dancing and drinking with his students."
        };

        Table gridTable = new Table();
        for (int i = 0; i < characters.length; i++) {
            TextButton charButton = new TextButton(characters[i], skin, "default");

            charButton.getLabel().setAlignment(com.badlogic.gdx.utils.Align.center);

            final String name = characters[i];
            final String desc = descriptions[i];
            final String img = images[i];

            charButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    com.extracreditstudios.thelastsemester.TheLastSemesterGame game =
                        (com.extracreditstudios.thelastsemester.TheLastSemesterGame) Gdx.app.getApplicationListener();

                    game.setScreen(new CharacterInfoScreen(game, name, desc, img));
                }
            });

            gridTable.add(charButton).width(350).height(80).pad(10).row();
        }

        mainTable.add(gridTable).colspan(2).padBottom(40).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                com.extracreditstudios.thelastsemester.TheLastSemesterGame game =
                    (com.extracreditstudios.thelastsemester.TheLastSemesterGame) Gdx.app.getApplicationListener();
                game.setScreen(new CopyHomeScreen(game));
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
