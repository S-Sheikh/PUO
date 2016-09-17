package za.ac.cut.puo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWordListItemClickListener} interface
 * to handle interaction events.
 * Use the {@link WordListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Word> mWords;
    private WordListItemAdapter mAdapter;
    View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_word_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_word_list);
        mWords = new ArrayList<>();
        mAdapter = new WordListItemAdapter(mWords,getContext());
        loadData(mAdapter,mWords);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
    }

    private OnWordListItemClickListener mListener;

    public void setWordListItemListener(OnWordListItemClickListener mListener) {
      this.mListener = mListener;
//        if (mListener != null) {
//            mListener.(mAdapter.getOnItemClickListenerCallBack());
//        }
    }

    public void loadData(final WordListItemAdapter adapter, final List<Word> wordList) {
        final ProgressBar circularBar = (ProgressBar) rootView.findViewById(R.id.progressBarCircular);
        circularBar.setVisibility(View.VISIBLE);
        if (wordList != null) {
            wordList.clear();
        }

        Backendless.Persistence.of(Word.class).find(new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> puoWordList) {
                int curSize = adapter.getItemCount();
                wordList.addAll(puoWordList.getData());
                adapter.notifyItemRangeInserted(curSize, wordList.size());
                circularBar.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Snackbar sb = Snackbar.make(rootView.findViewById(R.id.container),
                        backendlessFault.getMessage(),Snackbar.LENGTH_LONG);
                sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                sb.show();
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnWordListItemClickListener) {
//            mListener = (OnWordListItemClickListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnWordListItemClickListener");
//        }
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
    public interface OnWordListItemClickListener extends WordListItemAdapter.OnItemClickListener{ }

}
