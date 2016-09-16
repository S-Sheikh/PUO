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
        View rowView = inflater.inflate(R.layout.word_list_item, parent, false);
        iv_ic_popup = (ImageView) rowView.findViewById(R.id.iv_ic_popup_menu);
        iv_ic_popup.setVisibility(View.GONE);
        wordDescImg = (CircleImageView) rowView.findViewById(R.id.civ_word_pic);
        tv_word_title = (TextView) rowView.findViewById(R.id.tv_word_text);
        tv_word_status = (TextView) rowView.findViewById(R.id.tv_word_status);
        tv_word_author = (TextView) rowView.findViewById(R.id.tv_word_author);
        tvLanguage = (TextView) rowView.findViewById(R.id.tv_word_lexicon);
        tv_word_title.setText(values.get(position).getWord());
        tvLanguage.setText(values.get(position).getLexicon());
        tv_word_status.setText(values.get(position).getStatus());
        tv_word_author.setText(values.get(position).getAuthor());
        return rowView;
    }
}
