package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WordTreasureActivity extends AppCompatActivity implements
        WordListFragment.OnWordListItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_treasure);
        PUOHelper.initAppBar(this, getString(R.string.title_activity_word_treasure))
                .setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMenuOptionSelected(int id) {


    }

    @Override
    public void onWordSelected(Word word) {
    }


}
