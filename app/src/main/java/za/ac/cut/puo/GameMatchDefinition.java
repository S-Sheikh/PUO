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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

import xyz.hanks.library.SmallBang;

public class GameMatchDefinition extends AppCompatActivity {
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
    boolean scoreStreak = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_match_definition);

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
        mSmallBang = SmallBang.attach2Window(GameMatchDefinition.this);

        //init button adapter
        buttonAdapter.add(new WordGameAdapter(btn_ans_bottomLeft));
        buttonAdapter.add(new WordGameAdapter(btn_ans_bottomRight));
        buttonAdapter.add(new WordGameAdapter(btn_ans_topLeft));
        buttonAdapter.add(new WordGameAdapter(btn_ans_topRight));
        //end Init button adpater

        //Word Array BoilerPlate Start
        //English Words
        wordArrayList.add(new Word("eutaxy", "English", "good order or management", "noun"));
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
        wordArrayList.add(new Word("inja", "Xhosa", "a domesticated carnivorous mammal that typically has a long snout", "test"));
        wordArrayList.add(new Word("Dudula", "Xhosa", "an act of exerting force on someone or something", "verb"));
        wordArrayList.add(new Word("umzabalazo", "Xhosa", "a forceful or violent effort to get free of restraint or resist attack", "noun"));
        wordArrayList.add(new Word("thetha", "Xhosa", "conversation; discussion", "noun"));
        //Word Array BoilerPlate End

        randomGenerator = new Random(); // Initialize it lolololol

        populateButtonTxt();

        score = 001;
        answerAttempts = 0;
        scoreMulitplier = 1.00;

        mHandler.sendEmptyMessage(MSG_START_TIMER);
        PUOHelper.setAppBar(this, getResources().getString(R.string.app_name))
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
        if (correctWord.getWord().toString().trim().equals(btn.getText().toString())) {//Absolutley Necessary kek
            mSmallBang.setColors(GameMatchDefinition.this.getResources().getIntArray(R.array.gBangCorrect));
            scoreStreak = true;
        } else {
            mSmallBang.setColors(GameMatchDefinition.this.getResources().getIntArray(R.array.gBangWrong));
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
            tv_multiplier.setText("X " + Double.toString(scoreMulitplier));
            bounce();
        } else {
            scoreMulitplier = 1.00;
            tv_multiplier.setText("X " + Double.toString(scoreMulitplier));
        }
        mSmallBang.bang(v);
        populateButtonTxt();
        attemptCount++;
//        if(attemptCount == 5){
//            this.finish();
//        }else{
        btn_attempts.setText("Words:  " + attemptCount + "/20");
//        }
    }

    //Set Random Buttons with Random(non Repeating) Words
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
                        if (allWords.getWord().equals(randWord.getWord())) { // if the used word has a language thats been used up
                            allWords.setRepeatFlag(true);//set it to true( Used )
                        }
                    }
                    btn.getAnswer().setText(randWord.getWord());
                    btn.getAnswer().setBackgroundColor(getColor());

                    questionArray.add(randWord);
                }
                randWord.setRepeatFlag(true);
            }
        }
        correctWord = makeQuestion();
        btn_question.setText(correctWord.getDefinition());
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
        int index = randomGenerator.nextInt(GameMatchDefinition.this.getResources().getIntArray(R.array.gRandomColors).length);
        int[] colorArray = GameMatchDefinition.this.getResources().getIntArray(R.array.gRandomColors);
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
