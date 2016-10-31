package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

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

        loadData();

        //TODO: Update table , table currently empty EX D D D


    }

    public void loadData() {
        circularBar.setVisibility(View.VISIBLE);
        if (scores != null) {
            scores.clear();
        }
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption("score DESC");
        dataQuery.setQueryOptions(queryOptions);
        Backendless.Data.of(GameHighScores.class).find(dataQuery, new AsyncCallback<BackendlessCollection<GameHighScores>>() {
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
