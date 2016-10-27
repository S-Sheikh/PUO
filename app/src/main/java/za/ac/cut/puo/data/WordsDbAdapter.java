package za.ac.cut.puo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import za.ac.cut.puo.Word;
import za.ac.cut.puo.data.WordsDbContract.WordEntry;

public class WordsDbAdapter {
    private SQLiteDatabase db;
    private WordsDbHelper wordsDbHelper;

    public WordsDbAdapter(Context context) {
        wordsDbHelper = new WordsDbHelper(context);
        db = wordsDbHelper.getWritableDatabase();
    }

    public long insertWord(Word word) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(WordEntry.COLUMN_WORD, word.getWord());
        contentValues.put(WordEntry.COLUMN_LANGUAGE, word.getLanguage());
        contentValues.put(WordEntry.COLUMN_PARTOFSPEECH, word.getPartOfSpeech());
        contentValues.put(WordEntry.COLUMN_DEFINITION, word.getDefinition());
        contentValues.put(WordEntry.COLUMN_SENTENCE, word.getSentence());
        contentValues.put(WordEntry.COLUMN_SUPPORTED, word.isSupported());
        contentValues.put(WordEntry.COLUMN_RATING, word.isSupported());
        contentValues.put(WordEntry.COLUMN_IMAGELOCATION, word.getImageLocation());
        contentValues.put(WordEntry.COLUMN_NAME, word.getName());
        contentValues.put(WordEntry.COLUMN_SURNAME, word.getSurname());
        contentValues.put(WordEntry.COLUMN_EMAIL, word.getEmail());
        contentValues.put(WordEntry.COLUMN_CREATED, String.valueOf(word.getCreated()));

        return db.insert(WordEntry.TABLE_NAME, null, contentValues);
    }

    public Word getWordEntry(String wordKey) {
        String selection = WordEntry.COLUMN_WORD + " = ?";
        String[] selectionArgs = {wordKey};
        Word word = null;

        Cursor wCursor = db.query(WordEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (wCursor.getCount() > 0) {
            wCursor.moveToFirst();
            word = cursorToWord(wCursor);
        }
        return word;
    }

    public List<Word> getWords() {
        List<Word> words = new ArrayList<>();

        Cursor wCursor = db.query(WordEntry.TABLE_NAME, null, null, null, null, null, null);
        wCursor.moveToFirst();
        while (!wCursor.isAfterLast()) {
            words.add(cursorToWord(wCursor));
            wCursor.moveToNext();
        }
        return words;
    }

    public void clearEntries() {
        wordsDbHelper.onUpgrade(db, 1, 1);
    }

    private Word cursorToWord(Cursor wCursor) {
        return new Word(wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_WORD)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_LANGUAGE)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_PARTOFSPEECH)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_DEFINITION)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_SENTENCE)),
                wCursor.getType(wCursor.getColumnIndex(WordEntry.COLUMN_SUPPORTED)),
                wCursor.getFloat(wCursor.getColumnIndex(WordEntry.COLUMN_RATING)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_IMAGELOCATION)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_NAME)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_SURNAME)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_EMAIL)),
                wCursor.getString(wCursor.getColumnIndex(WordEntry.COLUMN_CREATED)));
    }
}
