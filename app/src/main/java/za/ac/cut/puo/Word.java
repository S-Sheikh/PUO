package za.ac.cut.puo;

import com.backendless.media.audio.AudioStream;

import java.util.Date;

public class Word {
    private boolean repeatFlag;
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
    private boolean supported;
    private boolean blocked;
    private int count;
    private float rating;
    private AudioStream pronunciation;
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


    public AudioStream getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(AudioStream pronunciation) {
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
