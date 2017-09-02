package com.ispring.robotwars.game.map;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/2.
 */

public abstract class BaseMap {

    final static String TAG= "BaseMap";

    public BaseMap() {
        int[][] indexs=getIndexs();
        block=new int[indexs.length][indexs[0].length];
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

    public int[][] block;
    public boolean[][] scope;
    public Node[][] nodes;

    public abstract int[][] getIndexs();

    private long lastTime;

    public boolean[][] getEnableMap(int x, int y,int step) {
        lastTime=System.currentTimeMillis();
        boolean[][] enable=new boolean[block.length][block[0].length];
        scope=new boolean[block.length][block[0].length];
        int[] inner;
        int index = 0;

        int mini,maxi,minj,maxj;
        mini=y-step;
        maxi=y+step+1;
        minj=x-step;
        maxj=x+step+1;
        end=new Point(x,y);
        getScope(x,y,step);

        for (int i = mini; i < maxi; i++) {
            if(i<0||i>block.length-1){
                continue;
            }
            inner = block[i];
            if(i<=y) {
                minj = x - (i-mini);
                maxj = x + (i-mini)+1;
            }else {
                minj = x-(maxi-i)+1;
                maxj = x+(maxi-i);
            }
            if(minj<0){
                minj=0;
            }
            if(maxj>inner.length){
                maxj=inner.length;
            }
            for (int j = minj; j < maxj; j++) {
                minStep=Integer.MAX_VALUE;
                minNode=null;
//                if(i==mini&&j==minj) {
                OrderFast(new Node(new Point(j, i)), step);

//                }
                enable[i][j]=minStep<=step;
                nodes[i][j]=minNode;
                Log.d(TAG, "minStep="+minStep);
                if(minNode!=null) {
                    Log.d(TAG, "minNode=" + minNode.toString());
                }
//                enable[i][j]=true;

            }
        }
        Log.d(TAG, "timeCost: "+(System.currentTimeMillis()-lastTime));
        Log.d(TAG, "count: "+count);
        return enable;
    }

    private void getScope(int x, int y,int step){
        int[] inner;
        int index = 0;

        int mini,maxi,minj,maxj;
        mini=y-step;
        maxi=y+step+1;
        minj=x-step;
        maxj=x+step+1;

        for (int i = mini; i < maxi; i++) {
            if(i<0||i>block.length-1){
                continue;
            }
            inner = block[i];
            if(i<=y) {
                minj = x - (i-mini);
                maxj = x + (i-mini)+1;
            }else {
                minj = x-(maxi-i)+1;
                maxj = x+(maxi-i);
            }
            if(minj<0){
                minj=0;
            }
            if(maxj>inner.length){
                maxj=inner.length;
            }
            for (int j = minj; j < maxj; j++) {
                scope[i][j]=true;
            }
        }
    }

    public Point end;

    public int count;

    public Node minNode;
    public int minStep;


    private StepInfo hasSameNode(Node node,int step){
        StepInfo info=new StepInfo();
        Node temp=node;
        int stepCount=0;
        info=countStep(info,temp);
        while (temp.parent!=null){
            temp=temp.parent;
            if(temp.equals(node)){
                info.hasSame=true;
                break;
            }
            stepCount++;
            if(stepCount>step){
                info.hasSame=true;
                break;
            }
            info=countStep(info,temp);
        }
        return info;
    }

    private StepInfo countStep(StepInfo info,Node node){
        if(node.type==Node.LEFT){
            info.leftStep++;
        }else if(node.type==Node.RIGHT){
            info.rightStep++;
        }else if(node.type==Node.MIDDLE){
            info.middleStep++;
        }else if(node.type==Node.MIDDLEBACK){
            info.middleBackStep++;
        }
        return info;
    }


    /** mode点所在象限，0-第四象限，1-第一，2-第二，3-第三*/
    private void Order(Node node,int step) {
        StepInfo info=hasSameNode(node,step);
        if((node.value.y<scope.length&&node.value.x<scope[0].length&&!scope[node.value.y][node.value.x])
                ||info.hasSame
                ){
            count++;
            return;
        }

        int mode = 0;
        if (node.value.x < end.x && node.value.y < end.y) {
            mode = 0;
        } else if (node.value.x > end.x && node.value.y < end.y) {
            mode = 1;
        } else if (node.value.x > end.x && node.value.y >= end.y) {
            mode = 2;
        } else if (node.value.x <= end.x && node.value.y > end.y) {
            mode = 3;
        }
        if (node.value.x == end.x && node.value.y == end.y) {
            Node iterNode = node;
            int sum = 0;
            while (iterNode.parent != null) {
                sum += block[iterNode.parent.value.y][iterNode.parent.value.x];
                iterNode = iterNode.parent;
            }
            Log.d(TAG, "node=" + node.toString());
            if (sum < minStep) {
                minStep = sum;
                minNode = node;
            }
            count++;
            return;
        }

        Point leftPoint;
        switch (mode) {
            case 1:
                leftPoint = new Point(node.value.x, node.value.y + 1);
                break;
            case 2:
                leftPoint = new Point(node.value.x, node.value.y + 1);
                break;
            case 3:
                leftPoint = new Point(node.value.x, node.value.y - 1);
                break;
            default:
                leftPoint = new Point(node.value.x, node.value.y - 1);
                break;
        }
        node.left = new Node(leftPoint);
        node.left.type = Node.LEFT;
        node.left.parent = node;
        switch (mode) {
            case 1:
//                if (node.value.y < end.y) {
                if(node.value.y<end.y+step) {
                    Order(node.left, step);
                }
//                }
                break;
            case 2:
//                if (node.value.y > end.y) {
                if(node.value.y<end.y+step)


                    Order(node.left,step);
//                }
                break;
            case 3:
//                if (node.value.y > end.y) {
                if(node.value.y>end.y-step)


                    Order(node.left,step);
//                }
                break;
            default:
//                if (node.value.y < end.y) {
                if(node.value.y>end.y-step)


                    Order(node.left,step);
//                }
                break;
        }




        Point rightPoint;
        switch (mode) {
            case 1:
                rightPoint = new Point(node.value.x , node.value.y-1);
                break;
            case 2:
                rightPoint = new Point(node.value.x , node.value.y-1);
                break;
            case 3:
                rightPoint = new Point(node.value.x, node.value.y + 1);
                break;
            default:
                rightPoint = new Point(node.value.x, node.value.y + 1);
                break;
        }
        node.right = new Node(rightPoint);
        node.right.type = Node.RIGHT;
        node.right.parent = node;
        switch (mode) {
            case 1:
//                if (node.value.x > end.x) {
                if(node.value.y>end.y-step)


                    Order(node.right,step);
//                }
                break;
            case 2:
//                if (node.value.x > end.x) {
                if(node.value.y>end.y-step)


                    Order(node.right,step);
//                }
                break;
            case 3:
//                if (node.value.x < end.x) {
                if(node.value.y<end.y+step)


                    Order(node.right,step);
//                }
                break;
            default:
//                if (node.value.y < end.y) {
                if(node.value.y<end.y+step)


                    Order(node.right,step);
//                }
                break;
        }


        Point middlePoint;
        switch (mode) {
            case 1:
                middlePoint = new Point(node.value.x - 1, node.value.y);
                break;
            case 2:
                middlePoint = new Point(node.value.x - 1, node.value.y);
                break;
            case 3:
                middlePoint = new Point(node.value.x + 1, node.value.y);
                break;
            default:
                middlePoint = new Point(node.value.x + 1, node.value.y);
                break;
        }
        node.middle = new Node(middlePoint);
        node.middle.type = Node.MIDDLE;
        node.middle.parent = node;
        switch (mode) {
            case 1:
//                if (node.value.x > end.x) {
                if(node.value.x>end.x-step/3f)

                    Order(node.middle,step);
//                }
                break;
            case 2:
//                if (node.value.x > end.x) {
                if(node.value.x>end.x-step/3f)


                    Order(node.middle,step);
//                }
                break;
            case 3:
//                if (node.value.x < end.x) {
                if(node.value.x<end.x+3f)


                    Order(node.middle,step);
//                }
                break;
            default:
//                if (node.value.x < end.x) {
                if(node.value.x<end.x+step/3f)


                    Order(node.middle,step);
//                }
                break;
        }


        Point backPoint;
        switch (mode) {
            case 1:
                backPoint = new Point(node.value.x + 1, node.value.y);
                break;
            case 2:
                backPoint = new Point(node.value.x + 1, node.value.y);
                break;
            case 3:
                backPoint = new Point(node.value.x - 1, node.value.y);
                break;
            default:
                backPoint = new Point(node.value.x - 1, node.value.y);
                break;
        }
        node.middleback = new Node(backPoint);
        node.middleback.type = Node.MIDDLEBACK;
        node.middleback.parent = node;
        switch (mode) {
            case 1:
//                if (node.value.x > end.x) {
                if(node.value.x<end.x+step/3f)


                    Order(node.middleback,step);
//                }
                break;
            case 2:
//                if (node.value.x > end.x) {
                if(node.value.x<end.x+step/3f)



                    Order(node.middleback,step);
//                }
                break;
            case 3:
//                if (node.value.x < end.x) {
                if(node.value.x>end.x-step/3f)


                    Order(node.middleback,step);
//                }
                break;
            default:
//                if (node.value.x < end.x) {
                if(node.value.x>end.x-step/3f)


                    Order(node.middleback,step);
//                }
                break;
        }
    } /** mode点所在象限，0-第四象限，1-第一，2-第二，3-第三*/
    private void OrderFast(Node node,int step) {
        if(minNode!=null){
            return;
        }
        count++;
        int mode = 0;
        if (node.value.x < end.x && node.value.y < end.y) {
            mode = 0;
        } else if (node.value.x > end.x && node.value.y < end.y) {
            mode = 1;
        } else if (node.value.x > end.x && node.value.y >= end.y) {
            mode = 2;
        } else if (node.value.x <= end.x && node.value.y > end.y) {
            mode = 3;
        }
        if (node.value.x == end.x && node.value.y == end.y) {
            Node iterNode = node;
            int sum = 0;
            while (iterNode.parent != null) {
                sum += block[iterNode.parent.value.y][iterNode.parent.value.x];
                iterNode = iterNode.parent;
            }
            if (sum < minStep) {
                minStep = sum;
                minNode = node;
//                Log.d(TAG, "node=" + node.toString());
            }
            return;
        }

        Point leftPoint;
        switch (mode) {
            case 1:
                leftPoint = new Point(node.value.x , node.value.y+1);
                break;
            case 2:
                leftPoint = new Point(node.value.x, node.value.y-1);
                break;
            case 3:
                leftPoint = new Point(node.value.x, node.value.y-1);
                break;
            default:
                leftPoint = new Point(node.value.x , node.value.y+1);
                break;
        }
        node.left = new Node(leftPoint);
        node.left.parent = node;
        switch (mode) {
            case 1:
                if(node.value.y<end.y)
                    OrderFast(node.left,step);
                break;
            case 2:
                if(node.value.y>end.y)
                    OrderFast(node.left,step);
                break;
            case 3:
                if(node.value.y>end.y)
                    OrderFast(node.left,step);
                break;
            default:
                if(node.value.y<end.y)
                    OrderFast(node.left,step);
                break;
        }




        Point rightPoint;
        switch (mode) {
            case 1:
                rightPoint = new Point(node.value.x-1 , node.value.y);
                break;
            case 2:
                rightPoint = new Point(node.value.x-1 , node.value.y);
                break;
            case 3:
                rightPoint = new Point(node.value.x+1, node.value.y);
                break;
            default:
                rightPoint = new Point(node.value.x+1, node.value.y);
                break;
        }
        node.right = new Node(rightPoint);
        node.right.parent = node;
        switch (mode) {
            case 1:
                if(node.value.x>end.x)
                    OrderFast(node.right,step);
                break;
            case 2:
                if(node.value.x>end.x)
                    OrderFast(node.right,step);
                break;
            case 3:
                if(node.value.x<end.x)
                    OrderFast(node.right,step);
                break;
            default:
                if(node.value.x<end.x)
                    OrderFast(node.right,step);
                break;
        }
    }

}
