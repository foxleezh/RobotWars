package com.ispring.robotwars.game.map;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/2.
 */

public abstract class BaseMap {

    final static String TAG= "BaseMap";
    protected int[][] indexs;
    public int[][] block;
    public boolean[][] enable;
    public Node[][] nodes;

    public BaseMap() {
        getIndexs();
        block=new int[indexs.length][indexs[0].length];
        enable=new boolean[indexs.length][indexs[0].length];
        nodes=new Node[indexs.length][indexs[0].length];
        int[] inner;
        int index = 0;
        for (int i = 0; i < indexs.length; i++) {
            inner = indexs[i];
            for (int j = 0; j < inner.length; j++) {
                index = inner[j];
                if(index==0){
                    block[i][j]=1;
                }else if(index==1){
                    block[i][j]=3;
                }else if(index==2){
                    block[i][j]=3;
                }else if(index==3){
                    block[i][j]=1;
                }else if(index==4){
                    block[i][j]=2;
                }
            }
        }
    }

    public abstract int[][] getIndexs();

    private long lastTime;

    public boolean[][] getEnableMap(int x, int y,int step) {
        lastTime=System.currentTimeMillis();
        enable=new boolean[indexs.length][indexs[0].length];
        nodes=new Node[indexs.length][indexs[0].length];
        Node start=new Node(new Point(x,y));
        start.step=step;
        Order(start);
        Log.d(TAG, "timeCost: "+(System.currentTimeMillis()-lastTime));
        Log.d(TAG, "count: "+count);
        return enable;
    }

    public int count;

    private boolean hasSameNode(Node node){
        boolean hasSame=false;
        Node temp=node;
        while (temp.parent!=null){
            temp=temp.parent;
            if(temp.equals(node)){
                hasSame=true;
                break;
            }
        }
        return hasSame;
    }


    /** mode点所在象限，0-第四象限，1-第一，2-第二，3-第三*/
    private void Order(Node node) {
        count++;
        if(hasSameNode(node)){
            return;
        }
        Point leftPoint = new Point(node.value.x-1, node.value.y);
        if(leftPoint.y>=0&&leftPoint.y<block.length&&leftPoint.x>=0&&leftPoint.x<block[0].length) {
            node.left = new Node(leftPoint);
            node.left.step = node.step - block[leftPoint.y][leftPoint.x];
            node.left.type = Node.LEFT;
            node.left.parent = node;
            if (node.left.step > 0) {
                enable[leftPoint.y][leftPoint.x] = true;
                setNodes(node.left,leftPoint.x,leftPoint.y);
                Order(node.left);
            } else if (node.left.step == 0) {
                enable[leftPoint.y][leftPoint.x] = true;
                setNodes(node.left,leftPoint.x,leftPoint.y);
            }
        }
        Point rightPoint = new Point(node.value.x+1, node.value.y);
        if(rightPoint.y>=0&&rightPoint.y<block.length&&rightPoint.x>=0&&rightPoint.x<block[0].length) {
            node.right = new Node(rightPoint);
            node.right.step = node.step - block[rightPoint.y][rightPoint.x];
            node.right.type = Node.RIGHT;
            node.right.parent = node;
            if (node.right.step > 0) {
                enable[rightPoint.y][rightPoint.x] = true;
                setNodes(node.right,rightPoint.x,rightPoint.y);

                Order(node.right);
            } else if (node.right.step == 0) {
                enable[rightPoint.y][rightPoint.x] = true;
                setNodes(node.right,rightPoint.x,rightPoint.y);

            }
        }

        Point middle = new Point(node.value.x, node.value.y-1);
        if(middle.y>=0&&middle.y<block.length&&middle.x>=0&&middle.x<block[0].length) {
            node.middle = new Node(middle);
            node.middle.step = node.step - block[middle.y][middle.x];
            node.middle.type = Node.LEFT;
            node.middle.parent = node;
            if (node.middle.step > 0) {
                enable[middle.y][middle.x] = true;
                setNodes(node.middle,middle.x,middle.y);

                Order(node.middle);
            } else if (node.middle.step == 0) {
                enable[middle.y][middle.x] = true;
                setNodes(node.middle,middle.x,middle.y);

            }
        }

        Point middleback = new Point(node.value.x, node.value.y+1);
        if(middleback.y>=0&&middleback.y<block.length&&middleback.x>=0&&middleback.x<block[0].length) {
            node.middleback = new Node(middleback);
            node.middleback.step = node.step - block[middleback.y][middleback.x];
            node.middleback.type = Node.LEFT;
            node.middleback.parent = node;
            if (node.middleback.step > 0) {
                enable[middleback.y][middleback.x] = true;
                setNodes(node.middleback,middleback.x,middleback.y);
                Order(node.middleback);
            } else if (node.middleback.step == 0) {
                enable[middleback.y][middleback.x] = true;
                setNodes(node.middleback,middleback.x,middleback.y);
            }
        }
    }


    private void setNodes(Node node,int x,int y){
        if(nodes[y][x]==null){
            nodes[y][x]=node;
        }else {
            if(node.step>nodes[y][x].step){
                nodes[y][x]=node;
            }
        }
    }
}
