package za.ac.cut.puo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hodielisrael on 2016/09/13.
 */

public class WordListItemAdapter extends RecyclerView.Adapter<WordListItemAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView civ_word_image;
        public TextView tv_word_title, tv_word_status,tv_word_author, tv_word_lexicon;
        public RatingBar rtb_word_rating;
        public ImageView iv_popup_menu_overflow;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public WordListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(WordListItemAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
