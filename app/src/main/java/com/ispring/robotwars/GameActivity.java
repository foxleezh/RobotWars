package com.ispring.robotwars;

import android.app.Activity;
import android.os.Bundle;

import com.ispring.robotwars.game.GameView;


public class GameActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView = (GameView)findViewById(R.id.gameView);
        //0:combatAircraft
        //1:explosion
        //2:yellowBullet
        //3:blueBullet
        //4:smallEnemyPlane
        //5:middleEnemyPlane
        //6:bigEnemyPlane
        //7:bombAward
        //8:bulletAward
        //9:pause1
        //10:pause2
        //11:bomb
        int[] bitmapIds = {
                R.drawable.explosion,R.drawable.menu_move,R.drawable.menu_skill,R.drawable.menu_state,
                R.drawable.bg_0,R.drawable.bg_1,R.drawable.bg_2,R.drawable.bg_3,R.drawable.bg_4
        };
        int[] animIds = {
                R.drawable.robot
        };
        gameView.start(bitmapIds,animIds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gameView != null){
            gameView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gameView != null){
            gameView.destroy();
        }
        gameView = null;
    }
}