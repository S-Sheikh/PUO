package za.ac.cut.puo;

import android.graphics.Bitmap;
import android.media.Rating;

import com.backendless.media.audio.AudioStream;

/**
 * Created by Shahbaaz Sheikh on 12/09/2016.
 */
public class Word {
    private String word, language, definition, partOfSpeech, usageSentence;
    private Bitmap descImage;
    private AudioStream pronunciation;
    private Rating wordRating;
    private boolean repeatFlag;

    public Word() {}

    public Word(String word, String language, String definition, String partOfSpeech) {
        this.word = word;
        this.language = language;
        this.definition = definition;
        this.partOfSpeech = partOfSpeech;
        repeatFlag = false;
    }

    public Bitmap getDescImage() {
        return descImage;
    }

    public void setDescImage(Bitmap descImage) {
        this.descImage = descImage;
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

    public String getUsageSentence() {
        return usageSentence;
    }

    public void setUsageSentence(String usageSentence) {
        this.usageSentence = usageSentence;
    }

    public AudioStream getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(AudioStream pronunciation) {
        this.pronunciation = pronunciation;
    }

    public Rating getWordRating() {
        return wordRating;
    }

    public void setWordRating(Rating wordRating) {
        this.wordRating = wordRating;
    }
}
