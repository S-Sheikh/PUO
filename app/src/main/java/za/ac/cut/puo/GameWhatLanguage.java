package za.ac.cut.puo;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import xyz.hanks.library.SmallBang;

public class GameWhatLanguage extends AppCompatActivity {
    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int REFRESH_RATE = 100;
    Button btn_question, btn_ans_topLeft, btn_ans_topRight, btn_ans_bottomLeft, btn_ans_bottomRight, btn_circleScore, btn_timer, btn_attempts;
    TextView tv_multiplier;
    Stopwatch timer = new Stopwatch();
    Word correctWord;
    Vibrator vibe;
    ArrayList<Word> wordArrayList = new ArrayList<>();
    ArrayList<Word> questionArray = new ArrayList<>();
    ArrayList<WordGameAdapter> buttonAdapter = new ArrayList<>();
    ObjectAnimator animY;
    double score;
    double scoreMulitplier;
    int answerAttempts;
    boolean scoreStreak =false;
            SpotsDialog
    progressDialog;
    BackendlessUser user;
    GameHighScores newScore;
    int attemptCount = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    if (timer.getElapsedTimeSecs() >= 0 && timer.getElapsedTimeSecs() <= 9) {
                        btn_timer.setText("Time: " + timer.getElapsedTimeMin() + ":0" + timer.getElapsedTimeSecs());
                    } else {
                        btn_timer.setText("Time: " + timer.getElapsedTimeMin() + ":" + timer.getElapsedTimeSecs());
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    timer.stop();//stop timer
                    if (timer.getElapsedTimeSecs() >= 0 && timer.getElapsedTimeSecs() <= 9) {
                        btn_timer.setText("Time: " + timer.getElapsedTimeMin() + ":0" + timer.getElapsedTimeSecs());
                    } else {
                        btn_timer.setText("Time: " + timer.getElapsedTimeMin() + ":" + timer.getElapsedTimeSecs());
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private SmallBang mSmallBang;
    private Random randomGenerator;
    List<Word> words;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_what_language);
        btn_question = (Button) findViewById(R.id.btn_question);
        btn_ans_topLeft = (Button) findViewById(R.id.btn_ans_topLeft);
        btn_ans_topRight = (Button) findViewById(R.id.btn_ans_topRight);
        btn_ans_bottomLeft = (Button) findViewById(R.id.btn_ans_bottomLeft);
        btn_ans_bottomRight = (Button) findViewById(R.id.btn_ans_bottomRight);
        btn_circleScore = (Button) findViewById(R.id.btn_circleScore);
        btn_timer = (Button) findViewById(R.id.btn_timer);
        btn_attempts = (Button) findViewById(R.id.btn_attempts);
        tv_multiplier = (TextView) findViewById(R.id.tv_multiplier);
        //Init small bang
        mSmallBang = SmallBang.attach2Window(GameWhatLanguage.this);

        //init button adapter
        buttonAdapter.add(new WordGameAdapter(btn_ans_bottomLeft));
        buttonAdapter.add(new WordGameAdapter(btn_ans_bottomRight));
        buttonAdapter.add(new WordGameAdapter(btn_ans_topLeft));
        buttonAdapter.add(new WordGameAdapter(btn_ans_topRight));
        //end Init button adpater
        user = Backendless.UserService.CurrentUser();
        newScore = new GameHighScores();
        //Word Array BoilerPlate Start
        //English Words
        wordArrayList.add(new Word("some", "English", "not all, but not none", "determiner"));
        wordArrayList.add(new Word("dabster", "English", "Slang. an expert", "noun"));
        wordArrayList.add(new Word("pulverulent", "English", "covered with dust or powder.", "adjective"));
        wordArrayList.add(new Word("vilipend", "English", "to vilify; depreciate", "verb"));

        //Afrikaans Words
        wordArrayList.add(new Word("vark", "Afrikaans", "an omnivorous domesticated hoofed mammal", "noun"));
        wordArrayList.add(new Word("evalueer", "Afrikaans", "evaluate or estimate the nature, ability, or quality of", "verb"));
        wordArrayList.add(new Word("piek", "Afrikaans", "a thin, pointed piece of metal, wood, or another rigid material", "noun"));
        wordArrayList.add(new Word("golf", "Afrikaans", "a gesture or signal made by moving one's hand to and fro", "noun"));

        //Zulu Words
        wordArrayList.add(new Word("ukuzibulala", "Zulu", "the action of killing oneself intentionally", "noun"));
        wordArrayList.add(new Word("bazizwa", "Zulu", "experience (an emotion or sensation)", "verb"));
        wordArrayList.add(new Word("kuzwakale", "Zulu", "in good condition; not damaged, injured, or diseased", "adjective"));
        wordArrayList.add(new Word("ingadi", "Zulu", "a piece of ground, often near a house, used for growing flowers, fruit, or vegetablesy", "noun"));

        //Xhosa Words
        wordArrayList.add(new Word("inja", "Xhosa", "a domesticated carnivorous mammal that typically has a long snout", "noun"));
        wordArrayList.add(new Word("Dudula", "Xhosa", "an act of exerting force on someone or something", "verb"));
        wordArrayList.add(new Word("umzabalazo", "Xhosa", "a forceful or violent effort to get free of restraint or resist attack", "noun"));
        wordArrayList.add(new Word("thetha", "Xhosa", "conversation; discussion", "noun"));
        //Word Array BoilerPlate End
        GameWhatLanguage.this.progressDialog = new SpotsDialog(GameWhatLanguage.this, R.style.Custom);
        GameWhatLanguage.this.progressDialog.show();
        int PAGESIZE = 100;
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setPageSize(PAGESIZE);
        if(PUOHelper.connectionAvailable(GameWhatLanguage.this)){
            Backendless.Data.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
                @Override
                public void handleResponse(BackendlessCollection<Word> wordBackendlessCollection) {
                    words = wordBackendlessCollection.getData();
                    wordArrayList.clear();
                    for (Word word : words) {
                        wordArrayList.add(new Word(word.getWord(), word.getLanguage(), word.getDefinition(), word.getPartOfSpeech()));
                    }
                    mHandler.sendEmptyMessage(MSG_START_TIMER);
                    newScore.setStartDate(java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                    GameWhatLanguage.this.progressDialog.dismiss();

                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(GameWhatLanguage.this, "Error, Could not get words from Internet", Toast.LENGTH_SHORT).show();
                    GameWhatLanguage.this.progressDialog.dismiss();

                }
            });
        }else{
            Toast.makeText(this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            GameWhatLanguage.this.finish();
        }
        randomGenerator = new Random(); // getTask it lolololol


        populateButtonTxt();
        score = 001;
        answerAttempts = 0;
        scoreMulitplier = 1.00;

        PUOHelper.initAppBar(this, getResources().getString(R.string.app_name))
                .setDisplayHomeAsUpEnabled(true);

        btn_question.setHorizontallyScrolling(true);
        btn_question.setMaxLines(2);
        btn_question.setEllipsize(TextUtils.TruncateAt.END);
        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void onBang(View v) {
        Button btn = (Button) v;
        //If Answer is correct
        if (correctWord.getLanguage().toString().trim().equals(btn.getText().toString())) {//Absolutley Necessary kek
            mSmallBang.setColors(GameWhatLanguage.this.getResources().getIntArray(R.array.gBangCorrect));
            scoreStreak = true;
        } else {
            mSmallBang.setColors(GameWhatLanguage.this.getResources().getIntArray(R.array.gBangWrong));
            vibe.vibrate(50); // 50 ms
            scoreStreak = false;
        }
        if (scoreStreak) {
            score += 1 * scoreMulitplier;
            switch (Double.toString(round(scoreMulitplier, 2))) {
                case "1.0":
                    scoreMulitplier = 1.05;
                    break;
                case "1.05":
                    scoreMulitplier = 1.20;
                    break;
                case "1.2":
                    scoreMulitplier = 1.45;
                    break;
                case "1.45":
                    scoreMulitplier = 1.80;
                    break;
                case "1.8":
                    scoreMulitplier = 2.25;
                    break;
                case "2.25":
                    scoreMulitplier = 2.80;
                    break;
                case "2.8":
                    scoreMulitplier = 3.00;
                    break;
                default:
                    scoreMulitplier = 3.00;
            }
            btn_circleScore.setText(Double.toString(round(score, 2)));
            tv_multiplier.setText("+ " + Double.toString(scoreMulitplier));
            bounce();
        } else {
            scoreMulitplier = 1.00;
            tv_multiplier.setText("+ " + Double.toString(scoreMulitplier));
        }
        mSmallBang.bang(v);
        populateButtonTxt();
        attemptCount++;
//        if(attemptCount == 5){
//            this.finish();
//        }else{
        btn_attempts.setText("Words:  " + attemptCount + "/20");
//        }
        if (attemptCount == 20) {
            newScore.setScore(Double.parseDouble(btn_circleScore.getText().toString()));
            newScore.setType("Language");
            newScore.setUserName(user.getProperty("name") + " " + user.getProperty("surname"));
            newScore.setUserMail(user.getEmail());

            Backendless.Persistence.save(newScore, new AsyncCallback<GameHighScores>() {
                @Override
                public void handleResponse(GameHighScores gameHighScores) {
                    Toast.makeText(GameWhatLanguage.this, newScore.getUserName() + " Score Submitted!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(GameWhatLanguage.this, backendlessFault.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            GameWhatLanguage.this.finish();
        }
    }

    //Set Random Buttons with Random(non Repeating words) Words
    public void populateButtonTxt() {
        //ReSet all Flags to False
        for (Word word : wordArrayList) {
            word.setRepeatFlag(false);
        }
        for (WordGameAdapter btn : buttonAdapter) {
            btn.setFlag(false);
        }
        //End Reset all Flags to/ False

        questionArray.clear();//resets question array

        for (WordGameAdapter btn : buttonAdapter) {
            if (!btn.isFlag()) {// if it's true, it has already been given a Word
                btn.setFlag(true);
                Word randWord = anyWord();
                while (randWord.isRepeatFlag()) {//if it's true , it has been used
                    randWord = anyWord(); // get another word
                }
                if (randWord.isRepeatFlag() == false) {
                    for (Word allWords : wordArrayList) { // Iterate through all words in list
                        if (allWords.getLanguage().equals(randWord.getLanguage())) { // if the used word has a language that's been used up
                            allWords.setRepeatFlag(true);//set it to true( Used )
                        }
                    }
                    btn.getAnswer().setText(randWord.getLanguage());
                    btn.getAnswer().setBackgroundColor(getColor());

                    questionArray.add(randWord);
                }
                randWord.setRepeatFlag(true);
            }
        }
        correctWord = makeQuestion();
        btn_question.setText(correctWord.getWord());
    }

    public Word anyWord() {
        int index = randomGenerator.nextInt(wordArrayList.size());
        Word randItem = wordArrayList.get(index);
        return randItem;
    }

    public Word makeQuestion() {
        int index = randomGenerator.nextInt(questionArray.size());
        Word randItem = questionArray.get(index);
        return randItem;
    }


    public void bounce() {
        animY = ObjectAnimator.ofFloat(btn_circleScore, "translationY", -100f, 0f);
        animY.setDuration(700);//0.7sec
        animY.setInterpolator(new BounceInterpolator());
        animY.setRepeatCount(0);
        animY.start();
    }

    public int getColor() {
        int index = randomGenerator.nextInt(GameWhatLanguage.this.getResources().getIntArray(R.array.gRandomColors).length);
        int[] colorArray = GameWhatLanguage.this.getResources().getIntArray(R.array.gRandomColors);
        int randItem = colorArray[index];
        return randItem;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}