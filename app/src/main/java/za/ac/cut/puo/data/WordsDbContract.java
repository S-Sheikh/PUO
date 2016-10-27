package za.ac.cut.puo.data;

import android.provider.BaseColumns;

public class WordsDbContract {

    private WordsDbContract() {}

    /* Inner class that defines the table contents */
    public static final class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";

        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_PARTOFSPEECH = "partofspeech";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_SENTENCE = "sentence";
        public static final String COLUMN_SUPPORTED= "supported";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_IMAGELOCATION = "imagelocation";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_CREATED = "created";

    }
}
