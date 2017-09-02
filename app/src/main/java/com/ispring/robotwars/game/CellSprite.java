package com.ispring.robotwars.game;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/9/2.
 */

public class CellSprite extends CurveSprite{

    public void setxIndex(int xIndex) {
        this.xIndex = xIndex;
        setX((xIndex)*GameView.cellWidth);
    }

    public void setyIndex(int yIndex) {
        this.yIndex = yIndex;
        setY((yIndex)*GameView.cellWidth);
    }

    public int xIndex;
    public int yIndex;

    public CellSprite(Bitmap bitmap,int xIndex,int yIndex) {
        super(bitmap);
        setxIndex(xIndex);
        setyIndex(yIndex);
    }

    @Override
    public float getWidth() {
        return GameView.cellWidth;
    }

    @Override
    public float getHeight() {
        return GameView.cellWidth;
    }
}
