package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class WordTreasureActivity extends AppCompatActivity implements
        WordListFragment.OnWordListItemListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_treasure);
        PUOHelper.setAppBar(this,getResources().getString(R.string.title_activity_word_treasure))
        .setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onWordListItemSelected(View v) {

    }

    @Override
    public void onOverFlowClicked(View v) {

    }
}
