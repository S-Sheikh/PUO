package za.ac.cut.puo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WordDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {
    private static Word selectedWord;
    private static ImageView wordImage;
    private static int wordPosition;
    private View wordDetailView;
    private RatingBar wordRatings;

    public WordDetailFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment WordDetailFragment.
     */
    public static WordDetailFragment newInstance(Word word, ImageView v, int position) {
        selectedWord = word;
        wordImage = v;
        wordPosition = position;
        return new WordDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        wordDetailView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        ImageView descImage = (ImageView) wordDetailView.findViewById(R.id.word_desc_image);
        TextView wordText = (TextView) wordDetailView.findViewById(R.id.tv_word_text);
        TextView worStatus = (TextView) wordDetailView.findViewById(R.id.tv_word_status);
        TextView wordAuthor = (TextView) wordDetailView.findViewById(R.id.tv_word_author);
        TextView wordLexicon = (TextView) wordDetailView.findViewById(R.id.tv_word_lexicon);
        TextView wordDefinition = (TextView) wordDetailView.findViewById(R.id.tv_word_definition);
        TextView wordSentence = (TextView) wordDetailView.findViewById(R.id.tv_word_sentence);
        wordRatings = (RatingBar) wordDetailView.findViewById(R.id.rtb_word_rating);
        Toolbar wordActionsBar = (Toolbar) wordDetailView.findViewById(R.id.word_actions_toolbar);
        Toolbar wordDetailToolbar = (Toolbar) wordDetailView.findViewById(R.id.word_detail_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) wordDetailView.findViewById(R.id.collapsing_toolbar);

        descImage.setImageDrawable(wordImage.getDrawable());
        wordText.setText(selectedWord.getWord());
        worStatus.setText(selectedWord.getStatus());
        wordAuthor.setText(selectedWord.getAuthor());
        wordLexicon.setText(selectedWord.getLexicon());
        wordDefinition.setText(selectedWord.getDefinition());
        wordSentence.setText(selectedWord.getSentence());
        wordRatings.setRating(selectedWord.getRating());
        wordActionsBar.inflateMenu(R.menu.word_actions_menu);
        wordDetailToolbar.inflateMenu(R.menu.word_detail_menu);
        collapsingToolbar.setTitle(selectedWord.getWord());

        wordActionsBar.setOnMenuItemClickListener(this);
        wordDetailToolbar.setOnMenuItemClickListener(this);

        return wordDetailView;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_word:
                return true;
            case R.id.share:
                return true;
            case R.id.add_to_word_chest:
                PUOHelper.SaveToWordChestTask.getTask(getContext()).execute(selectedWord);
                return true;
            case R.id.rate:
                rateWord();
                return true;
            case R.id.support:
                supportWord();
                return true;
            case R.id.block:
                return true;
            default:
                return true;
        }
    }

    /**
     * Updates a word status to supported.
     */
    public void supportWord() {
        //TODO: update support functionality to allow multiple support
        if (PUOHelper.connectionAvailable(getContext())) {

            if (!selectedWord.isSupported()) {
                selectedWord.setSupported(true);
                WordListFragment.getmAdapter().notifyItemChanged(wordPosition);
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        Toast.makeText(getContext(), word.getWord() + ": is now supported!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(), backendlessFault.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getContext(), selectedWord.getWord() + ": is already supported!",
                        Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();
    }

    public void rateWord() {
        final PopupWindow ratingPopup = PUOHelper.getPopup(getContext(), null);
        final RatingBar ratingBar = (RatingBar) ratingPopup.getContentView().findViewById(R.id.rating_bar);
        Button rate = (Button) ratingPopup.getContentView().findViewById(R.id.btn_rate);
        ratingPopup.showAtLocation(wordDetailView, Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWord.setRating(ratingBar.getRating());
                WordListFragment.getmAdapter().notifyItemChanged(wordPosition);
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Snackbar.make(getView(), backendlessFault.getMessage(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
                ratingPopup.dismiss();
            }
        });

    }
}
