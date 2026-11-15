package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;

public class ChangePasswordScreen extends ScreenAdapter {
    private Stage stage;
    private Skin skin;

    public ChangePasswordScreen() {}

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


        com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle fieldStyle =
            new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle(skin.get("default", com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle.class));


        fieldStyle.background = skin.getDrawable("buttonbeige");
        TextField oldPass = new TextField("", fieldStyle);
        oldPass.setMessageText("Old password");
        oldPass.setAlignment(Align.center);
        oldPass.setPasswordCharacter('*');
        oldPass.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor, boolean focused) {
                oldPass.setPasswordMode(focused || !oldPass.getText().isEmpty());
            }
        });

        TextField newPass = new TextField("", fieldStyle);
        newPass.setMessageText("New password");
        newPass.setAlignment(Align.center);
        newPass.setPasswordCharacter('*');
        newPass.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor, boolean focused) {
                newPass.setPasswordMode(focused || !newPass.getText().isEmpty());
            }
        });

        TextField confirmPass = new TextField("", fieldStyle);
        confirmPass.setMessageText("Repeat new password");
        confirmPass.setAlignment(Align.center);
        confirmPass.setPasswordCharacter('*');
        confirmPass.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor, boolean focused) {
                confirmPass.setPasswordMode(focused || !confirmPass.getText().isEmpty());
            }
        });

        table.add(oldPass).width(450).padBottom(20).row();
        table.add(newPass).width(450).padBottom(20).row();
        table.add(confirmPass).width(450).padBottom(40).row();


        TextButton submitBtn = new TextButton("Submit", skin, "default");
        TextButton backBtn = new TextButton("Back", skin, "default");

        table.add(submitBtn).width(250).height(50).padBottom(20).row();
        table.add(backBtn).width(250).height(50).row();

        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Backend team will put password update logic here!");
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new AccountSettingsScreen());
            }
        });
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
    }
}
