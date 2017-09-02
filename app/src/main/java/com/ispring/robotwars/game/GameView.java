package com.ispring.robotwars.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.ispring.robotwars.R;
import com.ispring.robotwars.game.map.BaseMap;
import com.ispring.robotwars.game.map.Node;
import com.ispring.robotwars.game.map.Stage1Map;
import com.ispring.robotwars.game.robot.Robot;
import com.ispring.robotwars.game.robot.enemy.EnemyRobot;
import com.ispring.robotwars.game.robot.friend.FriendRobot;
import com.ispring.robotwars.game.robot.friend.GangdaRobot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameView extends View {

    final static String TAG="GameView";
    private Paint paint;
    private Paint textPaint;



    private List<Sprite> sprites = new ArrayList<Sprite>();
    private List<Sprite> spritesNeedAdded = new ArrayList<Sprite>();
    //0:combatAircraft
    //1:explosion
    //2:yellowBullet
    //3:blueBullet
    //4:smallEnemyRobot
    //5:middleEnemyRobot
    //6:bigEnemyRobot
    //7:bombAward
    //8:bulletAward
    //9:pause1
    //10:pause2
    //11:bomb
    public SparseArray<Bitmap> bitmaps = new SparseArray<Bitmap>();
    public SparseArray<Integer> bgmaps = new SparseArray<Integer>();
    public SparseArray<Bitmap> animbitmaps = new SparseArray<Bitmap>();
    private float density = getResources().getDisplayMetrics().density;//屏幕密度
    public static final int STATUS_GAME_STARTED = 1;//游戏开始
    public static final int STATUS_GAME_PAUSED = 2;//游戏暂停
    public static final int STATUS_GAME_OVER = 3;//游戏结束
    public static final int STATUS_GAME_DESTROYED = 4;//游戏销毁
    private int status = STATUS_GAME_DESTROYED;//初始为销毁状态
    private long frame = 0;//总共绘制的帧数
    private long score = 0;//总得分
    private int level = 0;//总得分
    public static int cellWidth;
    private float fontSize = 12;//默认的字体大小，用于绘制左上角的文本
    private float fontSize2 = 20;//用于在Game Over的时候绘制Dialog中的文本
    private float borderSize = 10;//Game Over的Dialog的边框
    private Rect continueRect = new Rect();//"继续"、"重新开始"按钮的Rect

    //触摸事件相关的变量
    private static final int TOUCH_MOVE = 1;//移动
    private static final int TOUCH_SINGLE_CLICK = 2;//单击
    private static final int TOUCH_DOUBLE_CLICK = 3;//双击
    //一次单击事件由DOWN和UP两个事件合成，假设从down到up间隔小于200毫秒，我们就认为发生了一次单击事件
    private static final int singleClickDurationTime = 200;
    //一次双击事件由两个点击事件合成，两个单击事件之间小于300毫秒，我们就认为发生了一次双击事件
    private static final int doubleClickDurationTime = 300;
    private long lastSingleClickTime = -1;//上次发生单击的时刻
    private long touchDownTime = -1;//触点按下的时刻
    private long touchUpTime = -1;//触点弹起的时刻
    private float touchX = -1;//触点的x坐标
    private float touchY = -1;//触点的y坐标
    private float downX ;
    private float downY ;
    private float originX;
    private float originY;
    private float canvasX;
    private float canvasY;
    MediaPlayer player;
    BaseMap stage;
    ColorMatrixColorFilter colorMatrixFilter;
    boolean[][] enable;
    boolean shouldUpdateBg=true;

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GameView, defStyle, 0);
        a.recycle();
        //初始化paint
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        //设置textPaint，设aaa置为抗锯齿，且是粗体
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        textPaint.setColor(0xffeeeeee);
        fontSize = textPaint.getTextSize();
        fontSize *= density;
        fontSize2 *= density;
        textPaint.setTextSize(fontSize);
        borderSize *= density;
        cellWidth = (int) (44* density);
        player=new MediaPlayer();
        stage=new Stage1Map();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
    }

    public void start(int[] bitmapIds,int[] animIds){
        destroy();
        for(int bitmapId : bitmapIds){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);

            if(bitmapId==R.drawable.bg_0){
                bgmaps.put(0,bitmapId);
            }else if(bitmapId==R.drawable.bg_1){
                bgmaps.put(1,bitmapId);
            }else if(bitmapId==R.drawable.bg_2){
                bgmaps.put(2,bitmapId);
            }else if(bitmapId==R.drawable.bg_3){
                bgmaps.put(3,bitmapId);
            }else if(bitmapId==R.drawable.bg_4){
                bgmaps.put(4,bitmapId);
            }

            if(bitmapId==R.drawable.main_bg){
                Bitmap backBitmap = Util.createRepeater(2,bitmap);
                bitmaps.put(bitmapId,backBitmap);
            }else {
                bitmaps.put(bitmapId,bitmap);
            }
        }

        for(int bitmapId : animIds){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);
            animbitmaps.put(bitmapId,bitmap);
            if(bitmapId==R.drawable.robot){
                int robot_width=(int) (bitmap.getWidth()/12f);
                Bitmap gangda= Bitmap.createBitmap(bitmap, robot_width*0, robot_width*1, robot_width, robot_width);
                bitmaps.put(R.id.robot_gangda,gangda);
            }
        }
        startWhenBitmapsReady();
    }
    
    private void startWhenBitmapsReady(){
        Sprite gangda=new GangdaRobot(bitmaps.get(R.id.robot_gangda),7,15);
        sprites.add(gangda);

        //将游戏设置为开始状态
        status = STATUS_GAME_STARTED;
        postInvalidate();
    }
    
    private void restart(){
        destroyNotRecyleBitmaps();
        startWhenBitmapsReady();
        bonusScore=0;
    }

    public void pause(){
        //将游戏设置为暂停状态
        status = STATUS_GAME_PAUSED;
    }

    private void resume(){
        //将游戏设置为运行状态
        status = STATUS_GAME_STARTED;
        postInvalidate();
    }

    private long getScore(){
        //获取游戏得分
        return score;
    }

    /*-------------------------------draw-------------------------------------*/

    @Override
    protected void onDraw(Canvas canvas) {
        //我们在每一帧都检测是否满足延迟触发单击事件的条件
        if(isSingleClick()){
            onSingleClick(touchX, touchY);
        }

        canvas.restore();
        super.onDraw(canvas);
        if(status == STATUS_GAME_STARTED){
            drawGameStarted(canvas);
        }else if(status == STATUS_GAME_PAUSED){
            drawGamePaused(canvas);
        }else if(status == STATUS_GAME_OVER){
            drawGameOver(canvas);
        }
    }

    private void updateBg(Canvas canvas){
        shouldUpdateBg=false;
        boolean gray=enable!=null;
        int[][] indexs = stage.getIndexs();
        if(bg_map==null) {
            bg_map = Bitmap.createBitmap(cellWidth * indexs[0].length, cellWidth * indexs.length, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas1 = new Canvas(bg_map);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int[] inner;
        int index = 0;
        RectF dst = new RectF();
        Bitmap srcBitmap;
        for (int i = 0; i < indexs.length; i++) {
            inner = indexs[i];
            for (int j = 0; j < inner.length; j++) {
                index = inner[j];
                srcBitmap = bitmaps.get(bgmaps.get(index));
                dst.left = j * cellWidth;
                dst.top = i * cellWidth;
                dst.right = dst.left + cellWidth;
                dst.bottom = dst.top + cellWidth;
                Paint paint1=new Paint();
                if(gray) {
                    if(enable[i][j]){
                        paint1.setColorFilter(null);
                    }else {
                        paint1.setColorFilter(colorMatrixFilter);
                    }
                }
                canvas1.drawBitmap(srcBitmap, null, dst, paint1);
            }
        }
//        if(gray) {
//            paint.setColorFilter(colorMatrixFilter);
//        }else {
//            paint.setColorFilter(null);
//        }
    }

    //绘制运行状态的游戏
    private void drawGameStarted(Canvas canvas){

        canvas.translate(canvasX,canvasY);
        if (bg_map == null) {
            updateBg(canvas);
            canvasX = getWidth() - bg_map.getWidth();
            canvasY = getHeight() - bg_map.getHeight();
        }
        if(shouldUpdateBg){
            updateBg(canvas);
        }
        drawScoreAndBombs(canvas);

        //第一次绘制时，将战斗机移到Canvas最下方，在水平方向的中心
//        if(frame == 0){
//            float centerX = canvas.getWidth() / 2;
//            float centerY = canvas.getHeight() - combatAircraft.getHeight();
//            combatAircraft.centerTo(centerX, centerY);
//        }
//
        //将spritesNeedAdded添加到sprites中
        if(spritesNeedAdded.size() > 0){
            sprites.addAll(spritesNeedAdded);
            spritesNeedAdded.clear();
        }
//
//        //检查战斗机跑到子弹前面的情况
////        destroyBulletsFrontOfCombatAircraft();
//
//        //在绘制之前先移除掉已经被destroyed的Sprite
//        removeDestroyedSprites();
//
//        //每隔30帧随机添加Sprite
//        if(frame % 30 == 0){
//            createRandomSprites(canvas.getWidth());
//        }
//        frame++;
//
//        //遍历ours，绘制我方人员
//        Sprite gangda=new GangdaRobot(bitmaps.get(R.id.robot_gangda),9,12);
//        ours.add(gangda);

        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()){
            Sprite s = iterator.next();

            if(!s.isDestroyed()){
                //在Sprite的draw方法内有可能会调用destroy方法
                s.draw(canvas, paint, this);
            }

            //我们此处要判断Sprite在执行了draw方法后是否被destroy掉了
            if(s.isDestroyed()){
                //如果Sprite被销毁了，那么从Sprites中将其移除
                iterator.remove();
            }
        }
//
//        if(combatAircraft != null){
//            //最后绘制战斗机
//            combatAircraft.draw(canvas, paint, this);
//            if(combatAircraft.isDestroyed()){
//                //如果战斗机被击中销毁了，那么游戏结束
//                status = STATUS_GAME_OVER;
//            }
//            //通过调用postInvalidate()方法使得View持续渲染，实现动态效果
            postInvalidate();
//        }
    }

    private Bitmap bg_map;

    //绘制暂停状态的游戏
    private void drawGamePaused(Canvas canvas){
        drawScoreAndBombs(canvas);

        //调用Sprite的onDraw方法，而非draw方法，这样就能渲染静态的Sprite，而不让Sprite改变位置
        for(Sprite s : sprites){
            s.onDraw(canvas, paint, this);
        }

        //绘制Dialog，显示得分
        if(lastSingleClickTime > 0){
            postInvalidate();
        }
    }

    //绘制结束状态的游戏
    private void drawGameOver(Canvas canvas){
        //Game Over之后只绘制弹出窗显示最终得分
        if(lastSingleClickTime > 0){
            postInvalidate();
        }
    }


    //绘制左上角的得分和左下角炸弹的数量
    private void drawScoreAndBombs(Canvas canvas){
        //绘制左上角的暂停按钮
//        Bitmap main_bg = bitmaps.get(R.drawable.main_bg);
//        Rect backResRect = getBackBitmapResRecF();
//        canvas.drawBitmap(main_bg, backResRect,new RectF(0,0,canvas.getWidth(),canvas.getHeight()), paint);


        canvas.drawBitmap(bg_map, null, new RectF(0, 0, bg_map.getWidth(), bg_map.getHeight()), paint);


//        Bitmap pauseBitmap = bitmaps.get(R.drawable.pause);
//        RectF pauseBitmapDstRecF = getPauseBitmapDstRecF();
//        float pauseLeft = pauseBitmapDstRecF.left;
//        float pauseTop = pauseBitmapDstRecF.top;
//        canvas.drawBitmap(pauseBitmap, pauseLeft, pauseTop, paint);
//        //绘制左上角的总得分数
//        float scoreLeft = pauseLeft + pauseBitmap.getWidth() + 20 * density;
//        float scoreTop = fontSize + pauseTop + pauseBitmap.getHeight() / 2 - fontSize / 2;
//        canvas.drawText(score + "", scoreLeft, scoreTop, textPaint);
//        canvas.drawText(level + "", scoreLeft, scoreTop*2, textPaint);
//        canvas.drawText(getCombatAircraft().getLifeCount() + "", scoreLeft, scoreTop*3, textPaint);
//
//        //绘制左下角
//        if(combatAircraft != null && !combatAircraft.isDestroyed()){
//            int bombCount = combatAircraft.getBombCount();
//            if(bombCount > 0){
//                //绘制左下角的炸弹
//                Bitmap bombBitmap = bitmaps.get(R.drawable.bomb);
//                float bombTop = canvas.getHeight() - bombBitmap.getHeight();
//                canvas.drawBitmap(bombBitmap, 0, bombTop, paint);
//                //绘制左下角的炸弹数量
//                float bombCountLeft = bombBitmap.getWidth() + 10 * density;
//                float bombCountTop = fontSize + bombTop + bombBitmap.getHeight() / 2 - fontSize / 2;
//                canvas.drawText("X " + bombCount, bombCountLeft, bombCountTop, textPaint);
//            }
//        }
    }



    

    //移除掉已经destroyed的Sprite
    private void removeDestroyedSprites(){
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()){
            Sprite s = iterator.next();
            if(s.isDestroyed()){
                iterator.remove();
            }
        }
    }

    public long bonusScore;

    //生成随机的Sprite
    private void createRandomSprites(int canvasWidth){
        
    }

    /*-------------------------------touch------------------------------------*/

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //通过调用resolveTouchType方法，得到我们想要的事件类型
        //需要注意的是resolveTouchType方法不会返回TOUCH_SINGLE_CLICK类型
        //我们会在onDraw方法每次执行的时候，都会调用isSingleClick方法检测是否触发了单击事件
        int touchType = resolveTouchType(event);
        if(status == STATUS_GAME_STARTED){
            if(touchType == TOUCH_MOVE){
                canvasX=originX+(touchX-downX);
                canvasY=originY+(touchY-downY);

                if(canvasX>0){
                    canvasX=0;
                }
                if(canvasX<getWidth()-bg_map.getWidth()){
                    canvasX=getWidth()-bg_map.getWidth();
                }
                if(canvasY>0){
                    canvasY=0;
                }
                if(canvasY<getHeight()-bg_map.getHeight()){
                    canvasY=getHeight()-bg_map.getHeight();
                }
            }else if(touchType == TOUCH_DOUBLE_CLICK){
                if(status == STATUS_GAME_STARTED){
                    
                }
            }
        }else if(status == STATUS_GAME_PAUSED){
            if(lastSingleClickTime > 0){
                postInvalidate();
            }
        }else if(status == STATUS_GAME_OVER){
            if(lastSingleClickTime > 0){
                postInvalidate();
            }
        }
        return true;
    }

    //合成我们想要的事件类型
    private int resolveTouchType(MotionEvent event){
        int touchType = -1;
        int action = event.getAction();
        touchX = event.getX();
        touchY = event.getY();
        if(action == MotionEvent.ACTION_MOVE){
            long deltaTime = System.currentTimeMillis() - touchDownTime;
            if(deltaTime > singleClickDurationTime){
                //触点移动
                touchType = TOUCH_MOVE;
            }
        }else if(action == MotionEvent.ACTION_DOWN){
            //触点按下
            touchDownTime = System.currentTimeMillis();
            downX = touchX;
            downY = touchY;
            originX = canvasX;
            originY = canvasY;

        }else if(action == MotionEvent.ACTION_UP){
            //触点弹起
            touchUpTime = System.currentTimeMillis();
            //计算触点按下到触点弹起之间的时间差
            long downUpDurationTime = touchUpTime - touchDownTime;
            //如果此次触点按下和抬起之间的时间差小于一次单击事件指定的时间差，
            //那么我们就认为发生了一次单击
            if(downUpDurationTime <= singleClickDurationTime){
                //计算这次单击距离上次单击的时间差
                long twoClickDurationTime = touchUpTime - lastSingleClickTime;

                if(twoClickDurationTime <=  doubleClickDurationTime){
                    //如果两次单击的时间差小于一次双击事件执行的时间差，
                    //那么我们就认为发生了一次双击事件
                    touchType = TOUCH_DOUBLE_CLICK;
                    //重置变量
                    lastSingleClickTime = -1;
                    touchDownTime = -1;
                    touchUpTime = -1;
                }else{
                    //如果这次形成了单击事件，但是没有形成双击事件，那么我们暂不触发此次形成的单击事件
                    //我们应该在doubleClickDurationTime毫秒后看一下有没有再次形成第二个单击事件
                    //如果那时形成了第二个单击事件，那么我们就与此次的单击事件合成一次双击事件
                    //否则在doubleClickDurationTime毫秒后触发此次的单击事件
                    lastSingleClickTime = touchUpTime;
                }
            }
        }
        return touchType;
    }

    //在onDraw方法中调用该方法，在每一帧都检查是不是发生了单击事件
    private boolean isSingleClick(){
        boolean singleClick = false;
        //我们检查一下是不是上次的单击事件在经过了doubleClickDurationTime毫秒后满足触发单击事件的条件
        if(lastSingleClickTime > 0){
            //计算当前时刻距离上次发生单击事件的时间差
            long deltaTime = System.currentTimeMillis() - lastSingleClickTime;

            if(deltaTime >= doubleClickDurationTime){
                //如果时间差超过了一次双击事件所需要的时间差，
                //那么就在此刻延迟触发之前本该发生的单击事件
                singleClick = true;
                //重置变量
                lastSingleClickTime = -1;
                touchDownTime = -1;
                touchUpTime = -1;
            }
        }
        return singleClick;
    }

    private Robot moveRobot;

    private void onSingleClick(float x, float y){
        if(status == STATUS_GAME_STARTED){
            Point p=getTouchPoint(x,y);
            Sprite sprite=getTouchSprite(p);
            if(sprite!=null){
                if(sprite instanceof FriendRobot) {
                    if(hasMenu){
                        destroyMenu();
                        hasMenu=false;
                    }else {
                        int menuxIndex = p.x - 1;
                        int menuyIndex = p.y - 1;
                        if (menuxIndex < 0) {
                            menuxIndex = 0;
                        }
                        if (menuxIndex > stage.getIndexs()[0].length - 3) {
                            menuxIndex = stage.getIndexs()[0].length - 3;
                        }
                        if (menuyIndex < 1) {
                            menuyIndex = 1;
                        }
                        if (menuyIndex > stage.getIndexs().length - 2) {
                            menuyIndex = stage.getIndexs().length - 2;
                        }
                        MenuSprite move = new MenuSprite(bitmaps.get(R.drawable.menu_move), menuxIndex, menuyIndex);
                        move.action=MenuSprite.MOVE;
                        move.target=sprite;
                        addSprite(move);
                        MenuSprite skill = new MenuSprite(bitmaps.get(R.drawable.menu_skill), menuxIndex + 1, menuyIndex);
                        skill.action=MenuSprite.SKILL;
                        move.target=sprite;
                        addSprite(skill);
                        MenuSprite state = new MenuSprite(bitmaps.get(R.drawable.menu_state), menuxIndex + 2, menuyIndex);
                        state.action=MenuSprite.STATE;
                        move.target=sprite;
                        addSprite(state);
                        hasMenu = true;
                    }
                }if(sprite instanceof MenuSprite){
                    MenuSprite menuSprite = (MenuSprite) sprite;
                    if(menuSprite.target instanceof FriendRobot) {
                        FriendRobot robot= (FriendRobot) menuSprite.target;
                        if (menuSprite.action == MenuSprite.MOVE) {
                            enable=stage.getEnableMap(robot.xIndex,robot.yIndex,robot.step);
                            shouldUpdateBg=true;
                            moveRobot=robot;


                        } else if (menuSprite.action == MenuSprite.SKILL) {

                        } else if (menuSprite.action == MenuSprite.STATE) {

                        }
                    }
                    destroyMenu();
                    hasMenu=false;
                }
            }else if(hasMenu){
                destroyMenu();
                hasMenu=false;
            }else if(enable!=null){
                if(enable[p.y][p.x]){
                    Node node=stage.nodes[p.y][p.x];
                    Log.d(TAG, "move: "+node.toString());
                    moveRobot.setNode(node);
                    enable=null;
                    shouldUpdateBg=true;
                }
            }

        }else if(status == STATUS_GAME_PAUSED){
            if(isClickContinueButton(x, y)){
                //单击了“继续”按钮
                resume();
            }
        }else if(status == STATUS_GAME_OVER){
            if(isClickRestartButton(x, y)){
                //单击了“重新开始”按钮
                restart();
            }
        }
    }

    private boolean hasMenu;

    public Sprite getTouchSprite(Point p){

        Sprite s=null;
        for (Sprite sprite : sprites) {
            if(sprite instanceof CellSprite){
                CellSprite cellSprite= (CellSprite) sprite;
                if(p.x==cellSprite.xIndex&&p.y==cellSprite.yIndex){
                    s=sprite;
                    if(!hasMenu){
                        break;
                    }
                }
            }
        }
        return s;
    }


    public Point getTouchPoint(float x,float y){
        x+=-canvasX;
        y+=-canvasY;
        return new Point((int)x/cellWidth, (int) (y/cellWidth));
    }

    //是否单击了左上角的暂停按钮
    private boolean isClickPause(float x, float y){
        RectF pauseRecF = getPauseBitmapDstRecF();
        return pauseRecF.contains(x, y);
    }

    //是否单击了暂停状态下的“继续”那妞
    private boolean isClickContinueButton(float x, float y){
        return continueRect.contains((int)x, (int)y);
    }

    //是否单击了GAME OVER状态下的“重新开始”按钮
    private boolean isClickRestartButton(float x, float y){
        return continueRect.contains((int)x, (int)y);
    }

    private RectF getPauseBitmapDstRecF(){
        RectF recF = new RectF();
        return recF;
    }


    private Rect getBackBitmapResRecF(){

        Bitmap backBitmap = bitmaps.get(R.drawable.main_bg);
        Rect recF = new Rect();
        float rate=((float) getHeight())/getWidth();

        int rectHeight= (int) (backBitmap.getHeight()/2f);
        int rectWidth= (int) ((backBitmap.getHeight()/2)/rate);
        int speed=2;


        recF.left = (int) ((backBitmap.getWidth()-rectWidth)/2f);

        recF.top = (int) (backBitmap.getHeight()/2-(frame*speed)%(backBitmap.getHeight()/2));
        recF.right = recF.left+rectWidth;
        recF.bottom = recF.top + rectHeight;
        return recF;
    }

    /*-------------------------------destroy------------------------------------*/
    
    private void destroyNotRecyleBitmaps(){
        //将游戏设置为销毁状态
        status = STATUS_GAME_DESTROYED;

        //重置frame
        frame = 0;

        //重置得分
        score = 0;

        //销毁敌机、子弹、奖励、爆炸
        for(Sprite s : sprites){
            s.destroy();
        }
        sprites.clear();
    }

    public void destroy(){
        destroyNotRecyleBitmaps();

        int key=0;

        //释放Bitmap资源
        for (int i = 0; i < bitmaps.size(); i++) {
            key = bitmaps.keyAt(i);
            bitmaps.get(key).recycle();
        }
        bitmaps.clear();
    }

    /*-------------------------------public methods-----------------------------------*/

    //向Sprites中添加Sprite
    public void addSprite(Sprite sprite){
        spritesNeedAdded.add(sprite);
    }

    //添加得分
    public void addScore(int value){
        score += value;
    }

    public int getStatus(){
        return status;
    }

    public float getDensity(){
        return density;
    }


    public Bitmap getExplosionBitmap(){
        return bitmaps.get(R.drawable.explosion);
    }

    //获取处于活动状态的敌机
    public List<EnemyRobot> getAliveEnemyRobots(){
        List<EnemyRobot> EnemyRobots = new ArrayList<EnemyRobot>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof EnemyRobot){
                EnemyRobot sprite = (EnemyRobot)s;
                EnemyRobots.add(sprite);
            }
        }
        return EnemyRobots;
    }

    //获取处于活动状态的菜单
    public List<MenuSprite> getAliveMenu(){
        List<MenuSprite> menuSprites = new ArrayList<MenuSprite>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof MenuSprite){
                MenuSprite sprite = (MenuSprite)s;
                menuSprites.add(sprite);
            }
        }
        return menuSprites;
    }

    public void destroyMenu(){
        List<MenuSprite> aliveMenu = getAliveMenu();
        for (MenuSprite menuSprite : aliveMenu) {
            menuSprite.destroy();
        }
    }
}