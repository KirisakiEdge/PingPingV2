package com.example.pingpingv2;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.content.Context;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;


public class GameActivity extends Activity {
    //for high score
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "heha";
    String intData = "huha";
    int defaultInt = 0;
    int hiScore;

    double ballSpeed = 0;
    //declaring canvas
    Canvas canvas;
    Canvas canvasScore;
    SquashCourtView squashCourtView;



    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    int racketWidth;
    int racketHeight;
    Point racketPosition;


    int enemyWidth;
    int enemyHeight;
    Point enemyPosition;

    Point ballPosition;
    int ballWidth;
    boolean left;
    boolean right;
    boolean up;
    boolean down;
    boolean r_left;
    boolean r_right;

    long lastFrameTime;
    int fps;
    int score;
    int lives;
    double displayParts;
    double pauseZoneH;
    double pauseZoneW;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        squashCourtView = new SquashCourtView(this);
        setContentView(squashCourtView);
        prefs = getSharedPreferences(dataName,MODE_PRIVATE);
        editor = prefs.edit();
        hiScore = prefs.getInt("myInt",defaultInt);





        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        //racket position
        racketHeight = 10;
        racketPosition = new Point();
        racketPosition.x = screenWidth / 2;
        racketPosition.y = screenHeight - 20;
        racketWidth = screenWidth / 9;

        //enemy characters
        enemyHeight = screenWidth / 40;
        enemyPosition = new Point();
        enemyPosition.x = screenWidth;
        enemyPosition.y = screenHeight + 950;
        enemyWidth = screenWidth / 40;


        //ball characterstics
        ballWidth = screenWidth / 45;
        ballPosition = new Point();
        ballPosition.x = screenWidth / 2;
        ballPosition.y = 1 + ballWidth;
        lives = 3;
    }

    class SquashCourtView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;
        Paint paint;
        Paint paintScore;
        public SquashCourtView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            paintScore = new Paint();
            down = true;

            Random random = new Random();
            int ballDirection = random.nextInt(2);
            switch (ballDirection) {
                case 0:
                    left = true;
                    right = false;
                    break;
                case 1:
                    right = true;
                    left = false;
                    break;
            }
        }

        @Override
        public void run() {
            while (playing) {
                updateCourt();
                drawCourt();
                controlFPS ();
            }
        }


        public void updateCourt() {
            if (r_right) {
                if (racketPosition.x + racketWidth/2 <= screenWidth ) {
                    racketPosition.x += screenWidth/30;
                }
            }
            if (r_left) {
                if (racketPosition.x - racketWidth/2 >= 0) {
                    racketPosition.x -= screenWidth/30;
                }
            }
            if (ballPosition.x + ballWidth/2 > screenWidth) {
                left = true;
                right = false;
            }
            if (ballPosition.x - ballWidth/2 < 0) {
                left = false;
                right = true;
            }
            if (ballPosition.y > screenHeight - ballWidth) {
                lives = lives - 1;
                if (lives == 0) {
                    GameOver();
                }
                ballPosition.y = 1 + ballWidth;
                Random randnum = new Random();
                int startX = randnum.nextInt(screenWidth) + 1;
                ballPosition.x = startX;

                int direction = randnum.nextInt(2);
                switch (direction) {
                    case 0:
                        right = false;
                        left = true;
                        break;
                    case 1:
                        right = true;
                        left = false;
                        break;
                }
            }
            //we hit top of the screen
            if(ballPosition.y <= 0)
            {
                down = true;
                up = false;
                ballPosition.y = 1;
            }
            if(up){
                ballPosition.y -= ballSpeed + 10;
            }
            if(down){
                ballPosition.y +=8 + ballSpeed;
            }
            if(left){
                ballPosition.x -= 12 + ballSpeed;
            }
            if(right){
                ballPosition.x += 12 + ballSpeed;
            }
            //to check weather ball has hit racket
            if(ballPosition.y + ballWidth > racketPosition.y + racketHeight/2){
                if((ballPosition.x + ballWidth > racketPosition.x - racketWidth/2)
                        &&(ballPosition.x - ballWidth < racketPosition.x + racketWidth/2)) {
                    up = true;
                    down = false;
                    score++;
                    if (ballPosition.x > racketPosition.x + racketWidth/5){
                        right = true;
                        left = false;
                    }
                    if(ballPosition.x < racketPosition.x - racketWidth/5) {
                        right = false;
                        left = true;
                    }

                    if (ballSpeed != 10) {
                        ballSpeed = ballSpeed + 0.5;
                    }

                }
            }
        }
        public void drawCourt(){
            if(ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                canvas.drawText("Lives "+lives,30,70,paint);
                canvas.drawText("Pause", 900, 70, paint);
                //canvas.drawText("fps " + fps, 250, 70, paint);

                paintScore.setColor(Color.GRAY);
                paintScore.setTextSize(250);
                canvas.drawText("Score",65,1000,paintScore);
                canvas.drawText(String.valueOf(score),765,1000,paintScore);


                // canvas.drawRect(100, 600, 150,650,paint);

                canvas.drawRect(racketPosition.x - (racketWidth / 2),
                        racketPosition.y - (racketHeight / 6), racketPosition.x + (racketWidth / 2),
                        racketPosition.y + racketHeight, paint);///drawing bat
                canvas.drawCircle(ballPosition.x, ballPosition.y, ballWidth, paint);//drawing ball

                ourHolder.unlockCanvasAndPost(canvas);

            }
        }
        public void controlFPS(){
            long timeThisFrame = (System.currentTimeMillis()-lastFrameTime);
            long timeToSleep = 15-timeThisFrame;
            if(timeThisFrame > 0){
                fps = (int)(1000/timeThisFrame);
            }
            if(timeToSleep>0){
                try{
                    ourThread.sleep(timeToSleep);
                }catch(InterruptedException e){
                }
            }
            lastFrameTime = System.currentTimeMillis();
        }
        public void pause(){
            playing = false;
            try{
                ourThread.join();
            }catch(InterruptedException e){
            }
        }
        public void resume(){
            playing = true;
            ourThread = new Thread(this);
            ourThread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        displayParts = screenHeight / 1.35;
        pauseZoneH = screenHeight / 8;
        pauseZoneW = screenWidth;
        if ((racketPosition.x <= screenWidth - racketWidth / 2) || (racketPosition.x >= racketWidth / 2)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if(motionEvent.getY() < displayParts) {
                        racketPosition.x = (int) motionEvent.getX();
                    }
                case MotionEvent.ACTION_DOWN:
                    if((motionEvent.getX() >= screenWidth - racketWidth * 2) && (motionEvent.getY() < screenHeight / 12)){
                        Intent in = new Intent(this, PauseActivity.class);
                        startActivity(in);
                        onPause();
                    }
                    if (motionEvent.getY() > displayParts) {
                        if (motionEvent.getX() >= screenWidth / 2) {
                            r_left = false;
                            r_right = true;

                        } else {
                            r_left = true;
                            r_right = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    r_right = false;
                    r_left = false;
                    break;
            }
        }

        return true;
    }

    public void GameOver(){
        Intent i = new Intent(this, GameOverActivity.class);
        i.putExtra("endScore", (int)score);
        startActivity(i);
    }


    @Override
    protected void onStop(){
        super.onStop();
        while(true){
            squashCourtView.pause();
            break;
        }
        finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        squashCourtView.pause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        squashCourtView.resume();
    }
    public boolean onKeyDown(int Keycode,KeyEvent event) {
        if (Keycode == KeyEvent.KEYCODE_BACK) {
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    }

    public void onBackPressed() {

    }
}





