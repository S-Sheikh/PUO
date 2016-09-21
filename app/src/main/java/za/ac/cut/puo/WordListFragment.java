package za.ac.cut.puo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

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
    private SwipeRefreshLayout swipeRefreshWordList;
    private ProgressBar circularBar;

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
        circularBar = (ProgressBar) rootView.findViewById(R.id.progressBarCircular);
        swipeRefreshWordList = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_word_list);
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
                int position = mRecyclerView.getChildAdapterPosition(itemView);
                mListener.onWordSelected(position);
                setSnackBar(rootView, mWords.get(position).getWord() + ": selected.",
                        Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onOverflowClicked(final ImageView v) {
                final PopupMenu wordOptions = new PopupMenu(getContext(), v);
                MenuInflater inflater = wordOptions.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, wordOptions.getMenu());

                /**
                 * Check if word is supported or not and remove menu options accordingly.*/
                if (!mWords.get((mRecyclerView.findContainingViewHolder(v)
                        .getAdapterPosition())).isSupported())
                    wordOptions.getMenu().removeItem(R.id.unsupport);
                else
                    wordOptions.getMenu().removeItem(R.id.support);

                wordOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int position = (mRecyclerView.findContainingViewHolder(v).getAdapterPosition());
                        switch (item.getItemId()) {
                            case R.id.support:
                                mListener.onMenuOptionSelected(R.id.support);
                                supportWord(position);
                                return true;
                            case R.id.unsupport:
                                mListener.onMenuOptionSelected(R.id.unsupport);
                                unSupportWord(position);
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

        //Set pull to refresh.
        swipeRefreshWordList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mWords);
            }
        });

        swipeRefreshWordList.setColorSchemeResources(R.color.colorAccent);

        return rootView;
    }

    /**
     * load words from Backendless.
     */
    public void loadData(final List<Word> wordList) {

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<>();
        sortBy.add("created DESC");
        queryOptions.setSortBy(sortBy);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setQueryOptions(queryOptions);

        circularBar.setVisibility(View.VISIBLE);
        Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
            @Override
            public void handleResponse(BackendlessCollection<Word> puoWordList) {
                List<Word> newWords = puoWordList.getData();
                newWords.removeAll(wordList);
                for (Word word:newWords) {
                    System.out.println("newWords = " + word.getWord());
                }
                mWords.addAll(newWords);

                int curSize = mRecyclerView.getAdapter().getItemCount();
                mRecyclerView.getAdapter().notifyItemRangeInserted(curSize, mWords.size());
                circularBar.setVisibility(View.GONE);
                swipeRefreshWordList.setRefreshing(false);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                setSnackBar(getView(), backendlessFault.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Updates a word status to supported.
     */
    public void supportWord(final int position) {
        if (PUOHelper.connectionAvailable(getContext())) {
            if (!mWords.get(position).isSupported()) {
                mWords.get(position).setSupported(true);
                mAdapter.notifyItemChanged(position);
                Backendless.Persistence.of(Word.class).findById(mWords.get(position), new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        if (!word.isSupported()) {
                            word.setSupported(true);
                            mAdapter.notifyItemChanged(position);
                            Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                                @Override
                                public void handleResponse(Word word) {
                                    setSnackBar(getView(), word.getWord() + ": is now supported!",
                                            Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    setSnackBar(getView(), backendlessFault.getMessage(),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else
                            setSnackBar(getView(), word.getWord() + ": is already supported!",
                                    Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        setSnackBar(rootView, backendlessFault.getMessage(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else
                setSnackBar(getView(), mWords.get(position).getWord() + ": is already supported!",
                        Snackbar.LENGTH_SHORT).show();
        } else
            setSnackBar(getView(), "No internet available! Please check connection.",
                    Snackbar.LENGTH_LONG).show();
    }

    /**
     * Updates a word status to unsupported.
     */
    public void unSupportWord(final int position) {
        if (PUOHelper.connectionAvailable(getContext())) {
            if (mWords.get(position).isSupported()) {
                mWords.get(position).setSupported(false);
                mAdapter.notifyItemChanged(position);
                Backendless.Persistence.of(Word.class).findById(mWords.get(position), new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        if (word.isSupported()) {
                            word.setSupported(false);
                            mAdapter.notifyItemChanged(position);
                            Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                                @Override
                                public void handleResponse(Word word) {
                                    setSnackBar(getView(), word.getWord() + ": is now unsupported!",
                                            Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    setSnackBar(getView(), backendlessFault.getMessage(),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else
                            setSnackBar(getView(), word.getWord() + ": is already unsupported!",
                                    Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        setSnackBar(rootView, backendlessFault.getMessage(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else
                setSnackBar(getView(), mWords.get(position).getWord() + ": is already unsupported!",
                        Snackbar.LENGTH_SHORT).show();
        } else
            setSnackBar(getView(), "No internet available! Please check connection.",
                    Snackbar.LENGTH_LONG).show();
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
