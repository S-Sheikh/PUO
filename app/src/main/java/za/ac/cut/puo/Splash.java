package za.ac.cut.puo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;

public class Splash extends AppCompatActivity {
    TextView tvLoading;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        progressBar.setVisibility(View.GONE);
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean aBoolean) {
                if (!aBoolean) {
                    startActivity(new Intent(Splash.this, Login.class));
                    Splash.this.finish();
                } else {
                    tvLoading.setText(getString(R.string.tvLoading));
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();//gets user id of specific user that is loggen in
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            Intent intent = new Intent(Splash.this, HomeMenu.class);
                            intent.putExtra("user", backendlessUser.getEmail());
                            intent.putExtra("name", backendlessUser.getProperty("name").toString().trim());
                            intent.putExtra("surname", backendlessUser.getProperty("surname").toString().trim());
                            startActivity(intent);
                            Splash.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(Splash.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Splash.this, Login.class));
                            Splash.this.finish();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(Splash.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Splash.this, Login.class));
                Splash.this.finish();
            }
        });
    }

}
