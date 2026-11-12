package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class UIFactory {

    private final Skin skin;


    public static final float BUTTON_WIDTH = 110f;
    public static final float BUTTON_HEIGHT = 40f;

    public static final float FULL_BUTTON_WIDTH = 330f;
    public static final float FULL_BUTTON_HEIGHT = 56f;

    public static final float TEXTFIELD_WIDTH = 200f;
    public static final float TEXTFIELD_HEIGHT = 40f;

    public static final float SQUARE_SIZE = 36f;

    public UIFactory(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    public TextButton createButton(String text, String style) {
        return new TextButton(text, skin, style);
    }

    public TextButton createFullWidthButton(String text, String style) {
        return new TextButton(text, skin, style);
    }

    public TextButton createSquareButton(String text, String style) {
        return new TextButton(text, skin, style);
    }

    public Label createLabel(String text, String style) {
        return new Label(text, skin, style);
    }

    public TextField createTextField(String placeholder) {
        TextField field = new TextField("", skin, "default");
        field.setMessageText(placeholder);
        return field;
    }
}
