package za.ac.cut.puo;

import java.util.Date;

public class Word {
    private String Word;
    private String Language;
    private String Definition;
    private String PartOfSpeech;
    private String Email;
    private String Name;
    private String Surname;
    private boolean Supported;
    private boolean RepeatFlag;
    private String objectId;
    private Date created;
    private Date updated;

    public Word() {
        this.Word = null;
        this.Language = null;
        this.Definition = null;
        this.PartOfSpeech = null;
        this.Email = null;
        this.Name = null;
        this.Surname = null;
        this.Supported = false;
        this.RepeatFlag = false;
    }
    //For the Word Game
    public Word(String word, String language, String definition, String partOfSpeech) {
        Word = word;
        Language = language;
        Definition = definition;
        PartOfSpeech = partOfSpeech;
        RepeatFlag = false;
    }

    public boolean isRepeatFlag() {
        return RepeatFlag;
    }

    public void setRepeatFlag(boolean repeatFlag) {
        this.RepeatFlag = repeatFlag;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        this.Surname = surname;
    }

    public boolean isSupported() {
        return Supported;
    }

    public void setSupported(boolean supported) {
        this.Supported = supported;
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
}
