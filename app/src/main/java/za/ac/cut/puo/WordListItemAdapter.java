package za.ac.cut.puo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hodielisrael on 2016/09/13.
 */

public class WordListItemAdapter extends RecyclerView.Adapter<WordListItemAdapter.ViewHolder> {

    private static OnItemClickListener onItemClickCallBack;
    private List<Word> mWords;
    private Context mContext;

    public WordListItemAdapter(List<Word> mWords, Context mContext) {
        this.mWords = mWords;
        this.mContext = mContext;
    }

    /**
     * Allows the parent activity or fragment to define the listener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickCallBack = listener;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CircleImageView wordDescImg;
        public TextView wordText, wordStatus, wordAuthor, wordLexicon, supporters;
        public RatingBar wordRating;
        public ImageView icPopupMenu, ivBgSupporters;


        public ViewHolder(View itemView) {
            super(itemView);
            wordDescImg = (CircleImageView) itemView.findViewById(R.id.civ_word_pic);
            wordText = (TextView) itemView.findViewById(R.id.tv_word_text);
            wordStatus = (TextView) itemView.findViewById(R.id.tv_word_status);
            supporters = (TextView) itemView.findViewById(R.id.tv_supporters);
            wordAuthor = (TextView) itemView.findViewById(R.id.tv_word_author);
            wordLexicon = (TextView) itemView.findViewById(R.id.tv_word_lexicon);
            wordRating = (RatingBar) itemView.findViewById(R.id.rtb_word_rating);
            ivBgSupporters = (ImageView) itemView.findViewById(R.id.iv_bg_supporters);
            icPopupMenu = (ImageView) itemView.findViewById(R.id.iv_ic_popup_menu);

            itemView.setOnClickListener(this);
            icPopupMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickCallBack != null) {
                if (v instanceof ImageView) {
                    onItemClickCallBack.onOverflowClicked((ImageView) v);
                } else {
                    onItemClickCallBack.onItemClicked(v);
                }
            }
        }
    }

    @Override
    public WordListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom word_list_item layout
        View wordListItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_item, parent, false);

        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(WordListItemAdapter.ViewHolder holder, int position) {
        Word word = mWords.get(position);

        //change text color to green in word is supported.
        if (word.isSupported()&&!word.isBlocked()) {
            holder.wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.gLime));
            holder.supporters.setVisibility(View.VISIBLE);
            holder.ivBgSupporters.setVisibility(View.VISIBLE);
            holder.supporters.setText(String.valueOf(word.getSupporters()));
        } else {
            holder.wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.colorPrimaryText));
        }
        if (word.isBlocked())
            holder.wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.Cayenne));

        //Set the word properties on the views.
        holder.wordText.setText(word.getWord());
        holder.wordStatus.setText(word.getStatus());
        holder.wordAuthor.setText(word.getAuthor());
        holder.wordLexicon.setText(word.getLexicon());
        holder.wordRating.setRating(word.getRating());
        String imageUri = Defaults.WORD_IMAGE_BASE_URL + word.getImageLocation();
        Picasso.with(getContext()).load(imageUri)
                .resize(300, 300)
                .centerInside()
                .placeholder(PUOHelper.getTextDrawable(word))
                .error(PUOHelper.getTextDrawable(word))
                .into(holder.wordDescImg);
    }

    @Override
    public int getItemCount() {
        return mWords.size();
    }

    /*Define the listener interface*/
    interface OnItemClickListener {
        void onItemClicked(View itemView);

        void onOverflowClicked(ImageView v);
    }
}
