package za.ac.cut.puo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class HomeMenu extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    final int PAGE_SIZE = 100;
    Toolbar home_toolBar;
    TextView tvUsernameHome, tvUserType, tvWordCount, tvWordInfo;
    ListView lvWords;
    List<Word> words;
    EditText etAddWord, etDefinition, etSentence;
    Spinner spLanguage, spPartOfSpeech;
    ImageView ivAddImage, ivAddSound;
    CircleImageView civ_profile_Pic;
    SpotsDialog progressDialog;
    ProgressBar circularBar;
    int sum = 0;
    QueryOptions queryOptions = new QueryOptions();
    Bitmap bitmap;
    private SwipeRefreshLayout swipe_refresh_word_list_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);
        home_toolBar = (Toolbar) findViewById(R.id.home_toolBar);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        tvWordInfo = (TextView) findViewById(R.id.tvWordInfo);
        lvWords = (ListView) findViewById(R.id.lv_words);
        civ_profile_Pic = (CircleImageView) findViewById(R.id.civ_profile_pic);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        setSupportActionBar(home_toolBar);
        loadData();
        countWordWithPaging();
        refresh();
        PUOHelper.getImageOnline(new DownloadTask(civ_profile_Pic));
        PUOHelper.readImage(civ_profile_Pic);
        BackendlessUser user = Backendless.UserService.CurrentUser();
        tvUsernameHome.setText(user.getProperty("name").toString().trim() + " " + user.getProperty("surname").toString().trim());
        tvUserType.setText(user.getProperty("role").toString().trim());
        tvWordCount.setText(user.getProperty("count").toString() + " " + "Words Added");
        lvWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        switch (item.getItemId()) {
            case R.id.home_menu_addWord:
                AddWord();
                refresh();
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(HomeMenu.this.getContentResolver(), uri);
                    ivAddImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(HomeMenu.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

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

    private void refresh() {
              /*Set pull to refresh.*/
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        swipe_refresh_word_list_home = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_word_list_home);
        swipe_refresh_word_list_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                countWordWithPaging();
                tvWordCount.setText(user.getProperty("count").toString() + " " + "Words Added");
            }
        });
        swipe_refresh_word_list_home.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryLight);
    }

    public void loadData() {
        circularBar.setVisibility(View.VISIBLE);
        if (words != null) {
            words.clear();
        }

        Backendless.Persistence.of(Word.class).find(new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> addWordBackendlessCollection) {
                words = addWordBackendlessCollection.getData();
                AddWordAdapter adapter = new AddWordAdapter(HomeMenu.this, words);
                lvWords.setAdapter(adapter);
                circularBar.setVisibility(View.GONE);
                swipe_refresh_word_list_home.setRefreshing(false);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void countWordWithPaging() {
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        queryOptions = new QueryOptions();
        queryOptions.setPageSize(PAGE_SIZE);
        queryOptions.setOffset(0);
        dataQuery.setQueryOptions(queryOptions);
        Backendless.Data.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> wordBackendlessCollection) {
                if (wordBackendlessCollection.getCurrentPage().size() > PAGE_SIZE) {
                    while (wordBackendlessCollection.getCurrentPage().size() > 0) {
                        int count = 0;
                        List<Word> words_ = wordBackendlessCollection.nextPage().getCurrentPage();
                        for (Word word : words_) {
                            if (user.getEmail().equals(word.getEmail())) {
                                count = word.getCount();
                                sum += count;
                            }
                        }
                        user.setProperty("count", String.valueOf(sum));
                        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                            }
                        });
                    }
                } else {
                    int count = 0;
                    List<Word> words_ = wordBackendlessCollection.getCurrentPage();
                    for (Word word : words_) {
                        if (user.getEmail().equals(word.getEmail())) {
                            count = word.getCount();
                            sum += count;
                        }
                    }
                    user.setProperty("count", String.valueOf(sum));
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
        sum = 0;
    }

    public void ivAddImageClicked(View view) {
        AddImage();
    }

    public void AddImage() {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePhotoIntent.setType("image/*");
        if (choosePhotoIntent.resolveActivity(HomeMenu.this.getPackageManager()) != null) {
            choosePhotoIntent.putExtra("imageUri", choosePhotoIntent.getData());
            startActivityForResult(choosePhotoIntent, REQUEST_CODE_CHOOSE_PHOTO);
        }
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

                if (connectionAvailable()) {
                    progressDialog = new SpotsDialog(HomeMenu.this, R.style.Custom);
                    progressDialog.show();
                    if (!(etAddWord.getText().toString().trim().isEmpty() || etSentence.getText().toString().trim().isEmpty() ||
                            etDefinition.getText().toString().trim().isEmpty())) {
                        Backendless.Persistence.of(Word.class).find(new AsyncCallback<BackendlessCollection<Word>>() {
                            boolean wordExists = false;
                            @Override
                            public void handleResponse(BackendlessCollection<Word> addWordBackendlessCollection) {
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
                                    BackendlessUser user_ = Backendless.UserService.CurrentUser();
                                    final Word word = new Word();
                                    word.setName(user_.getProperty("name").toString().trim());
                                    word.setSurname(user_.getProperty("surname").toString().trim());
                                    word.setWord(etAddWord.getText().toString().trim());
                                    word.setDefinition(etDefinition.getText().toString().trim());
                                    word.setSentence(etSentence.getText().toString().trim());
                                    word.setLanguage(spLanguage.getSelectedItem().toString().trim());
                                    word.setPartOfSpeech(spPartOfSpeech.getSelectedItem().toString().trim());
                                    word.setEmail(user_.getEmail());
                                    word.setCount(word.getCount() + 1);
                                    final BackendlessUser user = Backendless.UserService.CurrentUser();
                                    String filename = etAddWord.getText().toString().trim() + "_.png";
                                    word.setImageLocation(filename);
                                    word.setEmail(user.getEmail());
                                    Backendless.Files.Android.upload(bitmap,
                                            Bitmap.CompressFormat.PNG,
                                            100,
                                            filename,
                                            "WordPictures",
                                            true,
                                            new AsyncCallback<BackendlessFile>() {
                                                @Override
                                                public void handleResponse(BackendlessFile backendlessFile) {
                                                    Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                                                        @Override
                                                        public void handleResponse(Word word) {

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
                                    Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                                        @Override
                                        public void handleResponse(Word word) {
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

    private boolean connectionAvailable() {
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
