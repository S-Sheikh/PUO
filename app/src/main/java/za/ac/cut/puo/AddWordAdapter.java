package za.ac.cut.puo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddWordAdapter extends ArrayAdapter<Word> {
    private final Context context;
    private final List<Word> values;
    private TextView tv_word_title, tv_word_status, tv_word_author, tvLanguage, supporters;
    private ImageView wordImg, iv_ic_popup, ivBgSupporters;
    private RatingBar wordRating;

    public AddWordAdapter(Context context, List<Word> list) {
        super(context, R.layout.word_list_item, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.word_list_item, parent, false);
        iv_ic_popup = (ImageView) convertView.findViewById(R.id.iv_ic_popup_menu);
        wordImg = (CircleImageView) convertView.findViewById(R.id.civ_word_pic);
        tv_word_title = (TextView) convertView.findViewById(R.id.tv_word_text);
        tv_word_status = (TextView) convertView.findViewById(R.id.tv_word_status);
        tv_word_author = (TextView) convertView.findViewById(R.id.tv_word_author);
        tvLanguage = (TextView) convertView.findViewById(R.id.tv_word_lexicon);
        supporters = (TextView) convertView.findViewById(R.id.tv_supporters);
        ivBgSupporters = (ImageView) convertView.findViewById(R.id.iv_bg_supporters);
        wordRating = (RatingBar) convertView.findViewById(R.id.rtb_word_rating);

        tv_word_title.setText(values.get(position).getWord());
        tvLanguage.setText(values.get(position).getLexicon());
        tv_word_status.setText(values.get(position).getStatus());
        tv_word_author.setText(values.get(position).getAuthor());
        wordRating.setRating(values.get(position).getRating());
        String imageUri = Defaults.WORD_IMAGE_BASE_URL + values.get(position).getImageLocation();
        Picasso.with(context).load(imageUri).fit()
                .placeholder(PUOHelper.getTextDrawable(values.get(position)))
                .error(PUOHelper.getTextDrawable(values.get(position)))
                .into(wordImg);

        //change text color to green in word is supported.
        if (values.get(position).isSupported()) {
            tv_word_status.setTextColor(getContext().getResources()
                    .getColor(R.color.gLime));
            supporters.setVisibility(View.VISIBLE);
            ivBgSupporters.setVisibility(View.VISIBLE);
            supporters.setText(String.valueOf(values.get(position).getSupporters()));
        } else {
            tv_word_status.setTextColor(getContext().getResources()
                    .getColor(R.color.colorPrimaryText));
        }

        return convertView;
    }
}
