package za.ac.cut.puo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
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
    private View rootView;
    private OnWordListItemClickListener mListener;

    public WordListFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment WordListFragment.
     */
    public static WordListFragment newInstance() {
        return new WordListFragment();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnWordListItemClickListener {
        void onMenuOptionSelected(int id);

        void onWordSelected(int position);
    }

    /**
     * Allows the parent activity or fragment to define the listener.
     */
    public void setWordListItemListener(OnWordListItemClickListener mListener) {
        this.mListener = mListener;
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
        mAdapter = new WordListItemAdapter(mWords, getContext());
        mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(mAdapter));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        loadData(mWords);

        //set adapter click listener
        mAdapter.setOnItemClickListener(new WordListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View itemView) {
                mListener.onWordSelected(mRecyclerView.getChildLayoutPosition(itemView));
                setSnackBar(rootView, "Word at position: " +
                                mRecyclerView.getChildLayoutPosition(itemView) + " selected.",
                        Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onOverflowClicked(final ImageView v) {
                PopupMenu wordOptions = new PopupMenu(getContext(), v);
                MenuInflater inflater = wordOptions.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, wordOptions.getMenu());
                wordOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int position = (mRecyclerView.findContainingViewHolder(v).getAdapterPosition());
                        switch (item.getItemId()) {
                            case R.id.support:
                                mListener.onMenuOptionSelected(R.id.support);
                                supportWord(position);
                                return true;
                            case R.id.block:
                                mListener.onMenuOptionSelected(R.id.block);
                                return true;
                            case R.id.rate:
                                mListener.onMenuOptionSelected(R.id.rate);
                                return true;
                            case R.id.add_to_word_chest:
                                mListener.onMenuOptionSelected(R.id.add_to_word_chest);
                                return true;
                            case R.id.share:
                                mListener.onMenuOptionSelected(R.id.share);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                wordOptions.show();
            }
        });

        return rootView;
    }

    //load words from cloud
    public void loadData(final List<Word> wordList) {
        final ProgressBar circularBar = (ProgressBar) rootView.findViewById(R.id.progressBarCircular);
        circularBar.setVisibility(View.VISIBLE);
        if (wordList != null) {
            wordList.clear();
        }

        Backendless.Persistence.of(Word.class).find(new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> puoWordList) {
                int curSize = mRecyclerView.getAdapter().getItemCount();
                wordList.addAll(puoWordList.getData());
                mRecyclerView.getAdapter().notifyItemRangeInserted(curSize, wordList.size());
                circularBar.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                setSnackBar(rootView.findViewById(R.id.container),
                        backendlessFault.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void supportWord(int position) {
        if (!mWords.get(position).isSupported()) {
            mWords.get(position).setSupported(true);
            mAdapter.notifyItemChanged(position);
            Backendless.Data.of(Word.class).findById(mWords.get(position), new AsyncCallback<Word>() {
                @Override
                public void handleResponse(Word word) {
                    if (!word.isSupported()) {
                        word.setSupported(true);
                        Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                            @Override
                            public void handleResponse(Word word) {
                                setSnackBar(rootView, word.getWord() + ": is now supported!",
                                        Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                setSnackBar(rootView, backendlessFault.getMessage(),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        setSnackBar(rootView, word.getWord() + ": is already supported!",
                                Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    setSnackBar(rootView, backendlessFault.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        } else
            setSnackBar(rootView, mWords.get(position).getWord() + ": is already supported!",
                    Snackbar.LENGTH_SHORT).show();
    }

    public Snackbar setSnackBar(View v, String message, int length) {
        Snackbar sb = Snackbar.make(v, message, length);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return sb;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWordListItemClickListener) {
            mListener = (OnWordListItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWordListItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
