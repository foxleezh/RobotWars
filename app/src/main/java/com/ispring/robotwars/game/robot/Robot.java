package com.ispring.robotwars.game.robot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.ispring.robotwars.game.CellSprite;
import com.ispring.robotwars.game.GameView;
import com.ispring.robotwars.game.map.Node;

/**
 * Created by Administrator on 2017/9/2.
 */

public class Robot extends CellSprite{

    public int step;

    public Node start;
    public Node end;

    public void setNode(Node start){
        this.start=start;
//        Node newNode=new Node(start.value);
//
//        /** 0-横，1-竖*/
//        int oritation=0;
//        Node temp=start;
//        Node parent;
//        while (temp.parent!=null){
//            parent=temp.parent;
//            if(temp.value.y==parent.value.y){
//
//            }
//        }
    }


    public Robot(Bitmap bitmap, int xIndex, int yIndex) {
        super(bitmap, xIndex, yIndex);
        setChangeOritation(false);
    }

    double degree;

    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(start!=null){
            end=start.parent;
            if(end!=null){
                setSpeed(2);
                degree=getDegree(start,end);
                setDegree(degree);
                if(degree==Math.PI/2){
                    if(getY()>end.value.y*getHeight()){
                        setY(end.value.y*getHeight());
                        start=end;
                    }
                }else if(degree==-Math.PI/2){
                    if(getY()<end.value.y*getHeight()){
                        setY(end.value.y*getHeight());
                        start=end;
                    }
                }else if(degree==0){
                    if(getX()>end.value.x*getHeight()){
                        setX(end.value.x*getHeight());
                        start=end;
                    }
                }else if(degree==Math.PI){
                    if(getX()<end.value.x*getHeight()){
                        setX(end.value.x*getHeight());
                        start=end;
                    }
                }
            }else {
                setSpeed(0);
                xIndex= start.value.x;
                yIndex=start.value.y;
                start=null;
            }
        }
        super.beforeDraw(canvas, paint, gameView);
    }

    public double getDegree(Node start,Node end){
        if(start.value.x==end.value.x){
            if(start.value.y<end.value.y){
                return Math.PI/2;
            }else {
                return -Math.PI/2;
            }
        }else {
            if(start.value.x<end.value.x){
                return 0;
            }else {
                return Math.PI;
            }
        }
    }
}
