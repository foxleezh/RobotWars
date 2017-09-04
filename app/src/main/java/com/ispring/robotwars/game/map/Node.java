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
    public int step;

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

    /**
     * 递归，在反转当前节点之前先反转后续节点
     */
    public Node reverse1(Node head) {
        // head看作是前一结点，head.getNext()是当前结点，reHead是反转后新链表的头结点
        if (head == null || head.parent== null) {
            return head;// 若为空链或者当前结点在尾结点，则直接还回
        }
        Node reHead = reverse1(head.parent);// 先反转后续节点head.getNext()
        head.parent.parent=(head);// 将当前结点的指针域指向前一结点
        head.parent=(null);// 前一结点的指针域令为null;
        return reHead;// 反转后新链表的头结点
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
