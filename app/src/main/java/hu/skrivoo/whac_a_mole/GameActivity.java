package hu.skrivoo.whac_a_mole;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
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
    private List<Mole> moles;
    private CountDownTimer cTimer = null;
    private Integer currentScoreNumber = 0;
    private FirebaseUser firebaseUser = null;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private SharedPreferences sharedPreferences;

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
        moles = initMoles();
        startGame();
    }

    private void startGame() {
        counter(60000, 1000);
        setHighestScoreValue();
        setCurrentScoreValue();
    }

    @SuppressLint("ClickableViewAccessibility")
    private List<Mole> initMoles() {
        List <Mole> out = new LinkedList<>();
        out.add(new Mole(findViewById(R.id.mole_1)));
        out.add(new Mole(findViewById(R.id.mole_2)));
        out.add(new Mole(findViewById(R.id.mole_3)));
        out.add(new Mole(findViewById(R.id.mole_4)));
        out.add(new Mole(findViewById(R.id.mole_5)));
        return out;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showUpMoles() {
        Random r = new Random();
        //Mole actMole = moles.get(r.nextInt(moles.size()));
        Mole actMole = moles.stream().filter(mole -> !mole.isActive()).findAny().get();
        //moles.remove(actMole);
        showUpMole(actMole);
        moleTimeCounter(3000, 100, actMole);
    }

    private void showUpMole(Mole mole) {
        YoYo.with(Techniques.SlideInUp)
                .duration(200)
                .onStart(animator -> mole.getMoleView().setVisibility(View.VISIBLE))
                .playOn(mole.getMoleView());
        mole.setActive(true);
    }

    public void hitAMole(View view) {
        Mole mole = null;
        for (Mole amole : moles) {
            if (amole.getMoleView().getId() == view.getId()) {
                mole = amole;
                break;
            }
        }
        if (mole.isActive()) {
            mole.setActive(false);
            currentScoreNumber++;
            setCurrentScoreValue();
            moleGoesAway(mole);
        }
    }

    private void moleGoesAway(Mole mole) {
        YoYo.with(Techniques.FadeOutDown)
                .duration(200)
                .onEnd(
                        animator -> mole.getMoleView().setVisibility(View.GONE)
                )
                .playOn(mole.getMoleView());
        mole.setActive(false);
        //moles.add(mole);
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

    private void setCurrentScoreValue() {
        if (currentScore.getText().equals("currentScore")) {
            currentScore.setText(String.valueOf(0));
        } else {
            currentScore.setText(String.valueOf(currentScoreNumber));
        }
    }

    private void moleTimeCounter(int total, int interval, Mole mole) {
        cTimer = new CountDownTimer(total, interval) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                moleGoesAway(mole);
            }
        }.start();
    }

    private void counter(int total, int interval) {
        cTimer = new CountDownTimer(total, interval) {
            @RequiresApi(api = Build.VERSION_CODES.N)
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