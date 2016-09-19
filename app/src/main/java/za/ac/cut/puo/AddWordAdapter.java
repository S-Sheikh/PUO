package za.ac.cut.puo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddWordAdapter extends ArrayAdapter<Word> {
    private final Context context;
    private final List<Word> values;
    TextView tv_word_title, tv_word_status, tv_word_author, tvLanguage;
    ImageView wordDescImg, iv_ic_popup;

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
        iv_ic_popup.setVisibility(View.GONE);
        wordDescImg = (CircleImageView) convertView.findViewById(R.id.civ_word_pic);
        tv_word_title = (TextView) convertView.findViewById(R.id.tv_word_text);
        tv_word_status = (TextView) convertView.findViewById(R.id.tv_word_status);
        tv_word_author = (TextView) convertView.findViewById(R.id.tv_word_author);
        tvLanguage = (TextView) convertView.findViewById(R.id.tv_word_lexicon);

        tv_word_title.setText(values.get(position).getWord());
        tvLanguage.setText(values.get(position).getLexicon());
        tv_word_status.setText(values.get(position).getStatus());
        tv_word_author.setText(values.get(position).getAuthor());

        //change text color to green in word is supported.
        if (values.get(position).isSupported()) {
            tv_word_status.setTextColor(getContext().getResources()
                    .getColor(R.color.gColorTime));
        }
        return convertView;
    }
}
