package hu.skrivoo.whac_a_mole;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedList;
import java.util.List;

public class ToplistActivity extends AppCompatActivity {

    private static final String LOG_TAG = ToplistActivity.class.getName();
    private FirebaseFirestore firestore;
    private CollectionReference players;
    private List<Player> playerList;
    private ToplistPlayerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toplist);
        playerList = new LinkedList<>();
        firestore = FirebaseFirestore.getInstance();
        players = firestore.collection("score");
        players.orderBy("topScore", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Player p = doc.toObject(Player.class);
                playerList.add(p);
            }
        });

        for (Player p : playerList) {
            Log.i(LOG_TAG, p.getFirebaseUser().getDisplayName() + " - " + p.getHighestScore());
        }
    }



}