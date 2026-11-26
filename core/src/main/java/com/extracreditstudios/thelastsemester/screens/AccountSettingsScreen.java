package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AccountSettingsScreen extends ScreenAdapter {
    private Stage stage;
    private Skin skin;

    public AccountSettingsScreen() {
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        skin.addRegions(new com.badlogic.gdx.graphics.g2d.TextureAtlas(Gdx.files.internal("design_sammlung.atlas")));
        skin.add("font_default", new com.badlogic.gdx.graphics.g2d.BitmapFont(Gdx.files.internal("PressStart2P-Regular.fnt")));
        skin.load(Gdx.files.internal("design_sammlung.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton changePassBtn = new TextButton("Change Password", skin, "default");
        TextButton deleteAccountBtn = new TextButton("Delete Account", skin, "default");
        TextButton backBtn = new TextButton("Back", skin, "default");

        table.add(changePassBtn).width(350).height(50).padBottom(20).row();
        table.add(deleteAccountBtn).width(350).height(50).padBottom(40).row();
        table.add(backBtn).width(150).height(50).row();

        changePassBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new ChangePasswordScreen());
            }
        });

        deleteAccountBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Delete Account triggered! Backend team will add API call here.");
            }
        });
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
