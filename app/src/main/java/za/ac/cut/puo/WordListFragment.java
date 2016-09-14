package za.ac.cut.puo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWordListItemListener} interface
 * to handle interaction events.
 * Use the {@link WordListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Word> mWords;
    private WordListItemAdapter mAdapter;


    public WordListFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment WordListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordListFragment newInstance() {
        return new WordListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_word_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_word_list);
        mWords = PUOHelper.populateWordList();
        mAdapter = new WordListItemAdapter(mWords,getContext());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private OnWordListItemListener mListener;

    public void setWordListItemListener(OnWordListItemListener mListener) {
        this.mListener = mListener;
//        if (mListener != null) {
//            mListener.onWordListItemSelected(WordListItemAdapter.ViewHolder);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWordListItemListener) {
            mListener = (OnWordListItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWordListItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnWordListItemListener {
        // TODO: Update argument type and name
        void onWordListItemSelected(View v);
    }
}
