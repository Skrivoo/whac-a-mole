package hu.skrivoo.whac_a_mole;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ToplistActivity extends AppCompatActivity {

    private static final String LOG_TAG = ToplistActivity.class.getName();
    private FirebaseFirestore firestore;
    private CollectionReference players;
    private ArrayList<Player> playerList;
    private ToplistPlayerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toplist);
        playerList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        players = firestore.collection("scores");
        players.orderBy("highestScore", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Player p = doc.toObject(Player.class);
                playerList.add(p);
            }
        }).addOnCompleteListener(runnable -> {
            for (Player p : playerList) {
                System.out.println("legnagyobb: " + p.getHighestScore());
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ToplistPlayerAdapter(this, playerList);
        //pullDataFromFirestore();

    }

    private void pullDataFromFirestore() {
        firestore.collection("scores").get().addOnCompleteListener(task -> {

            for (QueryDocumentSnapshot a : task.getResult()) {
                playerList.add(a.toObject(Player.class));
            }

        });

        firestore.collection("scores").orderBy("topScore", Query.Direction.DESCENDING).limit(10);

    }


}