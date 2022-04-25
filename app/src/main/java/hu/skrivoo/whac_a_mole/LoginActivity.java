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

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();
    private Button loginButton;
    private Button createRegistration;
    private Button deleteRegistration;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailAddressLogin);
        password = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        createRegistration = findViewById(R.id.openRegisterForm);
        deleteRegistration = findViewById(R.id.deleteRegistrationButton);
        loginButton.setOnClickListener(this::loginWithEmail);
        createRegistration.setOnClickListener(this::createRegistration);
        deleteRegistration.setOnClickListener(this::deleteRegistration);
    }

    private void deleteRegistration(View view) {

    }

    private void createRegistration(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginWithEmail(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Sikertelen belépés",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
