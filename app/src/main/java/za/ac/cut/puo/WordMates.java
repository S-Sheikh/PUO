package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Watlinton on 2016/10/26.
 */

public class WordMates extends AppCompatActivity {
    Toolbar wordMates_toolBar;
    ListView lvUsers;
    List<BackendlessUser> usersList;
    ProgressBar circularBar;
    TextView tvUsersOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_mates);
        wordMates_toolBar = (Toolbar) findViewById(R.id.wordMates_toolBar);
        setSupportActionBar(wordMates_toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        wordMates_toolBar.setTitleTextColor(getResources().getColor(R.color.colorIcons));
        lvUsers = (ListView) findViewById(R.id.lvContactList);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        tvUsersOnline = (TextView) findViewById(R.id.tvUsersOnline);
        loadData();
        countUsersOnline();
    }

    public void loadData() {
        circularBar.setVisibility(View.VISIBLE);
        if (usersList != null) {
            usersList.clear();

        }
        Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                try {
                    usersList = users.getData();
                    AddUserAdapter adapter = new AddUserAdapter(WordMates.this, usersList);
                    lvUsers.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    circularBar.setVisibility(View.GONE);
                } catch (Exception ex) {
                    Toast.makeText(WordMates.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(WordMates.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void countUsersOnline() {
        Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {
                int count = 0;
                try {
                    Iterator<BackendlessUser> userIterator = backendlessUserBackendlessCollection.getCurrentPage().iterator();

                    while (userIterator.hasNext()) {
                        BackendlessUser user = userIterator.next();
                        if (user.getProperty("status").toString().equals("Online")) {
                            count++;

                        }
                    }
                    tvUsersOnline.setText(String.valueOf(count));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(WordMates.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
