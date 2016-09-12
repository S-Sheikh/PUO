package za.ac.cut.puo;

import android.app.Application;
import com.backendless.Backendless;
public class PUOApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String appVersion = "v1";
        Backendless.initApp(this,"D200A885-7EED-CB51-FFAC-228F87E55D00","95A8B22D-7E32-572D-FF0B-C002E7959800",appVersion);
    }
}
