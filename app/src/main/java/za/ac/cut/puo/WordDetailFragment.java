package za.ac.cut.puo;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WordDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailFragment extends DialogFragment {
    private static Word selectedWord;
    private static ImageView wordImage;
    private static WordListItemAdapter.ViewHolder viewHolder;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View wordDetailView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        ImageView descImage = (ImageView) wordDetailView.findViewById(R.id.word_desc_image);
        TextView wordText = (TextView)wordDetailView.findViewById(R.id.tv_word_text);
        TextView worStatus = (TextView)wordDetailView.findViewById(R.id.tv_word_status);
        TextView wordAuthor = (TextView)wordDetailView.findViewById(R.id.tv_word_author);
        TextView wordLexicon = (TextView)wordDetailView.findViewById(R.id.tv_word_lexicon);
        TextView wordDefinition = (TextView)wordDetailView.findViewById(R.id.tv_word_definition);
        TextView wordSentence = (TextView)wordDetailView.findViewById(R.id.tv_word_sentence);

        descImage.setImageDrawable(wordImage.getDrawable());
        wordText.setText(selectedWord.getWord());
        worStatus.setText(selectedWord.getStatus());
        wordAuthor.setText(selectedWord.getAuthor());
        wordLexicon.setText(selectedWord.getLexicon());
        wordDefinition.setText(selectedWord.getDefinition());
        wordSentence.setText(selectedWord.getSentence());

        return wordDetailView;
    }

}
