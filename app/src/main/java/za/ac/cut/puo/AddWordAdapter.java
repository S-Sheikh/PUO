package za.ac.cut.puo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AddWordAdapter extends ArrayAdapter<AddWord> {
    private final Context context;
    private final List<AddWord> values;
    TextView tv_word_title, tv_word_status, tv_word_author, tvLanguage;

    public AddWordAdapter(Context context, List<AddWord> list) {
        super(context, R.layout.home_word_row, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.home_word_row, parent, false);
        tv_word_title = (TextView) rowView.findViewById(R.id.tvWord);
        tv_word_status = (TextView) rowView.findViewById(R.id.tvStatus);
        tv_word_author = (TextView) rowView.findViewById(R.id.tvAuthor);
        tvLanguage = (TextView) rowView.findViewById(R.id.tvLanguage);
        tv_word_title.setText(values.get(position).getWord());
        tvLanguage.setText(values.get(position).getLanguage());
        if (values.get(position).isSupported()) {
            tv_word_status.setText(getContext().getString(R.string.supported));
        } else {
            tv_word_status.setText(getContext().getString(R.string.not_supported));
        }
        tv_word_author.setText(values.get(position).getName() + " " + values.get(position).getSurname());

        return rowView;
    }
}
