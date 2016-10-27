package za.ac.cut.puo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hodielisrael on 2016/09/14.
 */

public class PUOHelper {

    public static final String BASE_EXTERNAL_APP_DIRECTORY =
            Environment.getExternalStorageDirectory().toString() + "/PUO/";
    public static final String PROFILE_PIC_SUB_DIRECTORY = "Profile_picture/";

    //Word List BoilerPlate
    public static List<Word> populateWordList() {
        List<Word> words = new ArrayList<>();

        //English Words
        words.add(new Word("eutaxy", "English", "good order or management", "noun"));
        words.add(new Word("dabster", "English", "Slang. an expert", "noun"));
        words.add(new Word("pulverulent", "English", "covered with dust or powder.", "adjective"));
        words.add(new Word("vilipend", "English", "to vilify; depreciate", "verb"));

        //Afrikaans Words
        words.add(new Word("vark", "Afrikaans", "an omnivorous domesticated hoofed mammal", "noun"));
        words.add(new Word("evalueer", "Afrikaans", "evaluate or estimate the nature, ability, or quality of", "verb"));
        words.add(new Word("piek", "Afrikaans", "a thin, pointed piece of metal, wood, or another rigid material", "noun"));
        words.add(new Word("golf", "Afrikaans", "a gesture or signal made by moving one's hand to and fro", "noun"));

        //Zulu Words
        words.add(new Word("ukuzibulala", "Zulu", "the action of killing oneself intentionally", "noun"));
        words.add(new Word("bazizwa", "Zulu", "experience (an emotion or sensation)", "verb"));
        words.add(new Word("kuzwakale", "Zulu", "in good condition; not damaged, injured, or diseased", "adjective"));
        words.add(new Word("ingadi", "Zulu", "a piece of ground, often near a house, used for growing flowers, fruit, or vegetablesy", "noun"));

        //Xhosa Words
        words.add(new Word("inja", "Xhosa", "a domesticated carnivorous mammal that typically has a long snout", "noun"));
        words.add(new Word("Dudula", "Xhosa", "an act of exerting force on someone or something", "verb"));
        words.add(new Word("umzabalazo", "Xhosa", "a forceful or violent effort to get free of restraint or resist attack", "noun"));
        words.add(new Word("thetha", "Xhosa", "conversation; discussion", "noun"));

        return words;
    }

    /**
     * Sets up the action bar for an activity using the toolbar puo_toolbar.xml layout.
     * Add <include layout="@layout/puo_toolbar"/> in your activity's layout then call this
     * method in your activity's onCreate.
     */
    public static ActionBar setAppBar(AppCompatActivity activity, String title) {
        Toolbar appBar = (Toolbar) activity.findViewById(R.id.puo_toolbar);
        appBar.setTitle(title);
        activity.setSupportActionBar(appBar);
        return activity.getSupportActionBar();
    }

    public static void writeImageToFile(Context context, Bitmap bitmap) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String path = BASE_EXTERNAL_APP_DIRECTORY + PROFILE_PIC_SUB_DIRECTORY;
        OutputStream fOut;
        String filename = user.getEmail() + "_.png";
        File profilePicDirectory = new File(path);
        File file = new File(profilePicDirectory, filename); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        System.out.println("file = " + file); // for debug purposes
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

    //Reads an image from device storage and sets it as background
    public static void readImage(ImageView view) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String filename = user.getEmail() + ".png";
        String filepath = BASE_EXTERNAL_APP_DIRECTORY + PROFILE_PIC_SUB_DIRECTORY + filename;
        System.out.println("filepath = " + filepath);
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
            URL url = new URL("https://api.backendless.com/D200A885-7EED-CB51-FFAC-228F87E55D00/v1/files/UserProfilePics/" + imageLocation);
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

}
