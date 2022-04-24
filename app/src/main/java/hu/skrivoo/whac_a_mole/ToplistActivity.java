package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ToplistActivity extends AppCompatActivity {

    private static final String LOG_TAG = ToplistActivity.class.getName();
    private CollectionReference players;
    public List<Player> playerList;
    private ToplistPlayerAdapter adapter;
    private RecyclerView recyclerView;
    private PlayerDAO dao;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerList = new ArrayList<>();
        dao = new PlayerDAO(this);
        players = dao.getRef();
        players.orderBy("highestScore", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Player p = doc.toObject(Player.class);
                        playerList.add(p);
                    }
                    adapter.notifyDataSetChanged();
                }).addOnCompleteListener(runnable -> {
            for (Player p : playerList) {
                System.out.println("legnagyobb: " + p.getHighestScore());
                adapter.notifyDataSetChanged();
            }
        });
        //orderingPlayers(2);


        //playerList = dao.getPlayersOrderByHighscore(10);

        adapter = new ToplistPlayerAdapter(this, playerList);
        setContentView(R.layout.activity_toplist);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


        recyclerView.setAdapter(adapter);


    }

    @SuppressLint("NotifyDataSetChanged")
    private void orderingPlayers(int limitnum) {

        //adapter.notifyDataSetChanged();
    }

}