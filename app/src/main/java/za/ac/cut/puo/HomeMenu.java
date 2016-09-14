package za.ac.cut.puo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class HomeMenu extends AppCompatActivity {
    Toolbar home_toolBar;
    TextView tvUsernameHome, tvUserType, tvWordCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);
        home_toolBar = (Toolbar)findViewById(R.id.home_toolBar);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        setSupportActionBar(home_toolBar);

        BackendlessUser user = Backendless.UserService.CurrentUser();
        tvUsernameHome.setText(user.getProperty("username").toString().trim());
        tvUserType.setText(user.getProperty("role").toString().trim());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_options_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.home_menu_addWord:
                //Add a word
                return true;
            case R.id.home_menu_profile:
                //go to profile
                updateProfileData();
                return true;
            case R.id.word_treasure:
                return true;
            case R.id.word_chest:
                return true;
            case R.id.word_2_word:
                return true;
            case R.id.word_mates:
                return true;
            case R.id.word_game:
                return true;
            case R.id.word_highscore:
                return true;
            case R.id.logout:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateProfileData() {

        BackendlessUser user = Backendless.UserService.CurrentUser();
        Intent intent = new Intent(HomeMenu.this, Update.class);
        intent.putExtra("user", user.getEmail());
        intent.putExtra("objectId", user.getObjectId());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("name", user.getProperty("name").toString().trim());
        intent.putExtra("surname", user.getProperty("surname").toString().trim());
        intent.putExtra("username", user.getProperty("username").toString().trim());
        intent.putExtra("role", user.getProperty("role").toString().trim());
        intent.putExtra("location", user.getProperty("location").toString().trim());
        intent.putExtra("cell", user.getProperty("cell").toString().trim());
        intent.putExtra("isUpdated", user.getProperty("isUpdated").toString().trim());
        startActivity(intent);

    }
}
