package za.ac.cut.puo;

import java.util.Date;

/**
 * Created by Shahbaaz Sheikh on 26/10/2016.
 */

public class GameHighScores {
    private String type;
    private double score;
    public Date created;
    private String userName;

    public GameHighScores(String type, double score, Date created, String userName) {
        this.type = null;
        this.setScore(0);
        this.created = null;
        this.userName = null;
    }

    public GameHighScores() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}