package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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
        if (currentUser != null) {
            updateUI(currentUser);
        }
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("196945603239-ikd8ja9970d4f7ikqobuv79ggctn5dbd.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = googleCredential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(LOG_TAG, "signInWithCredential:success");
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                                            updateUI(null);
                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    Log.e(LOG_TAG, "Baj van...");
                }
                break;
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
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(LOG_TAG, e.getLocalizedMessage());
                    });
        }
        currentUser = firebaseAuth.getCurrentUser();
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