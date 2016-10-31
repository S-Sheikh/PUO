package za.ac.cut.puo;

import android.widget.Button;

/**
 * Created by Shahbaaz Sheikh on 13/09/2016.
 */
public class WordGameAdapter {
    Button Answer;
    boolean flag;
    int btnColor;

    public WordGameAdapter(Button Answer) {
        this.Answer = Answer;
        flag = false;
    }

    public Button getAnswer() {
        return Answer;
    }

    public void setAnswer(Button answer) {
        Answer = answer;
    }

    public int getBtnColor() {
        return btnColor;
    }

    public void setBtnColor(int btnColor) {
        this.btnColor = btnColor;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
