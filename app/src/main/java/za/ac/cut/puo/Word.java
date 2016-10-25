package za.ac.cut.puo;

import com.backendless.media.audio.AudioStream;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Word {
    private String email;
    private String word;
    private String language;
    private String definition;
    private String partOfSpeech;
    private String name;
    private String surname;
    private String sentence;
    private String objectId;
    private String imageLocation;
    private boolean repeatFlag;
    private boolean supported;
    private boolean blocked;
    private int count;
    private float rating;
    private String pronunciation;
    private Date created;
    private Date updated;

    public Word() {
        this.word = null;
        this.language = null;
        this.definition = null;
        this.partOfSpeech = null;
        this.name = null;
        this.surname = null;
        this.sentence = null;
        this.supported = false;
        this.pronunciation = null;
        this.blocked = false;
        this.repeatFlag = false;
        this.setCount(0);
        this.setRating(0);
    }

    //Game Word
    public Word(String word, String language, String definition, String partOfSpeech) {
        this.word = word;
        this.language = language;
        this.definition = definition;
        this.partOfSpeech = partOfSpeech;
        repeatFlag = false;
    }

    //WordChest Word
    public Word(String word, String language, String partOfSpeech, String definition,
                String sentence, int supported, float rating, String imageLocation,
                String name, String surname, String email, String created) {
        this.word = word;
        this.language = language;
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
        this.sentence = sentence;
        this.supported = (supported == 1);
        this.rating = rating;
        this.imageLocation = imageLocation;
        this.name = name;
        this.surname = surname;
        this.email = email;
        try {
            this.created = DateFormat.getDateInstance().parse(created);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(boolean repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getLexicon() {
        return partOfSpeech + ", " + language;
    }

    public String getStatus() {
        /**
         * @return status "Blocked" if word is blocked, else if word is supported return
         * "Supported" else "Unsupported".
         * */
        return blocked ? "Blocked" : (supported ? "Supported" : "Unsupported");
    }

    public String getAuthor() {
        return "Added by: " + name.substring(0, 1).toUpperCase() + ". " + surname.substring(0, 1).toUpperCase() + surname.substring(1);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}
