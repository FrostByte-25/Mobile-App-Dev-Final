package com.example.mobileappdevfinalproject;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class portal {
    private String type;
    private Bitmap sprite;
    int px, py;
    int speed;
    int height;

    public portal(String t, int top, Bitmap spr, int x, int spd, int h)
    {
        type = t;
        py = top;
        px = x;
        sprite = spr;
        speed = spd;
        height = h;
    }
    public portal(int top, int bot)
    {
        int rand = (int)(Math.random()*5);
        switch (rand){
            case 0:
                type = "ufo";
                break;
            case 1:
                type = "swing";
                break;
            case 2:
                type = "wave";
                break;
            case 3:
                type = "normalGravity";
                break;
            case 4:
                type = "reversedGravity";
                break;
        }
    }
    public void updatePortal() {
        px -= speed;
    }
    public void incSpeed(int spd)
    {
        speed=spd;
    }

    public String getType() {
        return type;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public int getPx() {
        return px;
    }

    public int getPy() {
        return py;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean ifEntered(int playerX, int playerY)
    {
        Rect player = new Rect(playerX, playerY, playerX+200, playerY+200);
        Rect portHitbox = new Rect(px,py,px+150,py+height);

        if(player.intersect(portHitbox))
        {
            return true;
        }
        return false;

        /*
        int pX2 = pX + 200;
        int pY2 = pY + 250;

        int aX2 = aX + 100;
        int aY2 = aY + 100;

        Rect player = new Rect(pX, pY, pX2, pY2);
        Rect enemy = new Rect(aX, aY, aX2, aY2);

        Log.d("hitbox", player.toString());

        if(player.intersect(enemy))
        {
            return true;
        }
        return false;
        */
    }

}
