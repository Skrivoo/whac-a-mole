package hu.skrivoo.whac_a_mole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToplistActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private CollectionReference players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toplist);
        firestore = FirebaseFirestore.getInstance();
        players = firestore.collection("score");
    }



}