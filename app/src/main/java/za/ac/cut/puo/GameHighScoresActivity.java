package za.ac.cut.puo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

public class GameHighScoresActivity extends AppCompatActivity {

    private List<GameHighScores> scores;
    ListView lvWords;
    ProgressBar circularBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_high_scores);
        lvWords = (ListView) findViewById(R.id.lv_highScores);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        PUOHelper.setAppBar(GameHighScoresActivity.this, getResources().getString(R.string.app_name))
                .setDisplayHomeAsUpEnabled(true);
        try{
            loadData();
        }catch (Exception e){
            //TODO: Update table , table currently empty EX D D D
        }
    }
    public void loadData(){
        circularBar.setVisibility(View.VISIBLE);
        if (scores != null) {
            scores.clear();
        }
        Backendless.Data.of(GameHighScores.class).find(new AsyncCallback<BackendlessCollection<GameHighScores>>() {
            @Override
            public void handleResponse(BackendlessCollection<GameHighScores> addWordBackendlessCollection) {
                scores = addWordBackendlessCollection.getData();
                HighScoreItemAdapter adapter = new HighScoreItemAdapter(GameHighScoresActivity.this, scores);
                lvWords.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                circularBar.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(GameHighScoresActivity.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
