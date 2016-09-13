package za.ac.cut.puo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeMenu extends AppCompatActivity {
    Toolbar home_toolBar;
    TextView tvMail, tvUserType, tvWordCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        home_toolBar = (Toolbar)findViewById(R.id.home_toolBar);
        tvMail = (TextView) findViewById(R.id.tvEmail);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        setSupportActionBar(home_toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        tvMail.setText(getIntent().getStringExtra("user"));
        tvUserType.setText(getIntent().getStringExtra("role"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu_items,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.home_menu_item1:
                //Add a word
                return true;
            case R.id.home_menu_item2:
                //go to profile
                startActivity(new Intent(HomeMenu.this, Update.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
