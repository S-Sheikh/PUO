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

//------> Still needs Work <------- @Watley
public class Splash extends AppCompatActivity {
    TextView tvLoading;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean aBoolean) {
                if (!aBoolean) {
                    startActivity(new Intent(Splash.this, Login.class));
                    Splash.this.finish();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    tvLoading.setText(getString(R.string.tvLoading));
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();//gets user id of specific user that is logged in
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            Intent intent = new Intent(Splash.this, HomeMenu.class);
                            intent.putExtra("user", backendlessUser.getEmail());
                            intent.putExtra("password", backendlessUser.getPassword());
                            intent.putExtra("name", backendlessUser.getProperty("name").toString().trim());
                            intent.putExtra("surname", backendlessUser.getProperty("surname").toString().trim());
                            intent.putExtra("username", backendlessUser.getProperty("username").toString().trim());
                            intent.putExtra("cell", backendlessUser.getProperty("cell").toString().trim());
                            intent.putExtra("role", backendlessUser.getProperty("role").toString().trim());
                            intent.putExtra("location", backendlessUser.getProperty("location").toString().trim());
                            startActivity(intent);
                            Splash.this.finish();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(Splash.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Splash.this, Login.class));
                            Splash.this.finish();
                            progressBar.setVisibility(View.GONE);
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


