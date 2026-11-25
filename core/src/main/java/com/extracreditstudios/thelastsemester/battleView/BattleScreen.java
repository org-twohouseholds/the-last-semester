
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
import java.util.*;

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
    private Texture youSprite;
    private Texture enemySprite;
    private Texture button;
    private Texture hpBar;
    private Texture manaBar;
    private Texture emptyBar;

    private float youSprite_xOffset;
    private float yousprite_yOffset;
    private int yousprite_greennessTimer;
    private float enemysprite_xOffset;
    private float enemysprite_yOffset;
    private int enemysprite_rednessTimer;

    private StatusBar playerHp;
    private StatusBar playerMana;
    private StatusBar enemyHp;
    private StatusBar enemyMana;

    private AttackButton[] attackButtons;
    private Battle battle;

    private ArrayList<Particle> particles = new ArrayList<Particle>();

    private Random random = new Random();


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

        youSprite = new Texture(Gdx.files.internal("Characters/TheBitterOne.png"), true);
        enemySprite = new Texture(Gdx.files.internal("Characters/TheHacker.png"), true);
        youSprite_xOffset=0;
        yousprite_yOffset=0;
        enemysprite_xOffset=0;
        enemysprite_yOffset=0;

        youSprite.setFilter(Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear);
        enemySprite.setFilter(Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear);

        Fighter youFighter = new Fighter(new Attack[] {
            new Attack("Attacke 1", 20, 10),
            new Attack("Attacke 2", 35, 25),
            new Attack("Attacke 3", 10, 0),
            new Attack("Attacke 4", 50, 60)
        });

        Fighter enemyFighter = new Fighter(new Attack[] {
            new Attack("Hack 1", 15, 5),
            new Attack("Hack 2", 30, 20)
        });

        youFighter.hp = 50;
        youFighter.mana = 100;

        battle = new Battle(youFighter, enemyFighter);

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
            String updateReturn = battle.update(delta);
            if (updateReturn != null){
                if (updateReturn.startsWith("y")) {
                    switch (updateReturn.substring(1)) {
                        case "Attacke 2":
                            healthGainAnim();
                            break;
                        default:
                            basicAttackAnim();
                            break;
                    }
                }
            }

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
        if(yousprite_greennessTimer>0) {
            batch.setColor(0.5f, 1f, 0.5f, 1);
            yousprite_greennessTimer-=1;
        }
        batch.draw(youSprite, -10+youSprite_xOffset, 180+yousprite_yOffset, 170, 430);
        if(enemysprite_rednessTimer>0) {
            batch.setColor(1f, 0.5f, 0.5f, 1);
            enemysprite_rednessTimer-=1;
        } else {batch.setColor(1, 1, 1, 1);}
        batch.draw(enemySprite, 400+enemysprite_xOffset, 200+enemysprite_yOffset, -150, 400);
        batch.setColor(1, 1, 1, 1);

        for (int i = particles.size()-1; i>=0; i--){
            Particle p = particles.get(i);
            batch.draw(p.look,p.x,p.y,p.width,p.height);
            if (p.updateParticle()){
                particles.remove(p);
            }
        }

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
        if (youSprite != null) youSprite.dispose();
        if (enemySprite != null) enemySprite.dispose();
        if (button != null) button.dispose();
        if (hpBar != null) hpBar.dispose();
        if (manaBar != null) manaBar.dispose();
        if (emptyBar != null) emptyBar.dispose();
    }

    public void healthGainAnim(){
        yousprite_greennessTimer = 20;
        for (int i = 0; i<=4;i++){
            particles.add(new Particle("libgdx32.png", 50+random.nextInt(60), 400+random.nextInt(100), 1, 22));
        }
    }
    public void basicAttackAnim (){
        particles.add(new Particle("libgdx128.png", 240, 400, 0, 5));
        enemysprite_rednessTimer = 10;
    }

}


