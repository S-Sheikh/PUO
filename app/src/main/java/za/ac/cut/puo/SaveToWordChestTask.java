package za.ac.cut.puo;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import za.ac.cut.puo.data.WordsDbAdapter;

/**
 * Created by hodielisrael on 2016/10/27.
 */
public class SaveToWordChestTask extends AsyncTask<Word, Void, Long> {
    WordsDbAdapter wordsDbAdapter;
    Context context;
    SaveToWordChestTask(Context context, Word word) {
        wordsDbAdapter = new WordsDbAdapter(context);
        this.context = context;
        execute(word);
    }

    @Override
    protected Long doInBackground(Word... word) {
        return wordsDbAdapter.insertWord(word[0]);
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result > 0)
            Toast.makeText(context, "Word saved to WordChest!", Toast.LENGTH_SHORT).show();
    }
}
