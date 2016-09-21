package za.ac.cut.puo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//TODO: Complete this at home @Watley
public class WordPicturesAdapter extends ArrayAdapter<WordPictures> {
    private final Context context;
    private final List<WordPictures> values;
    ImageView wordImg;

    public WordPicturesAdapter(Context context, List<WordPictures> list) {
        super(context, R.layout.word_list_item, list);
        this.context = context;
        this.values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.word_list_item, parent, false);
        wordImg = (CircleImageView) convertView.findViewById(R.id.civ_word_pic);
        String imageUri = "https://api.backendless.com/D200A885-7EED-CB51-FFAC-228F87E55D00/v1/files/WordPictures/" + values.get(position).getImageLocation();
        Picasso.with(context).load(imageUri).fit().into(wordImg);
        System.out.print(imageUri);
        return convertView;
    }
}
