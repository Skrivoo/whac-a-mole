package hu.skrivoo.whac_a_mole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameActivity extends AppCompatActivity {

    TextView timeCounter;
    TextView topScore;
    TextView currentScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        timeCounter = findViewById(R.id.timeCounter);
        topScore = findViewById(R.id.topScore);
        currentScore = findViewById(R.id.currentScore);

        startGame();

    }

    private void startGame() {
        try {
            wait(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}