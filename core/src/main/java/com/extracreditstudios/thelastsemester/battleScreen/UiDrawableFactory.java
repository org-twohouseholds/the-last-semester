package com.extracreditstudios.thelastsemester.battleScreen;
// creates and manages drawable textures used by the UI (RoundTimer, StatusBars)

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class UiDrawableFactory implements Disposable {

    // stores all created textures for later disposal
    private final Array<Texture> textures = new Array<>();

    // creates a solid-colored drawable
    public TextureRegionDrawable solid(Color color, float minHeight) {

        // creates a 1x1 pixel image
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        // creates a texture from the pixel
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textures.add(texture);

        // creates a drawable from the texture
        TextureRegionDrawable drawable =
            new TextureRegionDrawable(new TextureRegion(texture));
        drawable.setMinHeight(minHeight);

        return drawable;
    }

    @Override
    public void dispose() {
        for (Texture t : textures) {
            t.dispose();
        }
        textures.clear();
    }
}


