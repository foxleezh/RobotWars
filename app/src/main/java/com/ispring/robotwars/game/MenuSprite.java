package com.ispring.robotwars.game;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/9/2.
 */

public class MenuSprite extends CellSprite{

    public static final int MOVE = 1;
    public static final int SKILL = 2;
    public static final int STATE = 3;

    public int action;
    public Sprite target;

    public MenuSprite(Bitmap bitmap, int xIndex, int yIndex) {
        super(bitmap, xIndex, yIndex);
    }
}
