package com.extracreditstudios.thelastsemester.battleScreen;

// animations for the 4 character passives (one per character)
// the visuals are placeholders until the team has defined what each passive does

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

public class PassiveAnimation implements Disposable {

    private static final Color HACKER_GREEN = new Color(0.2f, 1f, 0.3f, 1f);
    private static final Color ESOTERIC_PURPLE = new Color(0.75f, 0.45f, 1f, 1f);
    private static final Color BITTER_GREY = new Color(0.55f, 0.6f, 0.75f, 1f);
    private static final Color ALCOHOL_AMBER = new Color(1f, 0.75f, 0.2f, 1f);

    private final Stage stage;
    private final BitmapFont font;
    private final Texture circleTexture;

    public PassiveAnimation(Stage stage, BitmapFont font) {
        this.stage = stage;
        this.font = font;

        // white circle, tinted per effect (aura, bubbles)
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(32, 32, 31);
        circleTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    // plays the passive of the given character (names as in CharacterSelectionScreen)
    public void play(Image character, String characterName) {
        switch (characterName) {
            case "The Hacker":
                hacker(character);
                break;
            case "The Esoteric":
                esoteric(character);
                break;
            case "The Bitter One":
                bitterOne(character);
                break;
            case "The Alcoholic":
                alcoholic(character);
                break;
            default:
                break;
        }
    }

    // ---------- passives ----------

    // turns green, zeros and ones pop up around the character
    private void hacker(Image character) {
        popText("ACCESS GRANTED", character, HACKER_GREEN, 0f);
        flash(character, HACKER_GREEN);

        for (int i = 0; i < 8; i++) {
            float x = character.getX() + MathUtils.random(0.1f, 0.9f) * character.getWidth();
            float y = character.getY() + MathUtils.random(0.2f, 0.8f) * character.getHeight();
            String bit = MathUtils.randomBoolean() ? "0" : "1";
            floatingText(bit, x, y, HACKER_GREEN, i * 0.08f);
        }
    }

    // purple aura grows twice behind the character
    private void esoteric(Image character) {
        popText("INNER PEACE", character, ESOTERIC_PURPLE, 0f);
        flash(character, ESOTERIC_PURPLE);

        for (int i = 0; i < 2; i++) {
            Image aura = new Image(circleTexture);
            float size = character.getWidth() * 1.2f;
            aura.setSize(size, size);
            aura.setOrigin(Align.center);
            aura.setPosition(
                character.getX() + (character.getWidth() - size) / 2f,
                character.getY() + character.getHeight() * 0.45f - size / 2f
            );
            aura.setColor(ESOTERIC_PURPLE.r, ESOTERIC_PURPLE.g, ESOTERIC_PURPLE.b, 0f);
            aura.setScale(0.3f);
            aura.setTouchable(Touchable.disabled);
            stage.addActor(aura);
            aura.setZIndex(character.getZIndex()); // behind the character

            aura.addAction(sequence(
                delay(i * 0.4f),
                alpha(0.5f),
                parallel(
                    scaleTo(1.4f, 1.4f, 0.8f, Interpolation.pow2Out),
                    fadeOut(0.8f)
                ),
                removeActor()
            ));
        }
    }

    // turns grey and slumps down for a moment
    private void bitterOne(Image character) {
        popText("Ugh...", character, BITTER_GREY, 0f);
        character.addAction(sequence(
            color(BITTER_GREY, 0.2f),
            moveBy(0f, -12f, 0.25f, Interpolation.pow2Out),
            delay(0.3f),
            moveBy(0f, 12f, 0.3f, Interpolation.pow2In),
            color(Color.WHITE, 0.3f)
        ));
    }

    // sways left and right, amber bubbles rise
    private void alcoholic(Image character) {
        popText("Cheers!", character, ALCOHOL_AMBER, 0f);
        flash(character, ALCOHOL_AMBER);

        character.setOrigin(Align.bottom);
        character.addAction(sequence(
            rotateBy(6f, 0.2f), rotateBy(-12f, 0.35f),
            rotateBy(12f, 0.35f), rotateBy(-6f, 0.2f)
        ));

        for (int i = 0; i < 6; i++) {
            Image bubble = new Image(circleTexture);
            float size = MathUtils.random(8f, 16f);
            bubble.setSize(size, size);
            bubble.setPosition(
                character.getX() + MathUtils.random(0.3f, 0.7f) * character.getWidth(),
                character.getY() + character.getHeight() * 0.5f
            );
            bubble.setColor(ALCOHOL_AMBER.r, ALCOHOL_AMBER.g, ALCOHOL_AMBER.b, 0f);
            bubble.setTouchable(Touchable.disabled);
            stage.addActor(bubble);

            bubble.addAction(sequence(
                delay(i * 0.12f),
                alpha(0.9f),
                parallel(
                    moveBy(MathUtils.random(-20f, 20f), 120f, 1f, Interpolation.sineOut),
                    sequence(delay(0.6f), fadeOut(0.4f))
                ),
                removeActor()
            ));
        }
    }

    // ---------- building blocks ----------

    // character briefly takes on a color and goes back to normal
    private void flash(Image character, Color color) {
        character.addAction(sequence(
            color(color, 0.15f),
            delay(0.3f),
            color(Color.WHITE, 0.4f)
        ));
    }

    // text above the character that rises and fades out
    private void popText(String text, Image above, Color color, float startDelay) {
        Label label = new Label(text, new Label.LabelStyle(font, Color.WHITE));
        label.setFontScale(1.4f);
        label.pack();
        floatingLabel(label,
            above.getX() + (above.getWidth() - label.getWidth()) / 2f,
            above.getY() + above.getHeight() * 0.9f,
            color, startDelay);
    }

    // small text at a given position that rises and fades out
    private void floatingText(String text, float x, float y, Color color, float startDelay) {
        Label label = new Label(text, new Label.LabelStyle(font, Color.WHITE));
        label.pack();
        floatingLabel(label, x, y, color, startDelay);
    }

    private void floatingLabel(Label label, float x, float y, Color color, float startDelay) {
        label.setColor(color.r, color.g, color.b, 0f);
        label.setPosition(x, y);
        label.setTouchable(Touchable.disabled);
        stage.addActor(label);

        label.addAction(sequence(
            delay(startDelay),
            alpha(1f),
            parallel(
                moveBy(0f, 50f, 0.9f, Interpolation.pow2Out),
                sequence(delay(0.5f), fadeOut(0.4f))
            ),
            removeActor()
        ));
    }

    @Override
    public void dispose() {
        circleTexture.dispose();
    }
}
