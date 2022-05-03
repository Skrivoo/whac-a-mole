package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ToplistActivity extends AppCompatActivity {

    private static final String LOG_TAG = ToplistActivity.class.getName();
    private  List<Player> playerList;
    private ToplistPlayerAdapter adapter;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private PlayerDAO dao;

    @SuppressLint({"NotifyDataSetChanged", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new PlayerDAO(this);
        playerList = new ArrayList<>();
        adapter = new ToplistPlayerAdapter(this, playerList);
        setContentView(R.layout.activity_toplist);
        imageView = findViewById(R.id.toplistDesignImage);
        imageView.setOnClickListener(this::changeQuery);
        Glide.with(ToplistActivity.this).load(R.drawable.mole_in_toplist).into(imageView);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getTop10Player(new FirestoreCallback() {
            @Override
            public void onCallbackOne(Player player) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCallbackMore(List<Player> players) {
                playerList.clear();
                playerList.addAll(players);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void changeQuery(View view) {
        getAllPlayer(new FirestoreCallback() {
            @Override
            public void onCallbackOne(Player player) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCallbackMore(List<Player> players) {
                playerList.clear();
                playerList.addAll(players);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getTop10Player(FirestoreCallback firestoreCallback) {
        dao.getTop10(firestoreCallback);
    }

    private void getAllPlayer(FirestoreCallback firestoreCallback) {
        dao.getAll(firestoreCallback);
    }

}