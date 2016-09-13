package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import xyz.hanks.library.SmallBang;

public class GameWhatLanguage extends AppCompatActivity {
    Button btn_question, btn_ans_topLeft, btn_ans_topRight, btn_ans_bottomLeft, btn_ans_bottomRight;
    private SmallBang mSmallBang;
    Word correctWord;
    ArrayList<Word> wordArrayList = new ArrayList<>();
    ArrayList<Word> questionArray = new ArrayList<>();
    ArrayList<WordGameAdapter> buttonAdapter = new ArrayList<>();
    private Random randomGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_what_language);
        btn_question = (Button) findViewById(R.id.btn_question);
        btn_ans_topLeft = (Button) findViewById(R.id.btn_ans_topLeft);
        btn_ans_topRight = (Button) findViewById(R.id.btn_ans_topRight);
        btn_ans_bottomLeft = (Button) findViewById(R.id.btn_ans_bottomLeft);
        btn_ans_bottomRight = (Button) findViewById(R.id.btn_ans_bottomRight);
        //Init small bang
        mSmallBang = SmallBang.attach2Window(GameWhatLanguage.this);

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
        wordArrayList.add(new Word("inja", "Xhosa", "a domesticated carnivorous mammal that typically has a long snout", "noun"));
        wordArrayList.add(new Word("Dudula", "Xhosa", "an act of exerting force on someone or something", "verb"));
        wordArrayList.add(new Word("umzabalazo", "Xhosa", "a forceful or violent effort to get free of restraint or resist attack", "noun"));
        wordArrayList.add(new Word("thetha", "Xhosa", "conversation; discussion", "noun"));
        //Word Array BoilerPlate End

        randomGenerator = new Random(); // Initialize it lolololol

        populateButtonTxt();
    }

    public void onBang(View v) {
        Button btn = (Button)v;
        if(correctWord.getWord().toString().trim().equals(btn.getText().toString())){//Absolutley Necessary kek
            Toast.makeText(GameWhatLanguage.this, "Good For you !!!", Toast.LENGTH_SHORT).show();
            mSmallBang.setColors(GameWhatLanguage.this.getResources().getIntArray(R.array.gBangCorrect));
        }else{
            Toast.makeText(GameWhatLanguage.this, "Please KYS", Toast.LENGTH_SHORT).show();
            mSmallBang.setColors(GameWhatLanguage.this.getResources().getIntArray(R.array.gBangWrong));
        }
        mSmallBang.bang(v);
       populateButtonTxt();
    }

    //Set Random Buttons with Random(non Repeating words) Words
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
                while (randWord.repeatFlag) {//if it's true , it has been used
                    randWord = anyWord(); // get another word
                }
                if (randWord.repeatFlag == false) {
                    for (Word allWords : wordArrayList) { // Iterate through all words in list
                        if (allWords.getLanguage().equals(randWord.getLanguage())) { // if the used word has a language thats been used up
                            allWords.setRepeatFlag(true);//set it to true( Used )
                        }
                    }
                    btn.Answer.setText(randWord.getWord());
                    questionArray.add(randWord);
                }
                randWord.setRepeatFlag(true);
            }
        }
        correctWord = makeQuestion();
        btn_question.setText(correctWord.getLanguage());
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
}