package hu.skrivoo.whac_a_mole;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class EditLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button saveUpdate;
    private Button deleteScores;
    private EditText newName;
    private EditText newAvararUrl;
    private PlayerDAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editlogin);
        mAuth = FirebaseAuth.getInstance();
        dao = new PlayerDAO(this);
        saveUpdate = findViewById(R.id.saveDataButton);
        deleteScores = findViewById(R.id.resetScoresButton);
        newName = findViewById(R.id.newNameEdittext);
        newAvararUrl = findViewById(R.id.newAvatarEdittext);
        saveUpdate.setOnClickListener(this::saveUpdate);
        deleteScores.setOnClickListener(this::deleteScores);
    }

    private void saveUpdate(View view) {
        if (newName.getText().toString().equals("") && newAvararUrl.getText().toString().equals("")) {
            Toast.makeText(this, "Egyik mező sincs kitöltve", Toast.LENGTH_LONG).show();
        } else {
            if (
                    newName.getText().toString().equals("-1") ||
                    newName.getText().toString().equals("0") ||
                    newAvararUrl.getText().toString().equals("-1") ||
                    newAvararUrl.getText().toString().equals("0")) {
                    Toast.makeText(this, "Invalid values", Toast.LENGTH_LONG).show();
                    return;
            }
            if (!newName.getText().toString().equals("")) {
                dao.update(Objects.requireNonNull(mAuth.getCurrentUser()), "name", newName.getText().toString());
            }
            if (!newAvararUrl.getText().toString().equals("")) {
                dao.update(Objects.requireNonNull(mAuth.getCurrentUser()), "avatar", newAvararUrl.getText().toString());
            }
            Toast.makeText(this, "Mentés sikeres", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteScores(View view) {
        dao.update(Objects.requireNonNull(mAuth.getCurrentUser()), "highestScore", String.valueOf(0));
        dao.update(Objects.requireNonNull(mAuth.getCurrentUser()), "allScore", String.valueOf(-1));
        Toast.makeText(this, "Eredmények törölve", Toast.LENGTH_LONG).show();
    }
}
