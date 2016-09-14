package za.ac.cut.puo;

import android.graphics.Bitmap;

import com.backendless.media.audio.AudioStream;

import java.util.Date;

public class Word {
    private String word;
    private String language;
    private String definition;
    private String partOfSpeech;
    private String email;
    private String name;
    private String surname;
    private String sentence;
    private String lexicon;
    private boolean supported;
    private boolean repeatFlag;
    private String objectId;
    private Date created;
    private Date updated;
    private AudioStream pronunciation;
    private float rating;
    private Bitmap descImage;

    public Word() {
        this.word = null;
        this.language = null;
        this.definition = null;
        this.partOfSpeech = null;
        this.email = null;
        this.name = null;
        this.surname = null;
        this.supported = false;
        this.repeatFlag = false;
    }

    //For the word Game
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
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

    public Bitmap getDescImage() {
        return descImage;
    }

    public void setDescImage(Bitmap descImage) {
        this.descImage = descImage;
    }

    public String getLexicon() {
        return lexicon;
    }

    public void setLexicon(String lexicon) {
        this.lexicon = lexicon;
    }

    public String getStatus() {
        return supported ? "supported" : "not supported";
    }

    public String getAuthor(){
        return name + " " + surname;
    }
}
