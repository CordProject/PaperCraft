package cordproject.lol.papercraft.entity;

import android.graphics.Matrix;

/**
 * Created by matthewlim on 11/11/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class BulletData {
    public float x, y;
    public float length;
    public float destination;
    public boolean shouldDelete = false;

    public float speedX, speedY;
    public Matrix rotMat = new Matrix();
    public Matrix transMat = new Matrix();
    public Matrix shadowTransMat = new Matrix();
    public float lifeSpan= 0.f;
    public float rotAngle;

    public void setDestination(float destination) {
        this.destination = destination;
        lifeSpan = 1.f;
    }

    public float getLifeSpan() {
        return lifeSpan;
    }

    public void move() {
        if (destination <= 0) {
            x-=9;
            if (x <= destination) {
                shouldDelete = true;
            }
        } else {
            x+=3;
            if (x >= destination) {
                shouldDelete = true;
            }
        }
    }

    public void move(float speedX, float speedY) {
        if (destination <= 0) {
            x -= speedX;
            y -= speedY;
            if (x <= destination) {
                shouldDelete = true;
            }
        } else {
            x += speedX;
            y += speedY;
            if (x >= destination) {
                shouldDelete = true;
            }
        }
        lifeSpan = Math.min(1.f, Math.abs((x - destination)/(destination*.1f)));

    }
}
