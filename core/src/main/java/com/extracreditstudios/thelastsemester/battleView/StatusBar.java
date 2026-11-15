package com.extracreditstudios.thelastsemester.battleView;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StatusBar {
    private final Texture fill;
    private final Texture empty;
    private final float x, y, width, height;
    private final boolean rightAnchored;

    public StatusBar(Texture fill, Texture empty, float x, float y, boolean rightAnchored) {
        this.fill = fill;
        this.empty = empty;
        this.x = x;
        this.y = y;
        this.width = 150;
        this.height = 10;
        this.rightAnchored = rightAnchored;
    }

    //Zeichnet den Balken. value ist der aktuelle Wert von 0 bis 100 (HP/Mana)
    public void draw(SpriteBatch batch, float value) {
        // Wert (0-100) in (0-1) umrechnen, nie unter 0 oder über 1
        float p = Math.max(0, Math.min(1, value / 100f));

        // So viele Pixel des bunten Bildes sollen sichtbar sein
        int visible = (int) (fill.getWidth() * p);

        batch.draw(empty, x, y, width, height);
        if (visible <= 0) return;

        if (rightAnchored) {
            batch.draw(fill, x + width * (1 - p), y, width * p, height,
                fill.getWidth() - visible, 0, visible, fill.getHeight());
        } else {
            batch.draw(fill, x, y, width * p, height,
                0, 0, visible, fill.getHeight());
        }
    }
}
