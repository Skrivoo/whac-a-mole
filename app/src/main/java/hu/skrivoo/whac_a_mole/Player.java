package hu.skrivoo.whac_a_mole;

import android.net.Uri;

import java.util.List;

public class Player {

    private String name;
    private Uri avatar;
    private Integer highestScore;
    private List<Integer> scoreList;
    private String uid;

    public Player(String name, Uri avatar, Integer highestScore, List<Integer> scoreList, String uid) {
        this.highestScore = highestScore;
        this.scoreList = scoreList;
        this.avatar = avatar;
        this.name = name;
        this.uid = uid;
    }

    public Player() {
    }

    public Integer getHighestScore() {
        return highestScore;
    }


    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = Uri.parse(avatar);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Integer> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Integer> scoreList) {
        this.scoreList = scoreList;
    }

}
