package org.flower.risibank.user;

public class UserStat {

    int mediaCount;
    int interactCount;
    int score;

    public UserStat(int mediaCount, int interactCount, int score){
        this.mediaCount = mediaCount;
        this.interactCount = interactCount;
        this.score = score;
    }

    public int getInteractCount() {
        return interactCount;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public int getScore() {
        return score;
    }
}
