package za.ac.cut.puo;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class WordTreasureActivity extends AppCompatActivity implements
        WordListFragment.OnWordListItemListener {
    Toolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_treasure);
        setUpAppBar(this,R.drawable.ic_word_treasure, getResources().getString(R.string.title_activity_word_treasure));
        Snackbar sb = Snackbar.make(findViewById(R.id.container),"this is a snackbar",Snackbar.LENGTH_LONG);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        sb.show();
    }

    private void setUpAppBar(AppCompatActivity activity, int iconId, String title) {
        appBar = (Toolbar)findViewById(R.id.puo_toolbar);
        appBar.setLogo(iconId);
        appBar.setTitle(" "+title);
        setSupportActionBar(appBar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onWordListItemSelected(View v) {
        
    }
}
