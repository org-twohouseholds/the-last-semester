
package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

public class DesignKit implements Disposable {

    private static final String SKIN_PATH = "ui/lobby/design_sammlung.json";
    private static final String FONT_ATLAS_PATH = "font_data/font_1.atlas";
    private static final String FONT_FNT_PATH = "font_data/PatrickHand-Regular.fnt";
    private static final String FONT_REGION = "PatrickHand-Regular";

    public static final Color TEXT_DARK = new Color(0.1f, 0.1f, 0.1f, 1f);
    public static final Color TEXT_LIGHT = new Color(0.95f, 0.93f, 0.82f, 1f);
    public static final Color HINT_COLOR = new Color(0.4f, 0.4f, 0.35f, 1f);



    private static final String FOLDER = "finaldesign_data/";

    public static final String HOME_BACKGROUND = FOLDER + "home_background.png";
    public static final String LOBBY_BACKGROUND = FOLDER + "lobby_backgrounds.png";
    public static final String TITLE = FOLDER + "The Last Semester.png";
    public static final String GEAR = FOLDER + "settings.png";
    public static final String ARROW = FOLDER + "Polygon 2.png";

    public static final String BUTTON_GREEN = FOLDER + "button_green2.png";
    public static final String BUTTON_YELLOW = FOLDER + "button_new_yellow.png";

    public static final String[] CHAMPION_IMAGES = {
        FOLDER + "The Esoteric.png",
        FOLDER + "The Bitter One.png",
        FOLDER + "The Alcoholic.png",
        FOLDER + "The Hacker.png"
    };

    public static final String[] PROFILE_PICS = {
        FOLDER + "pb_alcoholic.png",
        FOLDER + "pb_anonym.png",
        FOLDER + "pb_bitter.png",
        FOLDER + "pb_esoteric.png",
        FOLDER + "pb_placeholder.png"
    };

    private final Skin skin;
    private final TextureAtlas fontAtlas;
    private final BitmapFont font;
    private final Label.LabelStyle labelStyle;

    private final Array<Texture> textures = new Array<>();

    public DesignKit() {
        skin = new Skin(Gdx.files.internal(SKIN_PATH));

        fontAtlas = new TextureAtlas(Gdx.files.internal(FONT_ATLAS_PATH));
        TextureRegion region = fontAtlas.findRegion(FONT_REGION);

        if (region == null) {
            throw new IllegalStateException(
                FONT_REGION + " wurde im Font-Atlas nicht gefunden."
            );
        }

        font = new BitmapFont(
            Gdx.files.internal(FONT_FNT_PATH),
            region,
            false
        );

        font.setUseIntegerPositions(false);

        // Standard-Label-Style des Skins kopieren, Schrift ersetzen
        labelStyle = new Label.LabelStyle(
            skin.get("default", Label.LabelStyle.class)
        );

        labelStyle.font = font;
    }


    // Zugriff auf geladene Ressourcen

    public Skin skin() {
        return skin;
    }

    public BitmapFont font() {
        return font;
    }


    // Textures

    public Texture texture(String path) {
        Texture t = new Texture(Gdx.files.internal(path));

        t.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        textures.add(t);
        return t;
    }


    public Texture textureOrNull(String path) {
        return Gdx.files.internal(path).exists() ? texture(path) : null;
    }

    /** Chat: Höhe/Breite-Verhältnis einer Textur. */
    public static float aspect(Texture t) {
        return t.getHeight() / (float) t.getWidth();
    }




    public TextButton button(String text, String styleName) {
        TextButton.TextButtonStyle style =
            new TextButton.TextButtonStyle(
                skin.get(styleName, TextButton.TextButtonStyle.class)
            );

        style.font = font;

        return new TextButton(text, style);
    }

    /** Label in Patrick Hand, zentriert. */
    public Label label(String text, Color color, float scale) {
        Label.LabelStyle style = new Label.LabelStyle(labelStyle);
        style.fontColor = color;

        Label l = new Label(text, style);
        l.setFontScale(scale);
        l.setAlignment(Align.center);

        return l;
    }



    public Label label(String text, String skinStyleName) {
        Label.LabelStyle style = new Label.LabelStyle(
            skin.get(skinStyleName, Label.LabelStyle.class)
        );

        style.font = font;

        Label l = new Label(text, style);
        l.setAlignment(Align.center);

        return l;
    }


    public Table imageButton(
        String text,
        Texture texture,
        Color textColor,
        float fontScale,
        Runnable action
    ) {
        Table button = new Table();

        button.setBackground(
            new TextureRegionDrawable(new TextureRegion(texture))
        );

        button.add(label(text, textColor, fontScale)).expand().center();

        onClick(button, action);

        return button;
    }


    public Image imageOnly(Texture texture, Runnable action) {
        Image image = new Image(texture);
        image.setScaling(Scaling.fit);

        onClick(image, action);

        return image;
    }

    public Label link(String text, float fontScale, Runnable action) {
        Label l = label(text, TEXT_LIGHT, fontScale);

        onClick(l, action);

        return l;
    }

    public TextField textField(
        Texture background,
        String hint,
        boolean password,
        int maxLength,
        TextField.TextFieldStyle base
    ) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(base);

        TextureRegionDrawable drawable =
            new TextureRegionDrawable(new TextureRegion(background));

        style.background = drawable;
        style.focusedBackground = drawable;
        style.fontColor = TEXT_DARK;
        style.messageFontColor = HINT_COLOR;

        TextField field = new TextField("", style);
        field.setMessageText(hint);
        field.setMaxLength(maxLength);

        if (password) {
            field.setPasswordMode(true);
            field.setPasswordCharacter('*');
        }

        return field;
    }

    public TextField textField(
        Texture background,
        String hint,
        boolean password,
        int maxLength
    ) {
        TextField.TextFieldStyle base = new TextField.TextFieldStyle(
            skin.get("default", TextField.TextFieldStyle.class)
        );

        base.font = font;
        base.messageFont = font;

        return textField(background, hint, password, maxLength, base);
    }

    public static void onClick(Actor actor, Runnable action) {
        actor.setTouchable(Touchable.enabled);

        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
    }



    @Override
    public void dispose() {
        for (Texture t : textures) {
            t.dispose();
        }

        textures.clear();

        font.dispose();
        fontAtlas.dispose();
        skin.dispose();
    }
}
