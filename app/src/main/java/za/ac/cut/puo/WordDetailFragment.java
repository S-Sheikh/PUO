package za.ac.cut.puo;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.ShareActionProvider;
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
public class WordDetailFragment extends AppCompatDialogFragment implements Toolbar.OnMenuItemClickListener {
    private static Word selectedWord;
    private static Drawable wordImage;
    private static int wordPosition;
    private View wordDetailView;
    private RatingBar wordRatings;
    private Toolbar wordDetailToolbar, wordActionsBar;
    private TextView wordStatus, supporters;
    private ShareActionProvider wordShareAction;
    private ViewGroup mContainer;

    public WordDetailFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment WordDetailFragment.
     */
    public static WordDetailFragment newInstance(Word word, Drawable image, int position) {
        selectedWord = word;
        wordImage = image;
        wordPosition = position;
        return new WordDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        // Inflate the layout for this fragment
        wordDetailView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        ImageView descImage = (ImageView) wordDetailView.findViewById(R.id.word_desc_image);
        TextView wordText = (TextView) wordDetailView.findViewById(R.id.tv_word_text);
        wordStatus = (TextView) wordDetailView.findViewById(R.id.tv_word_status);
        supporters = (TextView) wordDetailView.findViewById(R.id.tv_supporters);
        TextView wordAuthor = (TextView) wordDetailView.findViewById(R.id.tv_word_author);
        TextView wordLexicon = (TextView) wordDetailView.findViewById(R.id.tv_word_lexicon);
        TextView wordDefinition = (TextView) wordDetailView.findViewById(R.id.tv_word_definition);
        TextView wordSentence = (TextView) wordDetailView.findViewById(R.id.tv_word_sentence);
        wordRatings = (RatingBar) wordDetailView.findViewById(R.id.rtb_word_rating);

        wordActionsBar = (Toolbar) wordDetailView.findViewById(R.id.word_actions_toolbar);
        wordDetailToolbar = (Toolbar) wordDetailView.findViewById(R.id.word_detail_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) wordDetailView.findViewById(R.id.collapsing_toolbar);

        descImage.setImageDrawable(wordImage);
        wordText.setText(selectedWord.getWord());
        wordStatus.setText(selectedWord.getStatus());
        wordAuthor.setText(selectedWord.getAuthor());
        wordLexicon.setText(selectedWord.getLexicon());
        wordDefinition.setText(selectedWord.getDefinition());
        wordSentence.setText(selectedWord.getSentence());
        wordRatings.setRating(selectedWord.getRating());
        wordActionsBar.inflateMenu(R.menu.menu_word_actions);
        wordShareAction = (ShareActionProvider) MenuItemCompat
                .getActionProvider(wordActionsBar.getMenu().findItem(R.id.share));
        wordDetailToolbar.inflateMenu(R.menu.menu_word_detail);
        collapsingToolbar.setTitle(selectedWord.getWord());

        /*change text color to green if word is supported.*/
        if (selectedWord.isSupported()) {
            wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.gGreen));
            supporters.setVisibility(View.VISIBLE);
            supporters.setText(String.valueOf(selectedWord.getSupporters()));
        } else {
            wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.colorPrimaryText));
        }
        /*change text color to cayenne if word is blocked.*/
        if (selectedWord.isBlocked())
            wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.Cayenne));

        /**
         * Check if user is Collector and remove menu options accordingly.
         */
        if (Backendless.UserService.CurrentUser().getProperty("role").toString()
                .equalsIgnoreCase("Collector")) {
            wordActionsBar.getMenu().removeItem(R.id.support);
            wordActionsBar.getMenu().removeItem(R.id.block);
            wordDetailToolbar.getMenu().removeItem(R.id.edit_word);
        }

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
                blockWord();
                return true;
            default:
                return true;
        }
    }

    /**
     * Updates a word status to supported.
     */
    public void supportWord() {
        if (PUOHelper.connectionAvailable(getContext())) {

            if (!selectedWord.isBlocked()) {
                if (!selectedWord.isSupported()) {
                    selectedWord.setSupported(true);
                    wordStatus.setText(selectedWord.getStatus());
                    wordStatus.setTextColor(getContext().getResources()
                            .getColor(R.color.gGreen));
                    supporters.setVisibility(View.VISIBLE);
                }
                selectedWord.setSupporters(1);
                supporters.setText(String.valueOf(selectedWord.getSupporters()));
                ((WordListFragment) getTargetFragment())
                        .getmAdapter().notifyItemChanged(wordPosition);
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        Toast.makeText(getContext(), word.getWord() + ": supported!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(), backendlessFault.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getContext(), selectedWord.getWord() +
                        ": is blocked, cannot support!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Updates the word rating.
     */
    public void rateWord() {
        final PopupWindow ratingPopup = PUOHelper.getPopup(getContext(), mContainer);
        final RatingBar ratingBar = (RatingBar) ratingPopup.getContentView().findViewById(R.id.rating_bar);
        Button rate = (Button) ratingPopup.getContentView().findViewById(R.id.btn_rate);
        ratingPopup.showAtLocation(wordDetailView, Gravity.CENTER, 0, 0);
        ratingPopup.update();
        rate.setOnClickListener(v -> {
            if (ratingBar.getRating() > 0) {
                selectedWord.setRating(ratingBar.getRating());
                ((WordListFragment) getTargetFragment())
                        .getmAdapter().notifyItemChanged(wordPosition);
                wordRatings.setRating(selectedWord.getRating());
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(), backendlessFault.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getContext(), "No rating set!", Toast.LENGTH_SHORT).show();
            ratingPopup.dismiss();
        });

    }

    /**
     * Updates a word status to blocked and sets supported to false.
     */
    public void blockWord() {
        if (PUOHelper.connectionAvailable(getContext())) {
            if (!selectedWord.isBlocked()) {
                selectedWord.setBlocked(true);
                selectedWord.setSupported(false);
                wordStatus.setTextColor(getContext().getResources()
                        .getColor(R.color.Cayenne));
                ((WordListFragment) getTargetFragment())
                        .getmAdapter().notifyItemChanged(wordPosition);
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        Toast.makeText(getContext(), selectedWord.getWord() + ": is now blocked!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(), backendlessFault.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getContext(), selectedWord.getWord() + ": is already blocked!",
                        Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();
    }
}
