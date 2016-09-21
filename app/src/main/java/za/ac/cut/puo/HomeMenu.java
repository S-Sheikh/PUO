package za.ac.cut.puo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class HomeMenu extends AppCompatActivity {
    Toolbar home_toolBar;
    BackendlessUser user;
    TextView tvUsernameHome, tvUserType, tvWordCount, tvWordInfo;
    ListView lvWords;
    List<Word> words;
    EditText etAddWord, etDefinition, etSentence;
    Spinner spLanguage, spPartOfSpeech;
    ImageView ivAddImage, ivAddSound;
    CircleImageView civ_profile_Pic;
    SpotsDialog progressDialog;
    ProgressBar circularBar;
    AddWordAdapter adapter;
    int sum = 0;
    private SwipeRefreshLayout swipe_refresh_word_list_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);

        home_toolBar = (Toolbar) findViewById(R.id.home_toolBar);
        setSupportActionBar(home_toolBar);

        user = Backendless.UserService.CurrentUser();

        civ_profile_Pic = (CircleImageView) findViewById(R.id.civ_profile_pic);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        tvWordInfo = (TextView) findViewById(R.id.tvWordInfo);

        words = new ArrayList<>();
        adapter = new AddWordAdapter(this, words);
        lvWords = (ListView) findViewById(R.id.lv_words);
        lvWords.setAdapter(adapter);

        loadData();
        countWords();
        refresh();

        PUOHelper.readImage(civ_profile_Pic);

        tvUsernameHome.setText(user.getProperty("name").toString().trim() +
                " " + user.getProperty("surname").toString().trim());
        tvUserType.setText(user.getProperty("role").toString().trim());

        displayWordCount();

        lvWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = words.get(position).getDefinition().trim();
                tvWordInfo.setText(value);
            }
        });

    }

    private void displayWordCount() {
        if (user.getProperty("count").equals(1))
            tvWordCount.setText(user.getProperty("count").toString() + " " + "Word Added");
        else
            tvWordCount.setText(user.getProperty("count").toString() + " " + "Words Added");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_options_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_addWord:
                AddWord();
                return true;
            case R.id.home_menu_profile:
                updateProfileData();
                return true;
            case R.id.word_treasure:
                startActivity(new Intent(HomeMenu.this, WordTreasureActivity.class));
                return true;
            case R.id.word_chest:
                return true;
            case R.id.word_2_word:
                startActivity(new Intent(HomeMenu.this, ChooseNicknameActivity.class));
                return true;
            case R.id.word_mates:
                return true;
            case R.id.word_game:
                startActivity(new Intent(HomeMenu.this, GameHome.class));
                return true;
            case R.id.word_highscore:
                return true;
            case R.id.logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void updateProfileData() {
        Intent intent = new Intent(HomeMenu.this, Update.class);
        intent.putExtra("user", user.getEmail());
        intent.putExtra("objectId", user.getObjectId());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("name", user.getProperty("name").toString().trim());
        intent.putExtra("surname", user.getProperty("surname").toString().trim());
        intent.putExtra("role", user.getProperty("role").toString().trim());
        intent.putExtra("location", user.getProperty("location").toString().trim());
        intent.putExtra("cell", user.getProperty("cell").toString().trim());
        intent.putExtra("isUpdated", user.getProperty("isUpdated").toString().trim());
        startActivity(intent);

    }

    private void refresh() {
              /*Set pull to refresh.*/
        swipe_refresh_word_list_home = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_word_list_home);
        swipe_refresh_word_list_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        swipe_refresh_word_list_home.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryLight);
    }

    public void loadSpecificUserData() {
        circularBar.setVisibility(View.VISIBLE);
        if (words != null) {
            words.clear();
        }

        BackendlessUser user = Backendless.UserService.CurrentUser();
        String whereClause = "email = '" + user.getEmail() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> addWordBackendlessCollection) {
                words = addWordBackendlessCollection.getData();
                AddWordAdapter adapter = new AddWordAdapter(HomeMenu.this, words);
                lvWords.setAdapter(adapter);
                circularBar.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loadData() {
        circularBar.setVisibility(View.VISIBLE);
        if (words != null) {
            words.clear();
        }

        Backendless.Persistence.of(Word.class).find(new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> addWordBackendlessCollection) {
                words.addAll(addWordBackendlessCollection.getData());
                adapter.notifyDataSetChanged();
                circularBar.setVisibility(View.GONE);
                swipe_refresh_word_list_home.setRefreshing(false);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void countWords() {
        String whereClause = "email = '" + user.getEmail() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> userWords) {
                int count = userWords.getTotalObjects();
                user.setProperty("count", count);
                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                    }
                });

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
        sum = 0;
    }

    public void AddWord() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.home_add_word, null);
        etAddWord = (EditText) view.findViewById(R.id.etAddWord);
        etDefinition = (EditText) view.findViewById(R.id.etDefinition);
        etSentence = (EditText) view.findViewById(R.id.etSentence);
        spLanguage = (Spinner) view.findViewById(R.id.spLanguage);
        spPartOfSpeech = (Spinner) view.findViewById(R.id.spPartOfSpeech);
        ivAddImage = (ImageView) view.findViewById(R.id.ivAddImage);
        ivAddSound = (ImageView) view.findViewById(R.id.ivAddSound);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(view);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (PUOHelper.connectionAvailable(getApplicationContext())) {
                    progressDialog = new SpotsDialog(HomeMenu.this, R.style.Custom);
                    progressDialog.show();
                    if (!(etAddWord.getText().toString().trim().isEmpty() || etSentence.getText().toString().trim().isEmpty() ||
                            etDefinition.getText().toString().trim().isEmpty())) {
                        Backendless.Persistence.of(Word.class).find(new AsyncCallback<BackendlessCollection<Word>>() {
                            boolean wordExists = false;

                            @Override
                            public void handleResponse(BackendlessCollection<Word> addWordBackendlessCollection) {
                                words = addWordBackendlessCollection.getData();
                                for (Word word : words) {
                                    if (word.getWord().trim().equalsIgnoreCase(etAddWord.getText().toString().trim())) {
                                        wordExists = true;//word exists in database
                                        Toast.makeText(HomeMenu.this, "Word already exists!Please enter a new word :)", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                                if (!wordExists) {
                                    Word newWord = new Word();
                                    newWord.setName(user.getProperty("name").toString().trim());
                                    newWord.setSurname(user.getProperty("surname").toString().trim());
                                    newWord.setWord(etAddWord.getText().toString().trim());
                                    newWord.setDefinition(etDefinition.getText().toString().trim());
                                    newWord.setSentence(etSentence.getText().toString().trim());
                                    newWord.setLanguage(spLanguage.getSelectedItem().toString().trim());
                                    newWord.setPartOfSpeech(spPartOfSpeech.getSelectedItem().toString().trim());
                                    newWord.setEmail(user.getEmail());
                                    newWord.setCount(newWord.getCount() + 1);
                                    Backendless.Persistence.save(newWord, new AsyncCallback<Word>() {
                                        @Override
                                        public void handleResponse(Word word) {
                                            Toast.makeText(HomeMenu.this, word.getWord() + " saved successfully!", Toast.LENGTH_SHORT).show();
                                            loadData();
                                            countWords();
                                            displayWordCount();
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault backendlessFault) {
                                            Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else

                    {
                        Toast.makeText(HomeMenu.this, "Please fill in all fields!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(HomeMenu.this, "Please connect to the internet!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    public void logout() {
        progressDialog = new SpotsDialog(HomeMenu.this, R.style.Custom);
        progressDialog.show();
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                Toast.makeText(HomeMenu.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                HomeMenu.this.finish();
                //startActivity(new Intent(HomeMenu.this, Login.class));
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
