package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class WordTreasureActivity extends AppCompatActivity implements
        WordListItemAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_treasure);
        PUOHelper.setAppBar(this,getResources().getString(R.string.title_activity_word_treasure))
        .setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onWordSelected(View itemView) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.support:
                return true;
            case R.id.block:
                return true;
            case R.id.rate:
                return true;
            case R.id.add_to_word_chest:
                return true;
            case R.id.share:
                return true;
            default:
                return false;
        }

    }
}
