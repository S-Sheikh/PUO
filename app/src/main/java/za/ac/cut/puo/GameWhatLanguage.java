package za.ac.cut.puo;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameWhatLanguage extends AppCompatActivity {
    Button btn_question, btn_ans_topLeft, btn_ans_topRight, btn_ans_bottomLeft, btn_ans_bottomRight;
    Random rand;
    boolean randomColorFlagOne, randomColorFlagTwo, randomColorFlagThree, randomColorFlagFour;
    ArrayList<Integer> blocks = new ArrayList<>();
    GradientDrawable bgShape;
    int[] abcArray = {0, 1, 2, 3, 4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_what_language);
        btn_question = (Button) findViewById(R.id.btn_question);
        btn_ans_topLeft = (Button) findViewById(R.id.btn_ans_topLeft);
        btn_ans_topRight = (Button) findViewById(R.id.btn_ans_topRight);
        btn_ans_bottomLeft = (Button) findViewById(R.id.btn_ans_bottomLeft);
        btn_ans_bottomRight = (Button) findViewById(R.id.btn_ans_bottomRight);
        for (int i : abcArray) {
            blocks.add(i);
        }

    }

    public void btn_quesion(View v) {


    }

    public void mixColors(View v) {
        rand = new Random();
        int n = rand.nextInt(5); // Gives n such that 0 <= n < 20
        for (int i = 0; i < blocks.size(); i++) {
            int element = blocks.get(i);

            switch (element) {
                case 0:
                    bgShape = (GradientDrawable) btn_question.getBackground();

                    n = rand.nextInt(5);
                    switch (n){
                        case 0:
                            bgShape.setColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 1:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 2:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 3:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonTwo));
                            break;
                        case 4:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonOne));
                            break;
                        default:
                            break;
                    }
                    break;
                case 1:
                    bgShape = (GradientDrawable) btn_ans_topRight.getBackground();

                    n = rand.nextInt(5);
                    switch (n){
                        case 0:
                            bgShape.setColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 1:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 2:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 3:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonTwo));
                            break;
                        case 4:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonOne));
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    bgShape = (GradientDrawable) btn_ans_topLeft.getBackground();

                    n = rand.nextInt(5);
                    switch (n){
                        case 0:
                            bgShape.setColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 1:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 2:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 3:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonTwo));
                            break;
                        case 4:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonOne));
                            break;
                        default:
                            break;
                    }
                    break;
                case 3:
                    bgShape = (GradientDrawable)btn_ans_bottomRight.getBackground();

                    n = rand.nextInt(5);
                    switch (n){
                        case 0:
                            bgShape.setColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 1:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 2:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 3:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonTwo));
                            break;
                        case 4:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonOne));
                            break;
                        default:
                            break;
                    }
                    break;
                case 4:
                    bgShape = (GradientDrawable) btn_ans_bottomLeft.getBackground();

                    n = rand.nextInt(5);
                    switch (n){
                        case 0:
                            bgShape.setColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 1:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 2:
                            bgShape.setColor(getResources().getColor(R.color.gColorPoints));
                            break;
                        case 3:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonTwo));
                            break;
                        case 4:
                            bgShape.setColor(getResources().getColor(R.color.gColorButtonOne));
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    Toast.makeText(GameWhatLanguage.this, "Toasty!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
