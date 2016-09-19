package za.ac.cut.puo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hodielisrael on 2016/09/14.
 */

public class PUOHelper {

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
        appBar.setTitle(" " + title);
        activity.setSupportActionBar(appBar);
        return activity.getSupportActionBar();
    }

    public static void readImage(CircleImageView view) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String filename = user.getEmail() + ".png";
        String filepath = Environment.getExternalStorageDirectory().toString() + "/" + filename;
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

}
