package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    private ImageView imageView;
    private RecyclerView recyclerView;
    private PlayerDAO dao;

    @SuppressLint({"NotifyDataSetChanged", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerList = new ArrayList<>();
        dao = new PlayerDAO(this);
        players = dao.getRef();
        players.orderBy("highestScore", Query.Direction.DESCENDING)
                .limit(10)
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
        imageView = findViewById(R.id.toplistDesignImage);
        Glide.with(ToplistActivity.this).load(R.drawable.mole_in_toplist).into(imageView);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//GridLayoutManager(this, 1)

        recyclerView.setAdapter(adapter);


    }

    @SuppressLint("NotifyDataSetChanged")
    private void orderingPlayers(int limitnum) {

        //adapter.notifyDataSetChanged();
    }

}