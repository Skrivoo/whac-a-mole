package hu.skrivoo.whac_a_mole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private Button registerButton;
    private EditText email;
    private EditText password;
    private EditText passwordAgain;
    private FirebaseAuth mAuth;
    private PlayerDAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        dao = new PlayerDAO(this);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.emailAddressReg);
        password = findViewById(R.id.passwordReg);
        passwordAgain = findViewById(R.id.passwordAgainReg);
        registerButton = findViewById(R.id.registrationButton);
        registerButton.setOnClickListener(this::registerWithEmail);
    }

    public void registerWithEmail(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String passwordAgainText = passwordAgain.getText().toString();
        if (passwordText.equals(passwordAgainText)) {
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Player p = new Player(emailText, user.getPhotoUrl(),
                                    0, new ArrayList<>(), user.getUid());
                            dao.add(p);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Túl gyenge jelszó (min 6 char)", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "Nem egyezik meg a két jelszó", Toast.LENGTH_LONG).show();
        }
    }

}
