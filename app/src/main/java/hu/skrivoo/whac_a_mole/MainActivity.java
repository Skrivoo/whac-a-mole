package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseUser currentUser;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private TextView isUserLoggedTextView;
    private SignInButton googleSignInButton;
    public static Player player = new Player();
    private PlayerDAO dao;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(this::signInWithGoogleAccount);
        isUserLoggedTextView = findViewById(R.id.isLoggedTextView);
        loginButton = findViewById(R.id.startEmailLoginOrRegister);
        loginButton.setOnClickListener(this::startWithEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);
        currentUser = firebaseAuth.getCurrentUser();
        dao = new PlayerDAO(this);
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
        if (player == null) {
            updateUI();
        }
        if (currentUser != null) {
            getPlayer(new FirestoreCallback() {
                @Override
                public void onCallbackOne(Player player) {
                    MainActivity.player = player;
                    updateUI();
                }

                @Override
                public void onCallbackMore(List<Player> players) {

                }
            });
        }
    }

    private void getPlayer(FirestoreCallback firestoreCallback) {
        dao.get(currentUser, firestoreCallback);
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (currentUser != null) {
            isUserLoggedTextView.setText("Bejelentkezve, mint: " + player.getName());
        } else {
            isUserLoggedTextView.setText("Nincs bejelentkezve");
            player = new Player();
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
                                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    getPlayerDataFromFirestore();
                                } else {
                                    Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                                }
                            });
                }
            } catch (ApiException e) {
                Log.e(LOG_TAG, "Baj van...");
            }
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
                    }).addOnCompleteListener(task -> {
            });
        }
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
    public void onStart() {
        super.onStart();
        getPlayerDataFromFirestore();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getPlayerDataFromFirestore();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentUser = firebaseAuth.getCurrentUser();
        getPlayerDataFromFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlayerDataFromFirestore();
    }

    public void startWithEmail(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}