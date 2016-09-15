package za.ac.cut.puo;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
        Snackbar sb = Snackbar.make(findViewById(R.id.container),"this is a snackbar",Snackbar.LENGTH_LONG);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        sb.show();
    }


    @Override
    public void onWordListItemSelected(View v) {
        
    }
}
