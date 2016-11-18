package za.ac.cut.puo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import za.ac.cut.puo.data.WordsDbAdapter;

/**
 * Created by hodielisrael on 2016/09/14.
 */

public class PUOHelper {

    /**
     * Sets up and returns the action bar for an activity using the toolbar puo_toolbar.xml layout.
     * Add <include layout="@layout/puo_toolbar"/> in your activity's layout then call this
     * method in your activity's onCreate.
     *
     * @return ActionBar
     */
    public static ActionBar initAppBar(AppCompatActivity activity, String title) {
        Toolbar appBar = (Toolbar) activity.findViewById(R.id.puo_toolbar);
        appBar.setTitle(title);
        activity.setSupportActionBar(appBar);
        return activity.getSupportActionBar();
    }

    public static void writeImageToFile(Context context, Bitmap bitmap) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String path = Defaults.BASE_EXTERNAL_APP_DIRECTORY + Defaults.PROFILE_PIC_SUB_DIRECTORY;
        OutputStream fOut;
        String filename = user.getEmail() + "_.png";
        File profilePicDirectory = new File(path);
        File file = new File(profilePicDirectory, filename); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            if (profilePicDirectory.mkdirs()) {
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a PNG with 100% compression rate
                fOut.close(); // do not forget to close the stream
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads an image from device storage and sets it as background.
     */
    public static void readImage(ImageView view) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String filename = user.getEmail() + ".png";
        String filepath = Defaults.BASE_EXTERNAL_APP_DIRECTORY + Defaults.PROFILE_PIC_SUB_DIRECTORY + filename;
        File imagefile = new File(filepath);
        FileInputStream fis = null;
        if (imagefile.exists()) {
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            view.setImageBitmap(bitmap);
        } else {
            view.setImageResource((R.drawable.logo_puo));
            getImageOnline(new DownloadTask(view));
        }
    }

    /**
     * Check if there is an internet connection available.
     */
    public static boolean connectionAvailable(Context context) {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { //Connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                connected = true;
        }
        return connected;
    }

    public static void getImageOnline(DownloadTask task) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String imageLocation = user.getEmail() + "_.png";
        try {
            URL url = new URL(Defaults.PROF_PIC_BASE_URL + imageLocation);
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate image for word image using first two letters of word.
     */
    public static TextDrawable getTextDrawable(Word word) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // set random color generator
        String text = word.getWord().substring(0, 2); // get first two letters of word
        return TextDrawable.builder()
                .beginConfig()
                .bold()
                .fontSize(90)
                .width(180)
                .height(180)
                .endConfig().buildRect(text, generator.getRandomColor());
    }

    public static TextDrawable getTextDrawable(BackendlessUser user) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // set random color generator
        String text = user.getProperty("name").toString().substring(0, 2); // get first two letters of a user
        return TextDrawable.builder()
                .beginConfig()
                .bold()
                .fontSize(90)
                .width(180)
                .height(180)
                .endConfig().buildRect(text, generator.getRandomColor());
    }

    public static TextDrawable getTextDrawable(GameHighScores user) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // set random color generator
        String text = user.getUserName().toString().substring(0, 2); // get first two letters of a user
        return TextDrawable.builder()
                .beginConfig()
                .bold()
                .fontSize(90)
                .width(180)
                .height(180)
                .endConfig().buildRect(text, generator.getRandomColor());
    }

    /**
     * Returns a popup window for setting a word rating.
     *
     * @param container  view for anchoring the popup window.
     * @param context activity that this method is being called from.
     * @return popup
     */
    public static PopupWindow getPopup(Context context, @Nullable ViewGroup container) {
        PopupWindow popup = new PopupWindow(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        View layout = View.inflate(context, R.layout.rate_word_popup, container);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        return popup;
    }

    /**
     * Saves a Word object to the local SQLite database.
     */
    public static class SaveToWordChestTask extends AsyncTask<Word, Void, Long> {
        private static WordsDbAdapter wordsDbAdapter;
        private static Context mContext;

        private SaveToWordChestTask(Context context) {
            wordsDbAdapter = new WordsDbAdapter(context);
            mContext = context;
        }

        public static SaveToWordChestTask getTask(Context context) {
            return new SaveToWordChestTask(context);
        }

        @Override
        protected Long doInBackground(Word... word) {
            return wordsDbAdapter.insertWord(word[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            if (result > 0)
                Toast.makeText(mContext, "Word saved to WordChest!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets the words from the local SQLite database.
     *
     * @return a List<Word>
     */
    public static class LoadFromWordChestTask extends AsyncTask<Void, Void, List<Word>> {
        private WordsDbAdapter wordsDbAdapter;
        private Context mContext;
        WordListItemAdapter mAdapter;

        private LoadFromWordChestTask(Context context, WordListItemAdapter adapter) {
            wordsDbAdapter = new WordsDbAdapter(context);
            mAdapter = adapter;
            mContext = context;
        }

        public static LoadFromWordChestTask initialize(Context context, WordListItemAdapter adapter) {
            return new LoadFromWordChestTask(context, adapter);
        }

        @Override
        protected List<Word> doInBackground(Void... params) {
            return wordsDbAdapter.getWords();
        }

        @Override
        protected void onPostExecute(List<Word> words) {
            if (words == null)
                Toast.makeText(mContext, "No words in WordChest!", Toast.LENGTH_SHORT).show();
            else {
                WordListFragment.setmWords(words);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), words.size());
            }
        }
    }
}
