package za.ac.cut.puo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeMenu extends AppCompatActivity {
    Toolbar home_toolBar;
    TextView tvUsernameHome, tvUserType, tvWordCount, tvWordInfo;
    ListView lvWords;
    List<AddWord> words;
    EditText etAddWord, etDefinition, etSentence;
    Spinner spLanguage, spPartOfSpeech;
    ImageView ivAddImage, ivAddSound;
    SpotsDialog progressDialog;
    ProgressBar circularBar;
    CardView cv_word_list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);
        home_toolBar = (Toolbar)findViewById(R.id.home_toolBar);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        tvWordInfo = (TextView) findViewById(R.id.tvWordInfo);
        lvWords = (ListView) findViewById(R.id.lv_words);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        setSupportActionBar(home_toolBar);
        home_toolBar.setTitleTextColor(getResources().getColor(R.color.colorIcons));
        BackendlessUser user = Backendless.UserService.CurrentUser();
        tvUsernameHome.setText(user.getProperty("name").toString().trim() + " " + user.getProperty("surname").toString().trim());
        tvUserType.setText(user.getProperty("role").toString().trim());
        loadData();
        lvWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvWords.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                String value = words.get(position).getDefinition().trim();
                tvWordInfo.setText(value);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_options_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
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
    protected void onResume() {
        loadData();
        super.onResume();
    }

    public void updateProfileData() {

        BackendlessUser user = Backendless.UserService.CurrentUser();
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

    public void loadSpecificUserData() {
        circularBar.setVisibility(View.VISIBLE);
        if (words != null) {
            words.clear();
        }

        BackendlessUser user = Backendless.UserService.CurrentUser();
        String whereClause = "email = '" + user.getEmail() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(AddWord.class).find(dataQuery, new AsyncCallback<BackendlessCollection<AddWord>>() {
            @Override
            public void handleResponse(BackendlessCollection<AddWord> addWordBackendlessCollection) {
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

        Backendless.Persistence.of(AddWord.class).find(new AsyncCallback<BackendlessCollection<AddWord>>() {
            @Override
            public void handleResponse(BackendlessCollection<AddWord> addWordBackendlessCollection) {
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

    public void AddWord() {
        loadData();
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

                if (connectionAvailable()) {
                    progressDialog = new SpotsDialog(HomeMenu.this, R.style.Custom);
                    progressDialog.show();
                    if (!(etAddWord.getText().toString().trim().isEmpty() || etSentence.getText().toString().trim().isEmpty() ||
                            etDefinition.getText().toString().trim().isEmpty())) {
                        Backendless.Persistence.of(AddWord.class).find(new AsyncCallback<BackendlessCollection<AddWord>>() {
                            boolean wordExists = false;
                            @Override
                            public void handleResponse(BackendlessCollection<AddWord> addWordBackendlessCollection) {
                                words = addWordBackendlessCollection.getData();
                                for (int i = 0; i < words.size(); i++) {
                                    if (words.get(i).getWord().trim().equalsIgnoreCase(etAddWord.getText().toString().trim())) {
                                        wordExists = true;//word exists in database
                                        Toast.makeText(HomeMenu.this, "Word already exists!Please enter a new word :)", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        break;

                                    } else {
                                        wordExists = false;//word does not exist in database
                                    }
                                }
                                if (wordExists == false) {
                                    BackendlessUser user = Backendless.UserService.CurrentUser();
                                    AddWord word = new AddWord();
                                    word.setName(user.getProperty("name").toString().trim());
                                    word.setSurname(user.getProperty("surname").toString().trim());
                                    word.setWord(etAddWord.getText().toString().trim());
                                    word.setDefinition(etDefinition.getText().toString().trim());
                                    word.setSentence(etSentence.getText().toString().trim());
                                    word.setLanguage(spLanguage.getSelectedItem().toString().trim());
                                    word.setPartOfSpeech(spPartOfSpeech.getSelectedItem().toString().trim());
                                    word.setCount(word.getCount() + 1);
                                    Backendless.Persistence.save(word, new AsyncCallback<AddWord>() {
                                        @Override
                                        public void handleResponse(AddWord word) {
                                            Toast.makeText(HomeMenu.this, word.getWord() + " saved successfully!", Toast.LENGTH_SHORT).show();
                                            loadData();
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault backendlessFault) {
                                            Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                                /**if(wordExists == true){
                                 Toast.makeText(HomeMenu.this, "Word already exists!Please enter a new word :)", Toast.LENGTH_SHORT).show();
                                 progressDialog.dismiss();
                                 }else if(wordExists == false){
                                 AddWord word = new AddWord();
                                 word.setWord(etAddWord.getText().toString().trim());
                                 word.setDefinition(etDefinition.getText().toString().trim());
                                 word.setSentence(etSentence.getText().toString().trim());
                                 word.setLanguage(spLanguage.getSelectedItem().toString().trim());
                                 word.setPartOfSpeech(spPartOfSpeech.getSelectedItem().toString().trim());
                                 word.setCount(word.getCount() + 1);
                                 Backendless.Persistence.save(word, new AsyncCallback<AddWord>() {
                                @Override public void handleResponse(AddWord word) {
                                Toast.makeText(HomeMenu.this, word.getWord() + " saved successfully!", Toast.LENGTH_SHORT).show();
                                loadData();
                                progressDialog.dismiss();
                                }
                                @Override public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                }
                                });
                                 }**/
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    } else {
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
    private boolean connectionAvailable(){
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {//if true,connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = true;//connected to using wifi
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                connected = true;//connected using mobile data
            }
        } else {
            connected = false;//no internet connection
        }
        return connected;
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
                startActivity(new Intent(HomeMenu.this, Login.class));
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
