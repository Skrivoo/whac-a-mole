package hu.skrivoo.whac_a_mole;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.common.collect.RangeMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameActivity.class.getName();
    private static final String PREFS_NAME = "MOLE_GAME_PREFS";
    private TextView timeCounter;
    private TextView topScore;
    private TextView currentScore;
    private ImageView mole1;
    private ImageView mole2;
    private ImageView mole3;
    private ImageView mole4;
    private ImageView mole5;
    private List<ImageView> moles;
    private CountDownTimer cTimer = null;
    private Integer currentScoreNumber = 0;
    private FirebaseUser firebaseUser = null;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private SharedPreferences sharedPreferences;
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener changeColorListener = (v, event) -> {
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        int color = bmp.getPixel((int) event.getX(), (int) event.getY());
        return color == Color.TRANSPARENT;
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        timeCounter = findViewById(R.id.timeCounter);
        topScore = findViewById(R.id.topScore);
        currentScore = findViewById(R.id.currentScore);
        startGame();
    }

    private void startGame() {
        initMoles();
        counter(60000, 1000);
        setHighestScoreValue();
        setCurrentScoreValue();
    }

    private void showUpMoles() {
        Random r = new Random();
        ImageView actMole = moles.get(r.nextInt(moles.size()));
        moles.remove(actMole);
        showUpMole(actMole);
        moleTimeCounter(3000, 100, actMole);
    }

    private void showUpMole(View view) {
        YoYo.with(Techniques.FadeInUp)
                .duration(200)
                .onStart(animator -> view.setVisibility(View.VISIBLE))
                .playOn(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMoles() {
        moles = new LinkedList<>();
        moles.add(mole1 = findViewById(R.id.mole_1));
        moles.add(mole2 = findViewById(R.id.mole_2));
        moles.add(mole3 = findViewById(R.id.mole_3));
        moles.add(mole4 = findViewById(R.id.mole_4));
        moles.add(mole5 = findViewById(R.id.mole_5));
        for (ImageView mole : moles) {
            mole.setDrawingCacheEnabled(true);
            mole.setOnTouchListener(changeColorListener);
            mole.setVisibility(View.GONE);
        }
    }

    private void setHighestScoreValue() {
        int temp = 0;
        if (firebaseUser != null) {
            //Ha a user be van jelentkezve a google fiókjába akkor itt kiszedjük a topscore-t
            //ha nincs topscore vagy 0 akkor return 0
            temp = 0;
        } else {
            //local tárolt adatokat nézünk, ha itt van topsopre akkor az, ha nincs akkor get default 0
            sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
            temp = sharedPreferences.getInt("topscore", 0);
        }
        topScore.setText(String.valueOf(temp));
    }

    public void hitAMole(View view) {
        currentScoreNumber++;
        setCurrentScoreValue();
        moleGoesAway(view);
    }

    private void moleGoesAway(View view) {
        YoYo.with(Techniques.FadeOutDown)
                .duration(200)
                .onEnd(
                        animator -> view.setVisibility(View.GONE)
                )
                .playOn(view);
        moles.add((ImageView) view);
    }

    private void setCurrentScoreValue() {
        if (currentScore.getText().equals("currentScore")) {
            currentScore.setText(String.valueOf(0));
        } else {
            currentScore.setText(String.valueOf(currentScoreNumber));
        }
    }

    private void moleTimeCounter(int total, int interval, View view) {
        cTimer = new CountDownTimer(total, interval) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                moleGoesAway(view);
            }
        }.start();
    }

    private void counter(int total, int interval) {
        cTimer = new CountDownTimer(total, interval) {
            public void onTick(long millisUntilFinished) {
                timeCounter.setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
                if (moles.size() != 0) {
                    showUpMoles();
                }
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }

    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

}