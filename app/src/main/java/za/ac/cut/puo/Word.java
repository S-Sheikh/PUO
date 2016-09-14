package za.ac.cut.puo;

/**
 * Created by Shahbaaz Sheikh on 12/09/2016.
 */
public class Word {
    private String Word;
    private String Language;
    private String Definition;
    private String PartOfSpeech;
    private String email;
    private String name;
    private String surname;
    private boolean supported;
    private boolean repeatFlag;

    public Word(String word, String language, String definition, String partOfSpeech) {
        Word = word;
        Language = language;
        Definition = definition;

        PartOfSpeech = partOfSpeech;
        repeatFlag = false;
    }

    public boolean isRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(boolean repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getWord() {
        return Word;
    }

    public void setWord(String word) {
        Word = word;
    }

    public String getDefinition() {
        return Definition;
    }

    public void setDefinition(String definition) {
        Definition = definition;
    }

    public String getPartOfSpeech() {
        return PartOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        PartOfSpeech = partOfSpeech;
    }

}
