package com.ysk423.msd;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;



public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private TextView debuginfo;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;
    private ImageView black2;

    // サイズ
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    // 位置
    private float boxY;
    private float orangeX;
    private float orangeY;
    private float pinkX;
    private float pinkY;
    private float blackX;
    private float blackY;
    private float black2X;
    private float black2Y;

    // スピード
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;
    private int black2Speed;

    // Score
    private int score = 0;

    //HighScoreクライテリア_開発中はデバッグ効率の為10に設定、本来は100？
    private int highScoreCriteria = 10;

    //Handler&Time
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    // Status
    private boolean action_flg = false;
    private boolean start_flg = false;

    // Sound
    private SoundPlayer soundPlayer;

    //GameLevel
    private int gameLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        debuginfo = findViewById(R.id.debuginfo);//debug要のレベル(HO Mode表示）
        box = findViewById(R.id.box); //これが女性アイコン
        orange = findViewById(R.id.orange);//beerアイコン
        pink = findViewById(R.id.pink);//ワインアイコン
        black = findViewById(R.id.black);//ウイスキーアイコン
        black2 = findViewById(R.id.black2);//ウイスキーアイコン

        // Screen Size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //アイテムの移動初期値
        boxSpeed = Math.round(screenHeight / 120f);
        orangeSpeed = Math.round(screenWidth / 90f);
        pinkSpeed = Math.round(screenWidth / 90f);
        blackSpeed = Math.round(screenWidth / 90f);
        black2Speed = Math.round(screenWidth / 75f);

        orange.setX(-800.0f);
        orange.setY(-800.0f);
        pink.setX(-800.0f);
        pink.setY(-800.0f);
        black.setX(-800.0f);
        black.setY(-800.0f);
        black2.setX(-800.0f);
        black2.setY(-800.0f);
        scoreLabel.setText("Score : 0");


        gameLevel = getIntent().getIntExtra("GAME_LEVEL", 0);

        //レベル受けIntentのデバック用
        //リリース時はコメントアウトすること
        debuginfo.setText(String.valueOf(gameLevel));
    }

    public void changePos() {

        hitCheck();

        //orange
        orangeX -= orangeSpeed + score/10;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = (float)Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        if (score == 0){
            blackX -= blackSpeed * gameLevel;
        }else{
            blackX -= blackSpeed * gameLevel + score/10;
        }
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = (float)Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        // Black2, Balck2はスコアが50を超えると出現
        if (score < 50){
            black2X = -80.0f;
        }else{
            black2X -= black2Speed * gameLevel + score/7.5;
        }
        if (black2X < 0) {
            black2X = screenWidth + 50;
            black2Y = (float)Math.floor(Math.random() * (frameHeight - black2.getHeight()));
        }
        black2.setX(black2X);
        black2.setY(black2Y);

        // Pink
        pinkX -= pinkSpeed + score/5;
        if (pinkX < 0) {
            pinkX = screenWidth + 5000;
            pinkY = (float)Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //box
        if (action_flg) {
            boxY -= boxSpeed;
        } else {
            boxY += boxSpeed;
        }
        if (boxY < 0) boxY = 0;
        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);

        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck() {

        // Orange
        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if (hitStatus(orangeCenterX, orangeCenterY)) {
            orangeX = -10.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        // Pink
        float pinkCenterX = pinkX + pink.getWidth() / 2;
        float pinkCenterY = pinkY + pink.getHeight() / 2;

        if (hitStatus(pinkCenterX, pinkCenterY)) {
            pinkX = -10.0f;
            score += 30;
            soundPlayer.playHitSound();
        }

        // Black
        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if (hitStatus(blackCenterX, blackCenterY)) {

            soundPlayer.playOverSound();

            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            // 結果画面へ
            goResult();
        }

        //Black2
        float black2CenterX = black2X + black2.getWidth() / 2;
        float black2CenterY = black2Y + black2.getHeight() / 2;

        if (hitStatus(black2CenterX, black2CenterY)) {

            soundPlayer.playOverSound();

            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            // 結果画面へ
            goResult();
        }
    }

    public void goResult() {
        if(score < highScoreCriteria) {
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), ResultHighActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    public boolean hitStatus(float centerX, float centerY) {
        return (0 <= centerX && centerX <= boxSize &&
                boxY <= centerY && centerY <= boxY + boxSize) ? true : false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg == false) {
            start_flg = true;
            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            boxY = box.getY();
            boxSize = box.getHeight();
            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    //戻るボタンの無効化
    @Override
    public void onBackPressed() { }

 }