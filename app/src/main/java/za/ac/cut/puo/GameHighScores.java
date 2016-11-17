package za.ac.cut.puo;

/**
 * Created by Shahbaaz Sheikh on 26/10/2016.
 */

public class GameHighScores {
    private String type;
    private double score;
    private String startDate;
    private String userName;
    private String userMail;

    public GameHighScores() {
        this.type = null;
        setScore(0);
        this.startDate = null;
        this.userName = null;
        this.userMail = null;

    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}