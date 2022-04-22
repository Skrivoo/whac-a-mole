package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity {

    private static final String LOG_TAG = GameActivity.class.getName();
    private static final String PREFS_NAME = "MOLE_GAME_PREFS";
    private TextView timeCounter;
    private TextView topScore;
    private TextView currentScore;
    private List<Mole> moles;
    private CountDownTimer cTimer = null;
    private Integer currentScoreNumber = 0;
    private SharedPreferences sharedPreferences;
    private Random r;
    private Vibrator vibe;
    private MediaPlayer mp;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<Integer> listOfAllScore;
    private Integer topScoreAtFireStore;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        timeCounter = findViewById(R.id.timeCounter);
        topScore = findViewById(R.id.topScore);
        currentScore = findViewById(R.id.currentScore);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.i(LOG_TAG, currentUser == null ? "anonim" : "bejelentkezve mint: " + currentUser.getDisplayName());
        db = FirebaseFirestore.getInstance();
        r = new Random();
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mp = MediaPlayer.create(this, R.raw.oof);
        moles = initMoles();
        startGame();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void endGame() {
        saveScore();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveScore() {
        if (mAuth.getCurrentUser() != null) { //be van lépve firebase-zel a felhasználó
            CollectionReference collection = db.collection(currentUser.getUid());
            Map<String, Object> user = new HashMap<>();
            listOfAllScore.add(Integer.parseInt(currentScore.getText().toString()));
            if (Integer.parseInt(currentScore.getText().toString()) > topScoreAtFireStore) {
                topScoreAtFireStore = Integer.parseInt(currentScore.getText().toString());
            }
            user.put("topScore", topScoreAtFireStore);
            user.put("allScore", listOfAllScore);
            collection.document("scores").set(user);
        } else {
            String topSaved = sharedPreferences.getString("topscore", "0");
            if (Integer.parseInt(currentScore.getText().toString()) > Integer.parseInt(topSaved)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("topscore", currentScoreNumber.toString());
                editor.apply();
            }
        }
        finish();
    }

    private void startGame() {
        counter(60000, 1000);
        setHighestScoreValue();
        setCurrentScoreValue();
    }

    @SuppressLint("ClickableViewAccessibility")
    private List<Mole> initMoles() {
        List<Mole> out = new LinkedList<>();
        out.add(new Mole(findViewById(R.id.mole_1)));
        out.add(new Mole(findViewById(R.id.mole_2)));
        out.add(new Mole(findViewById(R.id.mole_3)));
        out.add(new Mole(findViewById(R.id.mole_4)));
        out.add(new Mole(findViewById(R.id.mole_5)));
        return out;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showUpMoles() {
        List<Mole> actMoles = moles.stream().filter(mole -> !mole.isActive()).collect(Collectors.toList());
        Mole actualShowedUpMole = showUpMole(actMoles.get(r.nextInt(actMoles.size())));
        moleTimeCounter(r.ints(1500, 3000).findFirst().getAsInt(),
                r.ints(150, 300).findFirst().getAsInt(), actualShowedUpMole);
    }

    private Mole showUpMole(Mole mole) {
        YoYo.with(Techniques.SlideInUp)
                .duration(150)
                .onStart(animator -> mole.getMoleView().setVisibility(View.VISIBLE))
                .onCancel(animator -> {
                    moleGoesAway(mole);
                    currentScoreNumber++;
                })
                .onEnd(animator -> mole.setActive(true))
                .playOn(mole.getMoleView());
        return mole;
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
            vibe.vibrate(100);
            mp.start();
            currentScoreNumber++;
            setCurrentScoreValue();
            moleGoesAway(mole);

        }
    }

    private void moleGoesAway(Mole mole) {
        YoYo.with(Techniques.SlideOutDown)
                .duration(150)
                .onEnd(
                        animator -> {
                            mole.getMoleView().setVisibility(View.GONE);
                            mole.setActive(false);
                        }
                )
                .playOn(mole.getMoleView());
    }

    private void setHighestScoreValue() {
        if (mAuth.getCurrentUser() != null) { //be van lépve firebase-zel a felhasználó
            DocumentReference scores = db.collection(currentUser.getUid()).document("scores");
            scores
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                listOfAllScore = (List<Integer>) document.get("allScore");
                                topScoreAtFireStore = Integer.parseInt(document.get("topScore").toString());
                                Log.d(LOG_TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Map<String, Object> user = new HashMap<>();
                                listOfAllScore = new LinkedList<Integer>();
                                topScoreAtFireStore = 0;
                                user.put("topScore", topScoreAtFireStore);
                                user.put("allScore", listOfAllScore);
                                user.put("user", mAuth.getCurrentUser()); //hozzácsatolom a firebase usert a player entitáshoz
                                db.collection(currentUser.getUid()).document("scores").set(user);
                                Log.d(LOG_TAG, "No such document, making new one");
                            }
                        } else {
                            Log.d(LOG_TAG, "get failed with ", task.getException());
                        }
                        task.addOnCompleteListener(runnable -> {
                            topScore.setText(String.valueOf(topScoreAtFireStore));
                        });
                    });
        } else {
            String temp = "0";
            //local tárolt adatokat nézünk, ha itt van topsopre akkor az, ha nincs akkor get default 0
            temp = sharedPreferences.getString("topscore", "0");
            topScore.setText(String.valueOf(temp));
        }
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onFinish() {
                endGame();
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
        mp.release();
        super.onDestroy();
    }

}