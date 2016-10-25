package za.ac.cut.puo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class HomeMenu extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    final int PAGE_SIZE = 5;
    Toolbar home_toolBar;
    BackendlessUser user;
    TextView tvUsernameHome, tvUserType, tvWordCount, tvWordInfo,tvAddSound;
    ListView lvWords;
    List<Word> words;
    EditText etAddWord, etDefinition, etSentence;
    Spinner spLanguage, spPartOfSpeech;
    ImageView ivAddImage, ivAddSound, wordImg;
    Button btnPlayClip, btnRecord;
    CircleImageView civ_profile_Pic;
    SpotsDialog progressDialog;
    ProgressBar circularBar;
    AddWordAdapter adapter;
    int sum = 0, pictureCount = 0;
    QueryOptions queryOptions = new QueryOptions();
    Bitmap bitmap;
    private SwipeRefreshLayout swipe_refresh_word_list_home;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    byte[] soundBytes ;
    String currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);

        home_toolBar = (Toolbar) findViewById(R.id.home_toolBar);
        setSupportActionBar(home_toolBar);

        user = Backendless.UserService.CurrentUser();
        btnPlayClip = (Button) findViewById(R.id.btnPlayClip);
        btnRecord = (Button) findViewById(R.id.btnRecord);
        civ_profile_Pic = (CircleImageView) findViewById(R.id.civ_profile_pic);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        tvWordInfo = (TextView) findViewById(R.id.tvWordInfo);
        lvWords = (ListView) findViewById(R.id.lv_words);
        tvAddSound = (TextView)findViewById(R.id.tvAddSound);
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
    protected void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

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
                refresh();
                return true;
            case R.id.home_menu_profile:
                updateProfileData();
                return true;
            case R.id.word_treasure:
                startActivity(new Intent(HomeMenu.this, WordTreasureActivity.class));
                return true;
            case R.id.word_chest:
                startActivity(new Intent(HomeMenu.this, WordChestActivity.class));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imageview.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imageview.setImageURI(selectedImage);
                }
                break;
        }
    }*/
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivAddImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        /*if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(HomeMenu.this.getContentResolver(), uri);
                    ivAddImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }*/
    }

    public void updateProfileData() {
        Intent intent = new Intent(HomeMenu.this, Update.class);
        intent.putExtra("user", user.getEmail());
        intent.putExtra("objectId", user.getObjectId());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("name", user.getProperty("name").toString().trim());
        intent.putExtra("surname", user.getProperty("surname").toString().trim());
        intent.putExtra("username", user.getProperty("username").toString().trim());
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
                countWords();
                displayWordCount();
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
    }


    public void RecordAudio(View view) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    btnRecord.setText("Stop");
                } else {
                    btnRecord.setText("Record");
                }
                mStartRecording = !mStartRecording;
    }

    public void PlayAudio(View view){
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    btnPlayClip.setText("Stop");
                } else {
                    btnPlayClip.setText("Play");
                }
                mStartPlaying = !mStartPlaying;
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
                btnRecord = (Button) view.findViewById(R.id.btnRecord);
                btnPlayClip = (Button) view.findViewById(R.id.btnPlayClip);
                //ivAddSound = (ImageView) view.findViewById(R.id.ivAddSound);
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
                                            final Word newWord = new Word();
                                            newWord.setName(user.getProperty("name").toString().trim());
                                            newWord.setSurname(user.getProperty("surname").toString().trim());
                                            newWord.setWord(etAddWord.getText().toString().trim());
                                            newWord.setDefinition(etDefinition.getText().toString().trim());
                                            newWord.setSentence(etSentence.getText().toString().trim());
                                            newWord.setLanguage(spLanguage.getSelectedItem().toString().trim());
                                            newWord.setPartOfSpeech(spPartOfSpeech.getSelectedItem().toString().trim());
                                            newWord.setEmail(user.getEmail());
                                            newWord.setCount(newWord.getCount() + 1);
                                            String filename = etAddWord.getText().toString().trim() + "_.png";
                                            final String voiceClipFIleName = "_AUD" + currentDateTime + "_.3gp";
                                            newWord.setPronunciation(voiceClipFIleName);
                                            newWord.setImageLocation(filename);
                                            newWord.setEmail(user.getEmail());
                                            if (bitmap != null) {
                                                Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG,
                                                        100, filename, "WordPictures", true,
                                                        new AsyncCallback<BackendlessFile>() {
                                                            @Override
                                                            public void handleResponse(BackendlessFile backendlessFile) {
                                                                Backendless.Files.saveFile("Voice_Clips", voiceClipFIleName, soundClipBytes(), new AsyncCallback<String>() {
                                                                    @Override
                                                                    public void handleResponse(String s) {
                                                                        //
                                                                    }
                                                                    @Override
                                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                                        Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                                        progressDialog.dismiss();
                                                                    }
                                                                });
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

                                                            @Override
                                                            public void handleFault(BackendlessFault backendlessFault) {
                                                                Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                            } else {
                                                Backendless.Files.saveFile("Voice_Clips", mFileName, soundClipBytes(), new AsyncCallback<String>() {
                                                    @Override
                                                    public void handleResponse(String s) {
                                                        //
                                                    }
                                                    @Override
                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                        Toast.makeText(HomeMenu.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });
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

            private void onRecord(boolean start) {
                if (start) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }

            private void onPlay(boolean start) {
                if (start) {
                    startPlaying();
                } else {
                    stopPlaying();
                }
            }

            private void startPlaying() {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }

            private void stopPlaying() {
                mPlayer.release();
                mPlayer = null;
            }

            private void startRecording() {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(mFileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }

                mRecorder.start();
            }

            private void stopRecording() {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }

    public byte[] soundClipBytes(){
        byte[] soundBytes = null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(mFileName)));
            soundBytes = new byte[inputStream.available()];

            soundBytes = toByteArray(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }
        return soundBytes;
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

    /**
     * Click on View to change photo. Sets into View of your layout, android:onClick="clickOnPhoto"
     * @param view View
     */
    public void clickOnPhoto(View view) {
        try{
            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_GET_CONTENT);

            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra
                    (
                            Intent.EXTRA_INITIAL_INTENTS,
                            new Intent[] { takePhotoIntent }
                    );
            startActivityForResult(chooserIntent, REQUEST_CODE_CHOOSE_PHOTO);
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }
    public void Photo(View view){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_CHOOSE_PHOTO);
    }
    public HomeMenu() {
        mFileName = Environment.getExternalStorageDirectory().toString();
        mFileName += "/_AUD_" + currentDateTime + "_.3gp";
    }
}