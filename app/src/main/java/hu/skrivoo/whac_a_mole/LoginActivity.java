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

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();
    private Button loginButton;
    private Button createRegistration;
    private Button deleteRegistration;
    private Button logout;
    private Button editProfile;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private PlayerDAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        dao = new PlayerDAO(this);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailAddressLogin);
        password = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        createRegistration = findViewById(R.id.openRegisterForm);
        deleteRegistration = findViewById(R.id.deleteRegistrationButton);
        logout = findViewById(R.id.logoutButton);
        editProfile = findViewById(R.id.updateUser);
        loginButton.setOnClickListener(this::loginWithEmail);
        createRegistration.setOnClickListener(this::createRegistration);
        deleteRegistration.setOnClickListener(this::deleteRegistration);
        logout.setOnClickListener(this::logout);
        editProfile.setOnClickListener(this::editProfile);
    }

    private void editProfile(View view) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Nem vagy bejelentkezve.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, EditLoginActivity.class);
            startActivity(intent);
        }
    }

    private void logout(View view) {
        if (mAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            MainActivity.player = null;
            Toast.makeText(this, "Kijelentkeztetve.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Nem vagy bejelentkezve.", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteRegistration(View view) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Nem vagy bejelentkezve.", Toast.LENGTH_LONG).show();
        } else {
            FirebaseUser user = mAuth.getCurrentUser();
            String uid = user.getUid();
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dao.delete(uid, this);
                            Log.d(LOG_TAG, "User account deleted.");
                            MainActivity.player = null;
                        }
                    });
        }
    }

    private void createRegistration(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginWithEmail(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        if (emailText.equals("") || passwordText.equals("")) {
            Toast.makeText(this, "Üres valamelyik mező", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            dao.get(user, new FirestoreCallback() {
                                @Override
                                public void onCallbackOne(Player player) {
                                    MainActivity.player = player;
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCallbackMore(List<Player> players) {

                                }
                            });
                        } else {
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Sikertelen belépés",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
