package hu.skrivoo.whac_a_mole;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlayerDAO {

    private CollectionReference db;

    public PlayerDAO() {
        db = FirebaseFirestore.getInstance().collection("scores");
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
                                MainActivity.player = new Player(user.getDisplayName(), user.getPhotoUrl(), 0, new ArrayList<Integer>(), user.getUid());
                                add(MainActivity.player).addOnSuccessListener(unused -> {
                                    //Toast.makeText(this, "Játékos létrehozva", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(q -> {
                                    //Toast.makeText(this, "Player error: " + q.getMessage(), Toast.LENGTH_LONG).show()
                                });
                            }
                        })
                .addOnFailureListener(e -> {});


        /*PlayerDAO dao = new PlayerDAO();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("scores").document(currentUser.getUid());
        docRef.get()
                .addOnSuccessListener(
                        documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                player = documentSnapshot.toObject(Player.class);
                                Toast.makeText(this, "lézezik már ilyen felhasználó", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "még nem létezik ilyen felhasználó", Toast.LENGTH_LONG).show();
                                player = new Player(currentUser.getDisplayName(), currentUser.getPhotoUrl(), 0, new ArrayList<Integer>(), currentUser.getUid());
                                dao.add(player).addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Játékos létrehozva", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(q -> {
                                    Toast.makeText(this, "Hiba történt", Toast.LENGTH_LONG).show();
                                });
                            }
                        })
                .addOnFailureListener(e -> {});*/
    }

}
