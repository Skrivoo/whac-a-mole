package hu.skrivoo.whac_a_mole;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Player {

    private Integer highestScore;
    private List<Integer> scoreList;
    private FirebaseUser firebaseUser;

    public Player(Integer highestScore, List<Integer> scoreList, FirebaseUser firebaseUser) {
        this.highestScore = highestScore;
        this.scoreList = scoreList;
        this.firebaseUser = firebaseUser;
    }

    public Player() {
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public List<Integer> getScoreList() {
        return scoreList;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }
}
