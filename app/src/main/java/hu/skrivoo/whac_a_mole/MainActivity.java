package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseUser currentUser;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private TextView isUserLoggedTextView;
    private SignInButton googleSignInButton;
    public static Player player;
    private PlayerDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(this::signInWithGoogleAccount);
        isUserLoggedTextView = findViewById(R.id.isLoggedTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);
        currentUser = firebaseAuth.getCurrentUser();
        dao = new PlayerDAO(this);
        if (currentUser != null) {
            updateUI(currentUser);
        }
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("196945603239-ikd8ja9970d4f7ikqobuv79ggctn5dbd.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
        getPlayerDataFromFirestore();
    }

    private void getPlayerDataFromFirestore() {
        if (currentUser != null) {
            dao.get(currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = googleCredential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    Log.d(LOG_TAG, "signInWithCredential:success");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                                    updateUI(null);
                                }
                            });
                }
            } catch (ApiException e) {
                Log.e(LOG_TAG, "Baj van...");
            }
        }
        currentUser = firebaseAuth.getCurrentUser();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            isUserLoggedTextView.setText("Bejelentkezve, mint: " + user.getDisplayName());
        } else {
            isUserLoggedTextView.setText("Nincs bejelentkezve");
        }
    }

    public void signInWithGoogleAccount(View view) {
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        } else {
            oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(this, result -> {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(LOG_TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Log.d(LOG_TAG, e.getLocalizedMessage());
                    });
        }
        currentUser = firebaseAuth.getCurrentUser();
        getPlayerDataFromFirestore();
        updateUI(currentUser);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


    public void startToplist(View view) {
        Intent intent = new Intent(this, ToplistActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentUser = firebaseAuth.getCurrentUser();
    }

}