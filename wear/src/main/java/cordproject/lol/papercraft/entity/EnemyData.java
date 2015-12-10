package cordproject.lol.papercraft.entity;

/**
 * Created by matthewlim on 11/11/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class EnemyData {
    public final static int FRONT = 0;
    public final static int BACK = 1;
    public final static int NONE = -1;

    public final static int CIRCLE = 0;
    public final static int DIAMOND = 1;
    public final static int PENTAGON = 2;

    public float speedX, speedY;

    public int shieldPosition = NONE;
    public int shape;
    public float speed;
    public float x, y;
    public float origin, destination;
    public float screenBoundary;
    public boolean shouldDelete = false;
    public int bitmapIndex = 0;
    public boolean reachedOffscreen = false;
    public boolean alert = false;

    public void move(float speedX, float speedY) {
        if (x > destination) {
            if (x > screenBoundary) {
                x -= speedX*3;
                y -= speedY*3;
            } else if (x <= origin/7){
                alert = true;
                x-=speedX/5;
                y-=speedY/5;
            } else if (x <= origin/6){
                alert = true;
                x-=speedX/4;
                y-=speedY/4;
            } else if (x <= origin/5){
                alert = true;
                x-=speedX/3;
                y-=speedY/3;
            }else if (x <= origin/4){
                alert = true;
                x-=speedX/2;
                y-=speedY/2;
            }else {
                x-=speedX;
                y-=speedY;
            }
        } else {
            shouldDelete = true;
            reachedOffscreen = true;
        }
    }
}