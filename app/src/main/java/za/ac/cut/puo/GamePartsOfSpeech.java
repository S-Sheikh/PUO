package za.ac.cut.puo;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;

import xyz.hanks.library.SmallBang;

public class GamePartsOfSpeech extends AppCompatActivity {
    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int REFRESH_RATE = 100;
    Button btn_question, btn_ans_topLeft, btn_ans_topRight, btn_ans_bottomLeft, btn_ans_bottomRight, btn_circleScore, btn_timer;
    Stopwatch timer = new Stopwatch();
    Word correctWord;
    ArrayList<Word> wordArrayList = new ArrayList<>();
    ArrayList<Word> questionArray = new ArrayList<>();
    ArrayList<WordGameAdapter> buttonAdapter = new ArrayList<>();
    ObjectAnimator animY;
    int score;
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
        setContentView(R.layout.game_parts_of_speech);
        btn_question = (Button) findViewById(R.id.btn_question);
        btn_ans_topLeft = (Button) findViewById(R.id.btn_ans_topLeft);
        btn_ans_topRight = (Button) findViewById(R.id.btn_ans_topRight);
        btn_ans_bottomLeft = (Button) findViewById(R.id.btn_ans_bottomLeft);
        btn_ans_bottomRight = (Button) findViewById(R.id.btn_ans_bottomRight);
        btn_circleScore = (Button)findViewById(R.id.btn_circleScore);
        btn_timer = (Button) findViewById(R.id.btn_timer);
        //Init small bang
        mSmallBang = SmallBang.attach2Window(GamePartsOfSpeech.this);

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
        mHandler.sendEmptyMessage(MSG_START_TIMER);

//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void onBang(View v) {
        Button btn = (Button)v;
        //If Answer is correct
        if(correctWord.getPartOfSpeech().toString().trim().equals(btn.getText().toString())){//Absolutley Necessary kek
            mSmallBang.setColors(GamePartsOfSpeech.this.getResources().getIntArray(R.array.gBangCorrect));
            btn_circleScore.setText(Integer.toString(score++));
            bounce();
        }else{
            mSmallBang.setColors(GamePartsOfSpeech.this.getResources().getIntArray(R.array.gBangWrong));
        }
        mSmallBang.bang(v);
        populateButtonTxt();
    }

    public void populateButtonTxt() {
        //ReSet all Flags to False
        for(Word word : wordArrayList){
            word.setRepeatFlag(false);
        }
        for(WordGameAdapter btn : buttonAdapter){
            btn.setFlag(false);
        }
        //End Reset all Flags to/ False

        questionArray.clear();//resets question array

        for (WordGameAdapter btn : buttonAdapter) {
            if (!btn.flag) {// if it's true, it has already been given a Word
                btn.setFlag(true);
                Word randWord = anyWord();
                while (randWord.isRepeatFlag()) {//if it's true , it has been used
                    randWord = anyWord(); // get another word
                }
                if (randWord.isRepeatFlag() == false) {
                    for (Word allWords : wordArrayList) { // Iterate through all words in list
                        if (allWords.getPartOfSpeech().equals(randWord.getPartOfSpeech())) { // if the used word has a language thats been used up
                            allWords.setRepeatFlag(true);//set it to true( Used )
                        }
                    }
                    btn.Answer.setText(randWord.getPartOfSpeech());
                    btn.Answer.setBackgroundColor(getColor());
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

    public Word makeQuestion(){
        int index = randomGenerator.nextInt(questionArray.size());
        Word randItem = questionArray.get(index);
        return randItem;
    }

    public void bounce(){
        animY = ObjectAnimator.ofFloat(btn_circleScore, "translationY", -100f, 0f);
        animY.setDuration(700);//0.7sec
        animY.setInterpolator(new BounceInterpolator());
        animY.setRepeatCount(0);
        animY.start();
    }

    public int getColor() {
        int index = randomGenerator.nextInt(GamePartsOfSpeech.this.getResources().getIntArray(R.array.gRandomColors).length);
        int[] colorArray = GamePartsOfSpeech.this.getResources().getIntArray(R.array.gRandomColors);
        int randItem = colorArray[index];
        return randItem;
    }

}
