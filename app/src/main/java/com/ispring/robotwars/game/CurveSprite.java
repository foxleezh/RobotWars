package com.ispring.robotwars.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 走直线的Sprite类，其位置只能直上直下
 */
public class CurveSprite extends Sprite {
    //每帧移动的像素数,以向下为正
    private float speed = 0;

    public void setChangeOritation(boolean changeOritation) {
        this.changeOritation = changeOritation;
    }

    private boolean changeOritation=true;

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public double getDegree() {
        return degree;
    }

    private double degree = 0;

    public CurveSprite(Bitmap bitmap){
        super(bitmap);
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }

    public float getSpeed(){
        return speed;
    }

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(!isDestroyed()){
            //在y轴方向移动speed像素
            move((float) (Math.cos(degree)*speed * gameView.getDensity()), (float) (Math.sin(degree)*speed * gameView.getDensity()));
        }

    }

    @Override
    public void onDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(changeOritation) {
            float newDegree = (float) (degree + Math.PI / 2);
            canvas.rotate((float) (newDegree / Math.PI * 180), getX() + getWidth() / 2f, getY() + getHeight() / 2);
            super.onDraw(canvas, paint, gameView);
            canvas.rotate((float) (-newDegree / Math.PI * 180), getX() + getWidth() / 2f, getY() + getHeight() / 2);
        }else {
            super.onDraw(canvas, paint, gameView);
        }
    }

    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView){
//        if(!isDestroyed()){
//            //检查Sprite是否超出了Canvas的范围，如果超出，则销毁Sprite
//            RectF canvasRecF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
//            RectF spriteRecF = getRectF();
//            if(!RectF.intersects(canvasRecF, spriteRecF)){
//                destroy();
//            }
//        }
    }
}