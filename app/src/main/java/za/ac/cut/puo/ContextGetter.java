package za.ac.cut.puo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shahbaaz Sheikh on 21/09/2016.
 */
//TODO: delete this cancer
public class ContextGetter extends Application {

    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }
}
