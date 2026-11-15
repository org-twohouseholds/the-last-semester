
package com.extracreditstudios.thelastsemester.battleView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.badlogic.gdx.Screen;

public class BattleScreen extends ScreenAdapter {

    private final TheLastSemesterGame game;
    private final Screen nextScreen;

    public BattleScreen(TheLastSemesterGame game, Screen nextScreen) {
        this.game = game;
        this.nextScreen = nextScreen;
    }

    private SpriteBatch batch;
    private Viewport viewport;
    private BitmapFont font;
    private BitmapFont countdownFont;
    private GlyphLayout layout = new GlyphLayout();

    private Texture background;
    private Texture bitterOne;
    private Texture hacker;
    private Texture button;
    private Texture hpBar;
    private Texture manaBar;
    private Texture emptyBar;

    private StatusBar playerHp;
    private StatusBar playerMana;
    private StatusBar enemyHp;
    private StatusBar enemyMana;

    private AttackButton[] attackButtons;
    private Battle battle;


    @Override
    public void show() {
        viewport = new ExtendViewport(400, 700);
        batch = new SpriteBatch();
        font = new BitmapFont();

        countdownFont = new BitmapFont();
        countdownFont.getData().setScale(5f);

        button = new Texture("BattlescreenUI/Empty.png");
        hpBar = new Texture("BattlescreenUI/Hp.png");
        manaBar = new Texture("BattlescreenUI/Mana.png");
        emptyBar = new Texture("BattlescreenUI/Empty.png");
        background = new Texture("backgrounds/Background.png");

        playerHp = new StatusBar(hpBar, emptyBar, 20, 660, false);
        playerMana = new StatusBar(manaBar, emptyBar, 20, 640, false);
        enemyHp = new StatusBar(hpBar, emptyBar, 230, 660, true);
        enemyMana = new StatusBar(manaBar, emptyBar, 230, 640, true);

        bitterOne = new Texture(Gdx.files.internal("Characters/TheBitterOne.png"), true);
        hacker = new Texture(Gdx.files.internal("Characters/TheHacker.png"), true);

        bitterOne.setFilter(Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear);
        hacker.setFilter(Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear);

        Fighter bitter = new Fighter(new Attack[] {
            new Attack("Attacke 1", 20, 10),
            new Attack("Attacke 2", 35, 25),
            new Attack("Attacke 3", 10, 0),
            new Attack("Attacke 4", 50, 60)
        });

        Fighter hackerFighter = new Fighter(new Attack[] {
            new Attack("Hack 1", 15, 5),
            new Attack("Hack 2", 30, 20)
        });

        bitter.hp = 50;
        bitter.mana = 100;

        battle = new Battle(bitter, hackerFighter);

        attackButtons = new AttackButton[battle.player.attacks.length];

        for (int i = 0; i < attackButtons.length; i++) {
            attackButtons[i] = new AttackButton(
                button, battle.player.attacks[i],
                30, 170 - i * 50, 340, 40
            );
        }
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        if (!battle.isOver()) {
            delta = Math.min(delta, 0.05f);
            battle.update(delta);
        }

        if (battle.isOver()) {
            game.goTo(nextScreen);
            return;
        }

        // Eingabe
        if (Gdx.input.justTouched() && !battle.isOver()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch);

            for (int i = 0; i < attackButtons.length; i++) {
                if (attackButtons[i].contains(touch)) {
                    battle.choose(i);
                    break;
                }
            }
        }

        // Zeichnen
        batch.begin();

        batch.draw(background, 0, 0,
            viewport.getWorldWidth(), viewport.getWorldHeight());

        batch.draw(bitterOne, -10, 180, 170, 430);
        batch.draw(hacker, 400, 200, -150, 400);

        playerHp.draw(batch, battle.player.hp);
        playerMana.draw(batch, battle.player.mana);
        enemyHp.draw(batch, battle.enemy.hp);
        enemyMana.draw(batch, battle.enemy.mana);

        for (int i = 0; i < attackButtons.length; i++) {
            Attack a = battle.player.attacks[i];

            attackButtons[i].draw(
                batch,
                font,
                i == battle.playerChoice,
                battle.player.canUse(a)
            );
        }

        int seconds = (int) Math.ceil(battle.timeLeft);

        if (seconds <= 3) {
            countdownFont.setColor(Color.RED);
        } else {
            countdownFont.setColor(Color.WHITE);
        }

        layout.setText(countdownFont, String.valueOf(seconds));

        float x = (viewport.getWorldWidth() - layout.width) / 2f;
        float y = viewport.getWorldHeight() * 0.9f;

        countdownFont.draw(batch, layout, x, y);

        batch.end();
    }


    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (countdownFont != null) countdownFont.dispose();

        if (background != null) background.dispose();
        if (bitterOne != null) bitterOne.dispose();
        if (hacker != null) hacker.dispose();
        if (button != null) button.dispose();
        if (hpBar != null) hpBar.dispose();
        if (manaBar != null) manaBar.dispose();
        if (emptyBar != null) emptyBar.dispose();
    }

}
