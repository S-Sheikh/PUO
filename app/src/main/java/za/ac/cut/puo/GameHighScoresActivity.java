package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
    BackendlessDataQuery dataQuery;
    QueryOptions queryOptions;
    Spinner sp_SortHighScores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_high_scores);
        lvWords = (ListView) findViewById(R.id.lv_highScores);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        sp_SortHighScores = (Spinner)findViewById(R.id.sp_sortHighScores);
        PUOHelper.initAppBar(GameHighScoresActivity.this, getResources().getString(R.string.app_name))
                .setDisplayHomeAsUpEnabled(true);
        dataQuery = new BackendlessDataQuery();
        queryOptions = new QueryOptions();
        loadData("type = 'Language'");
        sp_SortHighScores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view;
                switch (tv.getText().toString()){
                    case "What Language":
                        loadData("type = 'Language'");
                        break;
                    case "Match Defintion":
                        loadData("type = 'Definition'");
                        break;
                    case "Part Of Speech":
                        loadData("type = 'Speech'");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void loadData(String Clause) {

        circularBar.setVisibility(View.VISIBLE);
        if (scores != null) {
            scores.clear();
        }
        dataQuery = new BackendlessDataQuery();
        queryOptions = new QueryOptions();
        queryOptions.addSortByOption("score DESC");
        queryOptions.setPageSize(10);
        dataQuery.setWhereClause(Clause);
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
