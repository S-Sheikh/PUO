package za.ac.cut.puo;

import android.widget.Button;

/**
 * Created by Shahbaaz Sheikh on 13/09/2016.
 */
public class WordGameAdapter {
    Button Question;
    Button Answer;
    boolean flag;

    public WordGameAdapter(Button Answer){
        this.Answer = Answer;
        flag = false;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
