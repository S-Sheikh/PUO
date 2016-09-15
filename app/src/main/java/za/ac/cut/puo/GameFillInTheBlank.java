package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GameFillInTheBlank extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fill_in_the_blank);
        PUOHelper.setAppBar(this,getResources().getString(R.string.app_name));
    }

}
