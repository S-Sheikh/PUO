package za.ac.cut.puo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shahbaaz Sheikh on 26/10/2016.
 */

public class HighScoreItemAdapter extends ArrayAdapter<GameHighScores> {
    private final Context context;
    private final List<GameHighScores> values;
    TextView tv_highName, tv_highTime, btn_circleScore;
    ImageView wordImg;

    public HighScoreItemAdapter(Context context, List<GameHighScores> list) {
        super(context, R.layout.high_score_list_item, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.word_list_item, parent, false);

        wordImg = (CircleImageView) convertView.findViewById(R.id.civ_word_pic);
        tv_highName = (TextView) convertView.findViewById(R.id.tv_highName);
        tv_highTime = (TextView) convertView.findViewById(R.id.tv_highTime);
        btn_circleScore = (Button)convertView.findViewById(R.id.btn_circleScore);
        tv_highName.setText(values.get(position).getUserName());
        tv_highTime.setText((CharSequence) values.get(position).getCreated());//TODO: Fix conversion
        btn_circleScore.setText(String.valueOf(values.get(position).getScore()));
        //download the image from backendless;
       // String imageUri = "https://api.backendless.com/D200A885-7EED-CB51-FFAC-228F87E55D00/v1/files/WordPictures/" + values.get(position).getImageLocation();

        //Image Stuff

//        if (!(imageUri.equals("")))
//            Picasso.with(context).load(imageUri).fit()
//                    .placeholder(PUOHelper.getTextDrawable(values.get(position)))
//                    .error(PUOHelper.getTextDrawable(values.get(position)))
//                    .into(wordImg);
//        System.out.println(imageUri);// for debugging purposes

        //Image Stuff


        //change text color to green in word is supported.

        return convertView;
    }
}
