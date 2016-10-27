package za.ac.cut.puo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static za.ac.cut.puo.data.WordsDbContract.WordEntry;


public class WordsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "wordchest.db";
    public static final int DATABASE_VERSION = 1;


    public WordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WORDS_TABLE =
                "CREATE TABLE " + WordEntry.TABLE_NAME + " (" +
                        WordEntry.COLUMN_WORD + " TEXT PRIMARY KEY, " +
                        WordEntry.COLUMN_LANGUAGE + " TEXT, " +
                        WordEntry.COLUMN_PARTOFSPEECH + " TEXT, " +
                        WordEntry.COLUMN_DEFINITION + " TEXT, " +
                        WordEntry.COLUMN_SENTENCE + " TEXT, " +
                        WordEntry.COLUMN_SUPPORTED + " BOOLEAN, " +
                        WordEntry.COLUMN_RATING + " REAL, " +
                        WordEntry.COLUMN_IMAGELOCATION + " TEXT, " +
                        WordEntry.COLUMN_NAME + " TEXT, " +
                        WordEntry.COLUMN_SURNAME + " TEXT," +
                        WordEntry.COLUMN_EMAIL +" TEXT, " +
                        WordEntry.COLUMN_CREATED +" TEXT)";

        db.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME);
        onCreate(db);
    }

}
