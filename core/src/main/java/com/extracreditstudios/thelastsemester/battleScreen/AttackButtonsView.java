package com.extracreditstudios.thelastsemester.battleScreen;

// creates and manages attack buttons on battle screen
// updates appearance based on battle state

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

    public class AttackButtonsView {

    private static final float BUTTON_W = 165f;
    private static final float BUTTON_H = 65f;
    private static final float BUTTON_PAD = 6f;
    private static final float PAD_BOTTOM = 25f;
    private final TextButton[] buttons;

    // shadow for the selected button and for unaffordable attacks
    private static final Color COLOR_NORMAL = Color.WHITE;
    private static final Color COLOR_SELECTED = new Color(0.75f, 0.75f, 0.75f, 1f);
    private static final Color COLOR_DISABLED = new Color(0.5f, 0.5f, 0.5f, 1f);

    // initializes attack buttons using the provided textures, font and attack data
    public AttackButtonsView(
        Stage stage,
        Texture buttonTexture,
        BitmapFont font,
        Attack[] attacks,
        OnAttackSelected callback
    ) {

        TextureRegionDrawable drawable =
            new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = drawable;
        style.down = drawable;
        style.checked = drawable;
        style.disabled = drawable;
        style.font = font;
        style.fontColor = Color.BLACK;
        style.disabledFontColor = Color.BLACK;

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(PAD_BOTTOM);

        buttons = new TextButton[attacks.length];

        // creates a button for an attack using its name as label
        for (int i = 0; i < buttons.length; i++) {
            final int attackIndex = i;
            TextButton button = new TextButton(attacks[i].name, style);

            // adds click listener that notifies callback when attack is selected
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callback.selected(attackIndex);
                }
            });

            buttons[i] = button;
            table.add(button).width(BUTTON_W).height(BUTTON_H).pad(BUTTON_PAD);

            //buttons are arranged in rows of two
            if (i % 2 == 1) {
                table.row();
            }
        }
        stage.addActor(table);
    }

    // updates attack button based on current battle state
    public void update(Battle battle, boolean roundOver) {

        for (int i = 0; i < buttons.length; i++) {

            Attack attack = battle.player.attacks[i];

            boolean selected = (i == battle.playerChoice);
            boolean disabled = !battle.player.canUse(attack) || battle.isOver() || roundOver;

            buttons[i].setText(
                attack.name +
                    "\nDamage: " + attack.damage +
                    " | Energy: " + attack.energyCost
            );

            buttons[i].setDisabled(disabled);

            if (disabled) {
                buttons[i].setColor(COLOR_DISABLED);
            } else if (selected) {
                buttons[i].setColor(COLOR_SELECTED);
            } else {
                buttons[i].setColor(COLOR_NORMAL);
            }
        }
    }
}




