package za.ac.cut.puo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class HomeScreen extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    public static final int REQUEST_CODE_CAPTURE = 2;
    public static final int REQUEST_CODE__SELECT_AUDIO = 3;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    final int PAGE_SIZE = 5;
    Toolbar home_toolBar;
    BackendlessUser user;
    TextView tvUsernameHome, tvUserType, tvWordCount, tvWordInfo, tvAddSound, tvPlayClip;
    ListView lvWords;
    List<Word> words;
    EditText etAddWord, etDefinition, etSentence;
    Spinner spLanguage, spPartOfSpeech;
    ImageView ivAddImage, ivBrowse, ivPlayClip, ivRecord;
    CircleImageView civ_profile_Pic;
    SpotsDialog progressDialog;
    ProgressBar circularBar;
    AddWordAdapter adapter;
    int sum = 0, pictureCount = 0;
    QueryOptions queryOptions = new QueryOptions();
    Bitmap bitmap;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    byte[] soundBytes;
    String currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String audioPath = null;
    boolean ivRecordclicked = false;
    boolean ivSelectAudioclicked = false;
    Boolean supported = false;
    int count = 0;
    private SwipeRefreshLayout swipe_refresh_word_list_home;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private AdView mAdView;

    public HomeScreen() {
        mFileName = Environment.getExternalStorageDirectory().toString();
        mFileName += "/_AUD_" + currentDateTime + "_.3gp";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);
        mAdView = (AdView) findViewById(R.id.AdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        home_toolBar = (Toolbar) findViewById(R.id.home_toolBar);
        setSupportActionBar(home_toolBar);

        user = Backendless.UserService.CurrentUser();
        civ_profile_Pic = (CircleImageView) findViewById(R.id.civ_profile_pic);
        ivAddImage = (ImageView) findViewById(R.id.ivAddImage);
        circularBar = (ProgressBar) findViewById(R.id.progressBarCircular);
        tvUsernameHome = (TextView) findViewById(R.id.tvUsernameHome);
        tvUserType = (TextView) findViewById(R.id.tvUserType);
        tvWordCount = (TextView) findViewById(R.id.tvWordCount);
        tvWordInfo = (TextView) findViewById(R.id.tvWordInfo);
        lvWords = (ListView) findViewById(R.id.lv_words);
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

    //TODO:DONT FORGET TO IMPLEMENT RUN-TIME PERMISSIONS FOR CAMERA AND MIC!!!!!!!!
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

        if (mAdView != null) {
            mAdView.pause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
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
                startActivity(new Intent(HomeScreen.this, WordTreasureActivity.class));
                return true;
            case R.id.word_chest:
                startActivity(new Intent(HomeScreen.this, WordChestActivity.class));
                return true;
            case R.id.word_mates:
                startActivity(new Intent(HomeScreen.this, WordMates.class));
                return true;
            case R.id.word_game:
                startActivity(new Intent(HomeScreen.this, GameHome.class));
                return true;
            case R.id.word_highscore:
                startActivity(new Intent(HomeScreen.this, GameHighScoresActivity.class));
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

        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ivAddImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    ivAddImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE__SELECT_AUDIO) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if ((data != null) && (data.getData() != null)) {
                    Uri audioFileUri = data.getData();
                    audioPath = getRealPathFromURI(audioFileUri);
                    startPlaying(audioPath);
                    Toast.makeText(this, audioPath, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void updateProfileData() {
        Intent intent = new Intent(HomeScreen.this, Update.class);
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

        String whereClause = "status = '" + "Supported" + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setPageSize(5);
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> addWordBackendlessCollection) {
                words = addWordBackendlessCollection.getData();
                for (Word temp : words) {
                    if (count < 5) {
                        supported = true;
                    }
                    count++;
                }

                if (supported) {
                    AddWordAdapter adapter = new AddWordAdapter(HomeScreen.this, words);
                    lvWords.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    circularBar.setVisibility(View.GONE);
                    swipe_refresh_word_list_home.setRefreshing(false);
                }

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ImageChooser() {
        AlertDialog.Builder getImageFrom = new AlertDialog.Builder(this);
        getImageFrom.setIcon(getResources().getDrawable(R.drawable.ic_add_image));
        getImageFrom.setTitle("Select an Option:");
        final CharSequence[] opsChars = {"Take a Picture", "Open Gallery"};
        getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CODE_CAPTURE);
                } else if (which == 1) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Choose Gallery"), REQUEST_CODE_CHOOSE_PHOTO);
                }
                dialog.dismiss();
            }
        });
        getImageFrom.show();
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
        ivRecordclicked = true;
        onRecord(mStartRecording);
        if (mStartRecording) {
            //Stop timer
            ivRecord.setImageResource(0);
            ivRecord.setImageResource(R.drawable.ic_mic_off);
            tvAddSound.setText("Stop");
        } else {
            //Show and start timer
            ivRecord.setImageResource(0);
            ivRecord.setImageResource(R.drawable.ic_mic);
            tvAddSound.setText("Record");
        }
        mStartRecording = !mStartRecording;
    }

    public void PlayAudio(View view) {
        try {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                ivPlayClip.setImageResource(0);
                ivPlayClip.setImageResource(R.drawable.ic_stop);
                tvPlayClip.setText("Stop");
            } else {
                ivPlayClip.setImageResource(0);
                ivPlayClip.setImageResource(R.drawable.ic_play);
                tvPlayClip.setText("Play Clip");
            }
            mStartPlaying = !mStartPlaying;
        } catch (Exception e) {
            //Toast.makeText(this, "Please Record or Select Audio :)", Toast.LENGTH_LONG).show();
        }
    }

//    public void AddImage(View view) {
//        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        choosePhotoIntent.setType("image/*");
//        if (choosePhotoIntent.resolveActivity(this.getPackageManager()) != null) {
//            choosePhotoIntent.putExtra("imageUri", choosePhotoIntent.getData());
//            startActivityForResult(choosePhotoIntent, REQUEST_CODE_CHOOSE_PHOTO);
//        }
//    }

//    public void CaptureImage(View view) {
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, REQUEST_CODE_CAPTURE);
//    }

    public void AddWord() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.home_add_word, null);
        etAddWord = (EditText) view.findViewById(R.id.etAddWord);
        etDefinition = (EditText) view.findViewById(R.id.etDefinition);
        etSentence = (EditText) view.findViewById(R.id.etSentence);
        spLanguage = (Spinner) view.findViewById(R.id.spLanguage);
        spPartOfSpeech = (Spinner) view.findViewById(R.id.spPartOfSpeech);
        ivAddImage = (ImageView) view.findViewById(R.id.ivAddImage);
        ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageChooser();
            }
        });
        ivPlayClip = (ImageView) view.findViewById(R.id.ivPlayClip);
        tvAddSound = (TextView) view.findViewById(R.id.tvAddSound);
        tvPlayClip = (TextView) view.findViewById(R.id.tvPlayClip);
        ivBrowse = (ImageView) view.findViewById(R.id.ivBrowse);
        ivPlayClip = (ImageView) view.findViewById(R.id.ivPlayClip);
        ivRecord = (ImageView) view.findViewById(R.id.ivRecord);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(view);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (PUOHelper.connectionAvailable(getApplicationContext())) {
                    progressDialog = new SpotsDialog(HomeScreen.this, R.style.Custom);
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
                                        Toast.makeText(HomeScreen.this, "Word already exists!Please enter a new word :)", Toast.LENGTH_SHORT).show();
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
                                    final String selectedAudioFileName = audioPath;
                                    newWord.setPronunciation(voiceClipFIleName);
                                    newWord.setImageLocation(filename);
                                    newWord.setEmail(user.getEmail());
                                    if (bitmap != null) {
                                        Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG,
                                                100, filename, "WordPictures", true,
                                                new AsyncCallback<BackendlessFile>() {
                                                    @Override
                                                    public void handleResponse(BackendlessFile backendlessFile) {
                                                        //Upload recorded audio
                                                        Backendless.Files.saveFile("Voice_Clips", voiceClipFIleName, soundClipBytes(), new AsyncCallback<String>() {
                                                            @Override
                                                            public void handleResponse(String s) {
                                                                //
                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault backendlessFault) {
                                                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                        //Upload Selected Audio:
                                                        Backendless.Files.saveFile("Selected Audio", selectedAudioFileName, AudioBytes(), new AsyncCallback<String>() {
                                                            @Override
                                                            public void handleResponse(String s) {
                                                                //
                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault backendlessFault) {
                                                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                        Backendless.Persistence.save(newWord, new AsyncCallback<Word>() {
                                                            @Override
                                                            public void handleResponse(Word word) {
                                                                Toast.makeText(HomeScreen.this, word.getWord() + " saved successfully!", Toast.LENGTH_SHORT).show();
                                                                loadData();
                                                                countWords();
                                                                displayWordCount();
                                                                progressDialog.dismiss();
                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault backendlessFault) {
                                                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                        Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });

                                    } else {
                                        //Upload Recorded Audio;
                                        Backendless.Files.saveFile("Voice_Clips", mFileName, soundClipBytes(), new AsyncCallback<String>() {
                                            @Override
                                            public void handleResponse(String s) {
                                                //
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault backendlessFault) {
                                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                        //Upload Selected Audio:
                                        Backendless.Files.saveFile("Selected Audio", selectedAudioFileName, AudioBytes(), new AsyncCallback<String>() {
                                            @Override
                                            public void handleResponse(String s) {
                                                //
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault backendlessFault) {
                                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Backendless.Persistence.save(newWord, new AsyncCallback<Word>() {
                                            @Override
                                            public void handleResponse(Word word) {
                                                Toast.makeText(HomeScreen.this, word.getWord() + " saved successfully!", Toast.LENGTH_SHORT).show();
                                                loadData();
                                                countWords();
                                                displayWordCount();
                                                progressDialog.dismiss();
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault backendlessFault) {
                                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(HomeScreen.this, "Please fill in all fields!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(HomeScreen.this, "Please connect to the internet!", Toast.LENGTH_SHORT).show();
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
        progressDialog = new SpotsDialog(HomeScreen.this, R.style.Custom);
        progressDialog.show();
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                user.setProperty("status", "Offline");
                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        //
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //
                    }
                });
                Toast.makeText(HomeScreen.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                HomeScreen.this.finish();
                //startActivity(new Intent(HomeScreen.this, Login.class));
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(HomeScreen.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
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
            startPlaying(mFileName);
            startPlaying(audioPath);
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(String filename) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filename);
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

    public byte[] soundClipBytes() {
        byte[] soundBytes = null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(mFileName)));
            soundBytes = new byte[inputStream.available()];

            soundBytes = toByteArray(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soundBytes;
    }

    public byte[] AudioBytes() {
        byte[] soundBytes = null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(audioPath)));
            soundBytes = new byte[inputStream.available()];

            soundBytes = toByteArray(inputStream);
        } catch (Exception e) {
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
                out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public void SelectAudio(View view) {
        ivSelectAudioclicked = true;
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select Audio:"), REQUEST_CODE__SELECT_AUDIO);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(HomeScreen.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}