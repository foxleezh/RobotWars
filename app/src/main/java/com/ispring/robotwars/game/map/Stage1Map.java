package com.ispring.robotwars.game.map;

/**
 * Created by Administrator on 2017/9/2.
 */

public class Stage1Map extends BaseMap{


    @Override
    public int[][] getIndexs() {
        return new int[][]{
                {1,3,3,1,1,3,3,3,3,1,1,1,1,1,1,1},
                {1,3,3,3,1,3,3,3,3,3,3,3,3,1,1,1},
                {1,3,3,3,3,3,3,3,3,3,3,3,3,1,1,1},
                {1,1,3,3,3,3,3,3,3,3,3,3,4,1,1,1},
                {1,3,3,3,3,3,3,3,3,3,3,3,4,4,1,1},
                {3,3,3,3,3,3,3,3,3,3,3,3,3,4,1,1},
                {3,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4},
                {3,3,3,3,3,3,3,3,3,3,3,3,4,4,4,4},
                {3,3,3,3,3,3,1,1,1,1,1,3,3,1,1,4},
                {3,3,3,3,3,3,3,1,1,1,1,1,3,3,1,4},
                {1,3,3,3,3,3,3,3,1,1,1,1,1,1,1,1},
                {1,1,3,3,3,3,3,3,3,3,3,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,3,3,3,1,1,1,1},
                {2,1,1,1,1,1,1,1,1,3,3,3,3,1,1,3},
                {2,2,1,1,1,1,1,1,1,3,3,3,3,1,3,3},
                {2,2,2,4,3,3,3,3,3,3,3,3,3,3,3,3},
                {2,2,2,4,4,3,3,3,3,3,3,3,3,3,3,3},
                {2,2,2,2,4,3,3,3,3,3,3,3,3,3,3,3},
                {2,2,2,2,4,4,4,3,3,3,3,3,3,3,3,3},
                {2,2,2,2,2,2,4,3,3,3,3,3,3,3,3,3},

        };
    }



}
