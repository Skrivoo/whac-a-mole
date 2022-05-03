package hu.skrivoo.whac_a_mole;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerDAO {

    private static final String LOG_TAG = PlayerDAO.class.getName();
    private final CollectionReference db;
    private Context context;

    public PlayerDAO(Context context) {
        db = FirebaseFirestore.getInstance().collection("scores");
        this.context = context;
    }

    public Task<Void> add(Player player) {
        return db.document(player.getUid()).set(player);
    }

    public void add(FirebaseUser user, FirestoreCallback firestoreCallback) {
        Player p = new Player(user.getDisplayName(), user.getPhotoUrl(), 0, new ArrayList<Integer>(), user.getUid());
        db.document(user.getUid()).set(p);
        firestoreCallback.onCallbackOne(p);
    }

    public void get(FirebaseUser user, FirestoreCallback firestoreCallback) {
        DocumentReference docRef = db.document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                firestoreCallback.onCallbackOne(task.getResult().toObject(Player.class));
            } else {
                add(user, firestoreCallback);
            }
        });
    }

    public void getAll(FirestoreCallback firestoreCallback) {
        List<Player> players = new ArrayList<>();
        db.orderBy("name").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot ref : task.getResult()) {
                players.add(ref.toObject(Player.class));
            }
            firestoreCallback.onCallbackMore(players);
        });
    }


    public void getUsersByOneScoreAndAscendingByName(FirestoreCallback firestoreCallback, int times) {
        List<Player> players = new ArrayList<>();
        db.orderBy("scoreList", Query.Direction.DESCENDING)
                .whereArrayContainsAny("scoreList", Collections.singletonList(times))
                .orderBy("name") //növekvő
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot ref : task.getResult()) {
                        players.add(ref.toObject(Player.class));
                    }
                    firestoreCallback.onCallbackMore(players);
                });
    }

    public void getTop10(FirestoreCallback firestoreCallback) {
        List<Player> players = new ArrayList<>();
        db.orderBy("highestScore", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot ref : task.getResult()) {
                        players.add(ref.toObject(Player.class));
                    }
                    firestoreCallback.onCallbackMore(players);
                });
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
     * @param field       the field to be updated
     * @param value       the new value of field.
     *                    If value is "-1" -> delete allScore
     *                    if value is "0" -> delete highestScore
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
