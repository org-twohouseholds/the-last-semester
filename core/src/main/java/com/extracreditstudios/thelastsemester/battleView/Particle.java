package com.extracreditstudios.thelastsemester.battleView;
import com.badlogic.gdx.graphics.Texture;
public class Particle {
    public Texture look;
    public float x;
    public float y;
    public int behaviour;
    public int timeToDie;
    public int width;
    public int height;

    public Particle(String look, float x, float y, int behaviour, int timeToDie){
        this.look=new Texture(look);
        this.x=x;
        this.y=y;
        this.behaviour=behaviour;
        this.timeToDie=timeToDie;
        height = this.look.getHeight();
        width = this.look.getWidth();

    }

    public boolean updateParticle(){
        if (timeToDie<=0){
            return true;
        }
        switch (behaviour){
            case 1:
                y+=10;
        }
        timeToDie--;
        return false;
    }

}
