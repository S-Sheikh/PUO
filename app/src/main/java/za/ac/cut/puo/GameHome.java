package za.ac.cut.puo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class GameHome extends AppCompatActivity {
    private ImageView ivWhatLanguage;
    private ImageView ivPartsOfSpeech;
    private ImageView ivMatchDefinition;
    private ImageView ivFillInBlanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_home);

        PUOHelper.initAppBar(this, getResources().getString(R.string.app_name))
                .setDisplayHomeAsUpEnabled(true);

        ivWhatLanguage = (ImageView) findViewById(R.id.iv_whatLanguage);
        ivPartsOfSpeech = (ImageView) findViewById(R.id.iv_partsOfSpeech);
        ivMatchDefinition = (ImageView) findViewById(R.id.iv_matchDefinition);
        ivFillInBlanks = (ImageView) findViewById(R.id.iv_fillInBlanks);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_partsOfSpeech:
                Intent intent = new Intent(GameHome.this, GamePartsOfSpeech.class);
                startActivity(intent);
                break;
            case R.id.iv_whatLanguage:
                startActivity(new Intent(GameHome.this, GameWhatLanguage.class));
                break;
            case R.id.iv_fillInBlanks:
                startActivity(new Intent(GameHome.this, GameFillInTheBlank.class));
                break;
            case R.id.iv_matchDefinition:
                startActivity(new Intent(GameHome.this, GameMatchDefinition.class));
                break;
            default:
                break;
        }
    }
}
