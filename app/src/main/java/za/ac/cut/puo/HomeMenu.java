package za.ac.cut.puo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeMenu extends AppCompatActivity {
    Toolbar home_toolBar;
    TextView tvUsernameHome, tvUserType, tvWordCount;
    ListView lvWords;
    List<Word> words;
    EditText etAddWord, etDefinition, etSentence;
    Spinner spLanguage, spPartOfSpeech;
    ImageView ivAddImage, ivAddSound;
    SpotsDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);
        home_toolBar = (Toolbar)findViewById(R.id.home_toolBar);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        lvWords = (ListView) findViewById(R.id.lv_words);
        setSupportActionBar(home_toolBar);
        BackendlessUser user = Backendless.UserService.CurrentUser();
        tvUsernameHome.setText(user.getProperty("name").toString().trim() + " " + user.getProperty("surname").toString().trim());
        tvUserType.setText(user.getProperty("role").toString().trim());
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
                //go to profile
                updateProfileData();
                return true;
            case R.id.word_treasure:
                return true;
            case R.id.word_chest:
                return true;
            case R.id.word_2_word:
                return true;
            case R.id.word_mates:
                return true;
            case R.id.word_game:
                return true;
            case R.id.word_highscore:
                return true;
            case R.id.logout:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        //loadData();
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

    /**
     * public void loadData(){
     * if(words != null){
     * words.clear();
     * }
     * BackendlessUser user = Backendless.UserService.CurrentUser();
     * BackendlessDataQuery dataQuery = new BackendlessDataQuery();
     * dataQuery.setWhereClause("user = '" + user.getEmail() + "'");
     * Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
     *
     * @Override public void handleResponse(BackendlessCollection<Word> wordBackendlessCollection) {
     * words = wordBackendlessCollection.getData();
     * WordHomeAdapter wordHomeAdapter = new WordHomeAdapter(HomeMenu.this,words);
     * lvWords.setAdapter(wordHomeAdapter);
     * <p/>
     * }
     * @Override public void handleFault(BackendlessFault backendlessFault) {
     * Toast.makeText(HomeMenu.this,backendlessFault.getMessage(),Toast.LENGTH_SHORT).show();
     * }
     * });
     * <p/>
     * }
     **/
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
                progressDialog = new SpotsDialog(HomeMenu.this, R.style.Custom);
                BackendlessUser user = Backendless.UserService.CurrentUser();
                if (connectionAvailable()) {
                    if (!(etAddWord.getText().toString().trim().isEmpty() || etSentence.getText().toString().trim().isEmpty() ||
                            etDefinition.getText().toString().trim().isEmpty())) {
                        progressDialog.show();
                        Word word = new Word();
                        word.setEmail(user.getEmail());
                        word.setObjectId(user.getObjectId());
                        word.setWord(etAddWord.getText().toString().trim());
                        word.setDefinition(etDefinition.getText().toString().trim());
                        word.setSentence(etSentence.getText().toString().trim());
                        if (spLanguage.getSelectedItemPosition() < 0) {
                            word.setLanguage(spLanguage.getSelectedItem().toString().trim());
                        } else if (spPartOfSpeech.getSelectedItemPosition() < 0) {
                            word.setPartOfSpeech(spPartOfSpeech.getSelectedItem().toString().trim());
                        }
                        Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                            @Override
                            public void handleResponse(Word word) {
                                Toast.makeText(HomeMenu.this, word.getWord() + " saved successfully!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(HomeMenu.this, "Please fill in all fields!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(HomeMenu.this, "Please connect to the internet!", Toast.LENGTH_SHORT).show();

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
}
