package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class CharacterInfoScreen extends BaseScreen {

    private static final String BACKGROUND_FILE =
        "finaldesign_data/setting_background.png";

    private static final String BACK_BUTTON_FILE =
        "finaldesign_data/button_setting.png";

    private static final float CENTER_X = 0.51f;
    private static final float CONTENT_WIDTH_FRAC = 0.78f;

    private static final float NAME_Y = 0.43f;
    private static final float NAME_HEIGHT_FRAC = 0.06f;

    private static final float IMAGE_Y = 0.56f;
    private static final float IMAGE_HEIGHT_FRAC = 0.19f;

    private static final float DESC_Y = 0.74f;
    private static final float DESC_HEIGHT_FRAC = 0.15f;

    private static final float BACK_Y = 0.87f;
    private static final float BACK_WIDTH_FRAC = 0.76f * 0.55f;

    private final DesignKit kit = new DesignKit();

    private final String charName;
    private final String description;
    private final String imagePath;

    private Texture bgTexture;
    private Texture backButtonTexture;
    private Texture charTexture;
    private Texture panelTexture;

    public CharacterInfoScreen(TheLastSemesterGame game, String name, String description, String imagePath) {
        super(game);
        this.charName = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    @Override
    protected void buildUi() {
        bgTexture = kit.texture(BACKGROUND_FILE);
        backButtonTexture = kit.texture(BACK_BUTTON_FILE);
        panelTexture = solidTexture(new Color(1f, 0.99f, 0.93f, 0.92f));

        float backButtonAspect =
            backButtonTexture.getHeight() / (float) backButtonTexture.getWidth();

        Board board = new Board(bgTexture);

        Label.LabelStyle nameStyle = new Label.LabelStyle(
            kit.skin().get("default", Label.LabelStyle.class)
        );
        nameStyle.font = kit.font();
        nameStyle.fontColor = Color.WHITE;

        Label nameLabel = new Label(charName, nameStyle);
        nameLabel.setFontScale(1.4f);
        nameLabel.setAlignment(Align.center);
        board.placeBox(nameLabel, CENTER_X, NAME_Y, CONTENT_WIDTH_FRAC, NAME_HEIGHT_FRAC);

        if (imagePath != null && !imagePath.isEmpty() && Gdx.files.internal(imagePath).exists()) {
            charTexture = new Texture(Gdx.files.internal(imagePath));
            Image charImage = new Image(charTexture);
            charImage.setScaling(Scaling.fit);
            board.placeBox(charImage, CENTER_X, IMAGE_Y, CONTENT_WIDTH_FRAC, IMAGE_HEIGHT_FRAC);
        }

        Label.LabelStyle descStyle = new Label.LabelStyle(
            kit.skin().get("default", Label.LabelStyle.class)
        );
        descStyle.font = kit.font();
        descStyle.fontColor = DesignKit.TEXT_DARK;

        Label descLabel = new Label(description, descStyle);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.center);
        descLabel.setFontScale(1.0f);

        Table descCard = new Table();
        descCard.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        descCard.pad(12f);
        descCard.add(descLabel).grow();
        board.placeBox(descCard, CENTER_X, DESC_Y, CONTENT_WIDTH_FRAC, DESC_HEIGHT_FRAC);

        board.placeAspect(
            backButton(),
            CENTER_X,
            BACK_Y,
            BACK_WIDTH_FRAC,
            backButtonAspect
        );

        stage.addActor(board);
        board.toBack();
    }

    private Table backButton() {
        return kit.imageButton(
            "Back",
            backButtonTexture,
            Color.BLACK,
            1.2f,
            this::onBack
        );
    }

    private static Texture solidTexture(Color color) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fill();
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    @Override
    protected void onBack() {
        game.goTo(new CharacterSelectionScreen(game));
    }

    @Override
    public void dispose() {
        super.dispose();
        kit.dispose();
        if (charTexture != null) charTexture.dispose();
        if (panelTexture != null) panelTexture.dispose();
    }

    private static class Board extends WidgetGroup {

        private final Image bg;

        private final Array<Actor> items = new Array<>();
        private final Array<float[]> pos = new Array<>();

        Board(Texture texture) {
            bg = new Image(texture);
            bg.setScaling(Scaling.stretch);

            addActor(bg);
            setFillParent(true);
        }

        void placeAspect(Actor actor, float fx, float fyFromTop, float widthFrac, float aspect) {
            addActor(actor);
            items.add(actor);
            pos.add(new float[]{fx, fyFromTop, widthFrac, aspect, 0f});
        }

        void placeBox(Actor actor, float fx, float fyFromTop, float widthFrac, float heightFrac) {
            addActor(actor);
            items.add(actor);
            pos.add(new float[]{fx, fyFromTop, widthFrac, heightFrac, 1f});
        }

        @Override
        public void layout() {
            float w = getWidth();
            float h = getHeight();

            bg.setBounds(0, 0, w, h);

            for (int i = 0; i < items.size; i++) {
                Actor actor = items.get(i);
                float[] p = pos.get(i);

                float actorWidth = p[2] * w;
                float actorHeight = (p[4] == 1f) ? (p[3] * h) : (actorWidth * p[3]);
                actor.setSize(actorWidth, actorHeight);

                actor.setPosition(
                    p[0] * w - actor.getWidth() / 2f,
                    (1f - p[1]) * h - actor.getHeight() / 2f
                );
            }
        }
    }
}
