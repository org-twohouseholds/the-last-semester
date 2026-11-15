package com.extracreditstudios.thelastsemester.battleView;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AttackButton {
    private final Texture texture;
    private final Attack attack;
    private final float x, y, width, height;
    private final GlyphLayout layout = new GlyphLayout();

    public AttackButton(Texture texture, Attack attack, float x, float y, float width, float height) {
        this.texture = texture;
        this.attack = attack;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Prüft, ob der Klickpunkt im Button liegt
    public boolean contains(Vector2 p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    public void draw(SpriteBatch batch, BitmapFont font, boolean selected, boolean affordable) {
        // Button färben (sieht man bei den schwarzen Bsp. Buttons nicht, ist für später)
        if (!affordable) {
            batch.setColor(0.5f, 0.5f, 0.5f, 1f);   // grau: Mana reicht nicht
        } else if (selected) {
            batch.setColor(0.6f, 1f, 0.6f, 1f);     // grün: gewählt
        }
        batch.draw(texture, x, y, width, height);
        batch.setColor(1, 1, 1, 1);

        layout.setText(font, attack.name);
        font.draw(batch, attack.name, x + 15, y + (height + layout.height) / 2f);


    }
}
