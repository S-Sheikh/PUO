package za.ac.cut.puo;

public class WordPictures {
    private String imageLocation;
    private String email;

    public WordPictures() {
        this.setImageLocation(null);
        this.setEmail(null);
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
