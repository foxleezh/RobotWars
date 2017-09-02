package com.ispring.robotwars.game.map;

import android.graphics.Point;

/**
 * Created by Administrator on 2017/9/2.
 */

public class Node {

    public static final int LEFT= 0;
    public static final int RIGHT= 1;
    public static final int MIDDLE= 2;
    public static final int MIDDLEBACK= 3;

    public Node(Point value) {
        this.value = value;
    }

    public Point value;

    public Node parent;
    public Node left;
    public Node right;
    public Node middle;
    public Node middleback;

    /** 往哪个方向走*/
    public int type;

    public int mode;

    @Override
    public String toString() {
        String log="";
        Node temp=this;
        log+=(temp.value.toString()+"->");
        while (temp.parent!=null){
            log+=(temp.parent.value.toString()+"->");
            temp=temp.parent;
        }
        return log;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        Node node = (Node) obj;
        return node.value.equals(value.x,value.y);
    }
}
