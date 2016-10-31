package za.ac.cut.puo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WordDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {
    private static Word selectedWord;
    private static ImageView wordImage;
    private static Toolbar wordActionsBar;

    public WordDetailFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment WordDetailFragment.
     */
    public static WordDetailFragment newInstance(Word word, ImageView v) {
        selectedWord = word;
        wordImage = v;
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
        View wordDetailView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        ImageView descImage = (ImageView) wordDetailView.findViewById(R.id.word_desc_image);
        TextView wordText = (TextView) wordDetailView.findViewById(R.id.tv_word_text);
        TextView worStatus = (TextView) wordDetailView.findViewById(R.id.tv_word_status);
        TextView wordAuthor = (TextView) wordDetailView.findViewById(R.id.tv_word_author);
        TextView wordLexicon = (TextView) wordDetailView.findViewById(R.id.tv_word_lexicon);
        TextView wordDefinition = (TextView) wordDetailView.findViewById(R.id.tv_word_definition);
        TextView wordSentence = (TextView) wordDetailView.findViewById(R.id.tv_word_sentence);
        wordActionsBar = (Toolbar) wordDetailView.findViewById(R.id.word_actions_toolbar);
        Toolbar wordDetailToolbar = (Toolbar) wordDetailView.findViewById(R.id.word_detail_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) wordDetailView.findViewById(R.id.collapsing_toolbar);

        descImage.setImageDrawable(wordImage.getDrawable());
        wordText.setText(selectedWord.getWord());
        worStatus.setText(selectedWord.getStatus());
        wordAuthor.setText(selectedWord.getAuthor());
        wordLexicon.setText(selectedWord.getLexicon());
        wordDefinition.setText(selectedWord.getDefinition());
        wordSentence.setText(selectedWord.getSentence());
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
                return true;
            case R.id.support:
                return true;
            case R.id.block:
                return true;
            default:
                return true;
        }
    }

}
