package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {

    private static final String LOG_TAG = PlayerDAO.class.getName();
    private CollectionReference db;
    private Context context;

    public PlayerDAO(Context context) {
        db = FirebaseFirestore.getInstance().collection("scores");
        this.context = context;
    }

    public Task<Void> add(Player player) {
        return db.document(player.getUid()).set(player);
    }

    public void get(FirebaseUser user) {
        DocumentReference docRef = db.document(user.getUid());
        docRef.get()
                .addOnSuccessListener(
                        documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                MainActivity.player = documentSnapshot.toObject(Player.class);
                            } else {
                                String name = "";
                                if (user.getDisplayName() == null || user.getDisplayName().equals(""))
                                MainActivity.player = new Player(user.getDisplayName(), user.getPhotoUrl(), 0, new ArrayList<Integer>(), user.getUid());
                                add(MainActivity.player).addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Játékos létrehozva", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(q -> {
                                    Toast.makeText(context, "Player error: " + q.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                        })
                .addOnFailureListener(e -> {
                });
    }

    public List<Player> getAll() {
        List<Player> players = new ArrayList<>();
        db.orderBy("name").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot ref : task.getResult()) {
                players.add(ref.toObject(Player.class));
            }
        });
        return players;
    }

    @SuppressLint("NotifyDataSetChanged")
    public List<Player> getPlayersOrderByHighscore(int limitnum) {
        List<Player> playerList = new ArrayList<>();
        db.orderBy("highestScore", Query.Direction.DESCENDING)
                .limit(limitnum)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot qds : task.getResult()) {
                            playerList.add(qds.toObject(Player.class));
                        }
                    }
                }).addOnFailureListener(runnable -> {
                    Log.e(LOG_TAG, runnable.getMessage());
        });
        return playerList;
    }

    public CollectionReference getRef() {
        return db;
    }

    /**
     * Delete the users data from firestore database based on uid
     */
    public void delete(String uid, Context context) {
        db.document(uid).delete().addOnCompleteListener(task -> Toast.makeText(context, "User sikeresen törölve.", Toast.LENGTH_LONG).show());
    }

    /**
     * @param currentUser Firebase user to use uid
     * @param field the field to be updated
     * @param value the new value of field.
     *              If value is "-1" -> delete allScore
     *              if value is "0" -> delete highestScore
     */
    public void update(FirebaseUser currentUser, String field, String value) {
        if (value.equals("-1")) {
            db.document(currentUser.getUid()).update(
                    field, new ArrayList<>()
            );
        } else if (value.equals("0")) {
            db.document(currentUser.getUid()).update(
                    field, 0
            );
        } else {
            db.document(currentUser.getUid()).update(
                    field, value
            );
        }
    }

}
