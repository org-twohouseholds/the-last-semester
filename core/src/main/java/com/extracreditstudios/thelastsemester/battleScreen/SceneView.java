package com.extracreditstudios.thelastsemester.battleScreen;

// creates and manages the visual battle scene, including background and both characters

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

public class SceneView implements Disposable {

    private static final String BACKGROUND_PATH = "backgrounds/campus_2.png";
    private static final String PLAYER_PATH = "champions_resized/TheBitterOne.png";
    private static final String ENEMY_PATH = "champions_resized/TheHacker.png";

    // Reference height used to calculate positions and sizes
    private static final float WORLD_H = 700f;

    // Player position and size
    private static final float PLAYER_X = -10f;
    private static final float PLAYER_HEIGHT = 340f;
    private static final float PLAYER_Y = 120f;
    private static final float PLAYER_ASPECT = 170f / 430f;

    // Enemy position and size
    private static final float ENEMY_HEIGHT = 315f;
    private static final float ENEMY_Y = 155f;
    private static final float ENEMY_ASPECT = 150f / 400f;

    private final Texture backgroundTex;
    private final Texture playerTex;
    private final Texture enemyTex;

    private final Image background;
    private final Image player;
    private final Image enemy;

    // loads textures and adds scene elements to stage
    public SceneView(Stage stage) {

        backgroundTex = new Texture(Gdx.files.internal(BACKGROUND_PATH));
        playerTex = new Texture(Gdx.files.internal(PLAYER_PATH), true);
        enemyTex = new Texture(Gdx.files.internal(ENEMY_PATH), true);

        playerTex.setFilter(
            Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear
        );
        enemyTex.setFilter(
            Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear
        );

        background = new Image(backgroundTex);
        background.setScaling(Scaling.fill);

        player = new Image(playerTex);

        // mirror enemy image
        TextureRegion enemyRegion = new TextureRegion(enemyTex);
        enemyRegion.flip(true, false);
        enemy = new Image(enemyRegion);

        // order
        stage.addActor(background);
        stage.addActor(player);
        stage.addActor(enemy);
    }

    // adjusts size and position of scene elements
    public void layout(float w, float h) {

        background.setBounds(0, 0, w, h);

        // calculates scaling factor based on screen height
        float scale = h / WORLD_H;

        // calculates and sets players size and position
        float pH = PLAYER_HEIGHT * scale;
        player.setBounds(PLAYER_X, PLAYER_Y * scale, pH * PLAYER_ASPECT, pH);

        // calculates and sets enemies size and position
        float eH = ENEMY_HEIGHT * scale;
        float eW = eH * ENEMY_ASPECT;
        enemy.setBounds(w - eW, ENEMY_Y * scale, eW, eH);
    }

    public Image getPlayer() {
        return player;
    }

    public Image getEnemy() {
        return enemy;
    }

    @Override
    public void dispose() {
        backgroundTex.dispose();
        playerTex.dispose();
        enemyTex.dispose();
    }
}
