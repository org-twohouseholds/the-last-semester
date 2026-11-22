package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class CharacterInfoScreen extends ScreenAdapter {
    private TheLastSemesterGame game;
    private Stage stage;
    private Skin skin;
    private Texture charTexture;

    private String charName;
    private String description;
    private String imagePath;

    public CharacterInfoScreen(TheLastSemesterGame game, String name, String description, String imagePath) {
        this.game = game;
        this.charName = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    @Override
    public void show() {
        stage = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(800, 600));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        skin.addRegions(new com.badlogic.gdx.graphics.g2d.TextureAtlas(Gdx.files.internal("design_sammlung.atlas")));
        skin.add("font_default", new com.badlogic.gdx.graphics.g2d.BitmapFont(Gdx.files.internal("PressStart2P-Regular.fnt")));
        skin.load(Gdx.files.internal("design_sammlung.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.get("default", Label.LabelStyle.class));

        Label nameLabel = new Label(charName, labelStyle);
        nameLabel.setFontScale(1.5f);
        nameLabel.setAlignment(Align.center);

        charTexture = new Texture(Gdx.files.internal(imagePath));
        Image charImage = new Image(charTexture);

        Label descLabel = new Label(description, labelStyle);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.center);
        // FIX 1: Shrink the text so it is readable and doesn't overlap
        descLabel.setFontScale(0.5f);

        // Use the team's custom style for the back button
        TextButton backBtn = new TextButton("Back", skin, "second");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CharacterSelectionScreen());
            }
        });

        table.add(nameLabel).padBottom(10).row();

        // FIX 2: Slightly smaller image and paddings to ensure the Back button stays on screen
        table.add(charImage).width(180).height(280).padBottom(15).row();
        table.add(descLabel).width(700).padBottom(20).row();
        table.add(backBtn).width(250).height(50).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.14f, 0.15f, 0.20f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (charTexture != null) charTexture.dispose();
    }
}
