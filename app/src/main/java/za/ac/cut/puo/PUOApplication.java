package za.ac.cut.puo;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class PUOApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.initApp(this, Defaults.APP_ID, Defaults.SECRET_KEY, Defaults.APP_VERSION);
        Backendless.Messaging.registerDevice(Defaults.GCM_SENDER_ID, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

}
