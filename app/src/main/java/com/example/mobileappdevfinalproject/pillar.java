package com.example.mobileappdevfinalproject;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.firebase.firestore.FirebaseFirestore;

public class pillar
{

    private int tX;

    private int speed;

    private int space;
    private int top, bottom;

    private Bitmap topPillar, bottomPillar;

    public pillar(int spd, int minSpace, int maxSpace)
    {
        tX = 970;
        speed = spd;
        do{
            top = (int)(Math.random()*950)+200;
            bottom = (int)(Math.random()*950)+200;
        }while(!((bottom > top) && ((bottom-top > minSpace) && (bottom-top < maxSpace))));
        space = bottom-top;
    }
    public pillar(int spd, int minSpace, int maxSpace, Bitmap topP, Bitmap botP)
    {
        tX = 970;
        speed = spd;
        do{
            top = (int)(Math.random()*950)+200;
            bottom = (int)(Math.random()*950)+200;
        }while(!((bottom > top) && ((bottom-top > minSpace) && (bottom-top < maxSpace))));
        space = bottom-top;
        topPillar = topP;
        bottomPillar = botP;
    }
    public void incSpeed(int spd)
    {
        speed=spd;
    }
    public void decSpace(int sce)
    {
        space=sce;
    }

    public int getX() {
        return tX;
    }

    public int getSpace() {
        return space;
    }
    public int getSpeed() {
        return speed;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }
    public int getTopLength() {
        return top;
    }

    public int getBottomLength() {
        return 1600-bottom;
    }

    public void setTopPillar(Bitmap topPillar) {
        this.topPillar = topPillar;
    }

    public void setBottomPillar(Bitmap bottomPillar) {
        this.bottomPillar = bottomPillar;
    }

    public Bitmap getTopPillar() {
        return topPillar;
    }

    public Bitmap getBottomPillar() {
        return bottomPillar;
    }

    public void updatePillar() {
        tX -= speed;
    }

    public boolean ifCrashed(int playerX, int playerY)
    {
        Rect player = new Rect(playerX, playerY, playerX+200, playerY+200);
        Rect pillarTop = new Rect(tX, 0, tX+150, top);
        Rect pillarBot = new Rect(tX, bottom, tX+150, 1600);

        if(player.intersect(pillarTop) || player.intersect(pillarBot))
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
