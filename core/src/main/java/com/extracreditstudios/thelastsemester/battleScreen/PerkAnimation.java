package com.extracreditstudios.thelastsemester.battleScreen;

// animations for the 8 shop perks (done so far: Coffee Rush, Copy & Paste)
// the basic attack and perks without their own animation use basicAttack()

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;

public class PerkAnimation implements Disposable {

    private static final Color HIT_COLOR = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color COFFEE_COLOR = new Color(0.45f, 0.27f, 0.12f, 1f);
    private static final Color GHOST_COLOR = new Color(0.6f, 0.85f, 1f, 0.6f);

    private final Stage stage;
    private final BitmapFont font;
    private final Texture dotTexture;

    public PerkAnimation(Stage stage, BitmapFont font) {
        this.stage = stage;
        this.font = font;

        // small white circle, tinted per effect (coffee drops etc.)
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(8, 8, 7);
        dotTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    // attacker attacks target with the given attack; damage is the HP the target lost
    public void play(Image attacker, Image target, Attack attack, int damage) {
        switch (attack.name) {
            case "Coffee Rush":
                coffeeRush(attacker, target, damage);
                break;
            case "Copy & Paste":
                copyPaste(attacker, target, damage);
                break;
            // TODO: All-Nighter, Group Project, Deadline Panic,
            //       Office Hours, Mensa Special, Plagiarism
            default:
                basicAttack(attacker, target, damage);
                break;
        }
    }

    // ---------- attacks ----------

    // short lunge towards the target and back
    private void basicAttack(Image attacker, Image target, int damage) {
        float dir = direction(attacker, target);
        attacker.addAction(sequence(
            moveBy(dir * 35f, 0f, 0.12f, Interpolation.pow2Out),
            moveBy(-dir * 35f, 0f, 0.18f, Interpolation.pow2In)
        ));
        hit(target, damage, 0.12f);
    }

    // attacker turns brown, three coffee drops fly fast to the target
    private void coffeeRush(Image attacker, Image target, int damage) {
        popText("COFFEE RUSH!", attacker, COFFEE_COLOR, 0f);

        attacker.addAction(sequence(
            color(COFFEE_COLOR, 0.1f),
            moveBy(direction(attacker, target) * 15f, 0f, 0.08f),
            moveBy(-direction(attacker, target) * 15f, 0f, 0.08f),
            color(Color.WHITE, 0.3f)
        ));

        for (int i = 0; i < 3; i++) {
            projectile(attacker, target, COFFEE_COLOR, 0.15f + i * 0.08f, 0.25f);
        }
        hit(target, damage, 0.4f + 2 * 0.08f);
    }

    // a transparent copy of the attacker slides to the target ("Ctrl+C" -> "Ctrl+V")
    private void copyPaste(Image attacker, Image target, int damage) {
        popText("Ctrl+C", attacker, Color.WHITE, 0f);

        Image ghost = new Image(attacker.getDrawable());
        ghost.setBounds(attacker.getX(), attacker.getY(),
            attacker.getWidth(), attacker.getHeight());
        ghost.setColor(GHOST_COLOR.r, GHOST_COLOR.g, GHOST_COLOR.b, 0f);
        ghost.setTouchable(Touchable.disabled);
        stage.addActor(ghost);

        float targetX = target.getX() + (target.getWidth() - ghost.getWidth()) / 2f;
        ghost.addAction(sequence(
            alpha(GHOST_COLOR.a, 0.2f),
            moveTo(targetX, ghost.getY(), 0.35f, Interpolation.pow2In),
            fadeOut(0.2f),
            removeActor()
        ));

        popText("Ctrl+V", target, Color.WHITE, 0.55f);
        hit(target, damage, 0.55f);
    }

    // ---------- building blocks ----------

    // target flashes red, shakes and shows the damage number
    private void hit(Image target, int damage, float startDelay) {
        target.addAction(sequence(
            delay(startDelay),
            color(HIT_COLOR, 0.05f),
            moveBy(8f, 0f, 0.04f),
            moveBy(-16f, 0f, 0.04f),
            moveBy(16f, 0f, 0.04f),
            moveBy(-8f, 0f, 0.04f),
            color(Color.WHITE, 0.25f)
        ));
        if (damage > 0) {
            popText("-" + damage, target, Color.RED, startDelay);
        }
    }

    // text that appears above an actor, rises and fades out
    private void popText(String text, Image above, Color color, float startDelay) {
        Label label = new Label(text, new Label.LabelStyle(font, Color.WHITE));
        label.setFontScale(1.6f);
        label.pack();
        label.setColor(color.r, color.g, color.b, 0f);
        label.setPosition(
            above.getX() + (above.getWidth() - label.getWidth()) / 2f,
            above.getY() + above.getHeight() * 0.85f
        );
        stage.addActor(label);

        label.addAction(sequence(
            delay(startDelay),
            alpha(1f),
            parallel(
                moveBy(0f, 60f, 0.9f, Interpolation.pow2Out),
                sequence(delay(0.5f), fadeOut(0.4f))
            ),
            removeActor()
        ));
    }

    // a small dot flying in an arc from the attacker to the target
    private void projectile(Image from, Image to, Color color, float startDelay, float duration) {
        Image dot = new Image(dotTexture);
        dot.setSize(18f, 18f);
        dot.setColor(color.r, color.g, color.b, 0f);

        float startX = from.getX() + from.getWidth() * 0.7f;
        float startY = from.getY() + from.getHeight() * 0.6f;
        float endX = to.getX() + to.getWidth() * 0.4f;
        float endY = to.getY() + to.getHeight() * 0.55f;

        dot.setPosition(startX, startY);
        stage.addActor(dot);

        dot.addAction(sequence(
            delay(startDelay),
            alpha(1f),
            moveTo((startX + endX) / 2f, Math.max(startY, endY) + 40f, duration / 2f),
            moveTo(endX, endY, duration / 2f),
            removeActor()
        ));
    }

    // +1 if the target is to the right of the attacker, otherwise -1
    private float direction(Image attacker, Image target) {
        return target.getX() > attacker.getX() ? 1f : -1f;
    }

    @Override
    public void dispose() {
        dotTexture.dispose();
    }
}
