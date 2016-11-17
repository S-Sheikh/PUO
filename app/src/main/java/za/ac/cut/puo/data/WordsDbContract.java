package za.ac.cut.puo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class WordsDbContract {

    public static final String CONTENT_AUTHORITY = "za.ac.cut.puo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORDS = "words";

    private WordsDbContract() {
    }

    /* Inner class that defines the table contents */
    public static final class WordEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;

        public static final String TABLE_NAME = "words";

        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_PARTOFSPEECH = "partofspeech";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_SENTENCE = "sentence";
        public static final String COLUMN_SUPPORTED = "supported";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_IMAGELOCATION = "imagelocation";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_CREATED = "created";

    }
}
