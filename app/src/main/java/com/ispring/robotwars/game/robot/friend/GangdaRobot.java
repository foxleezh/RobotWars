package com.ispring.robotwars.game.robot.friend;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/9/2.
 */

public class GangdaRobot extends FriendRobot{
    public GangdaRobot(Bitmap bitmap, int xIndex, int yIndex) {
        super(bitmap, xIndex, yIndex);
        step=9;
    }
}
