package za.ac.cut.puo;

public class UserProfilePictures {
    private String userMail;
    private String imageLocation;

    public UserProfilePictures() {
        this.setUserMail(null);
        this.setImageLocation(null);
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}
