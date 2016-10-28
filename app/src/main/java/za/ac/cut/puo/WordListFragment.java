package za.ac.cut.puo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private static List<Word> mWords;
    private static RecyclerView mRecyclerView;
    private WordListItemAdapter mAdapter;
    private View rootView;
    private OnWordListItemClickListener mListener;
    private SwipeRefreshLayout swipeRefreshWordList;
    private ProgressBar circularBar;
    private Spinner spSortOptions;

    public WordListFragment() {
        // Required empty public constructor
    }

    public static void setmWords(List<Word> words) {
        if (mWords != null)
            mWords.clear();

        mWords.addAll(words);
        int curSize = mRecyclerView.getAdapter().getItemCount();
        mRecyclerView.getAdapter().notifyItemRangeInserted(curSize, mWords.size());
        mRecyclerView.getAdapter().notifyItemRangeChanged(0, mWords.size());
        for (Word word:mWords) {
            Log.d("MYTAG", "setmWords: " + word.getWord());
        }
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
        spSortOptions = (Spinner) rootView.findViewById(R.id.sp_ordering);
        swipeRefreshWordList = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_word_list);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_word_list);
        mWords = new ArrayList<>();
        mAdapter = new WordListItemAdapter(mWords, getContext());
        mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(mAdapter));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        swipeRefreshWordList.setColorSchemeResources(R.color.colorAccent);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*load words*/
        loadData();

        /*set adapter click listener*/
        mAdapter.setOnItemClickListener(new WordListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View itemView) {
                int position = mRecyclerView.getChildAdapterPosition(itemView);
                mListener.onWordSelected(position);

                WordListItemAdapter.ViewHolder vh = (WordListItemAdapter.ViewHolder)
                        mRecyclerView.getChildViewHolder(itemView);

                showWordDetailDialogFragment(mWords.get(position), vh.wordDescImg);
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
                                blockWord(position);
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

        /*Set pull to refresh.*/
        swipeRefreshWordList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        spSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortList(spSortOptions.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Checks if fragment is attached to WordTreasureActivity and loads words from Backendless,
     * else if it is attached to WordChestActivity and loads words from local database.
     */
    public void loadData() {
        circularBar.setVisibility(View.VISIBLE);

        if (getContext() instanceof WordTreasureActivity) {
            QueryOptions queryOptions = new QueryOptions();
            List<String> sortBy = new ArrayList<>();
            sortBy.add("created DESC");
            queryOptions.setSortBy(sortBy);

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setQueryOptions(queryOptions);

            Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
                @Override
                public void handleResponse(BackendlessCollection<Word> puoWordList) {
                    setmWords(puoWordList.getData());
                    circularBar.setVisibility(View.GONE);
                    swipeRefreshWordList.setRefreshing(false);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    setSnackBar(getView(), backendlessFault.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        } else if (getContext() instanceof WordChestActivity) {
            PUOHelper.LoadFromWordChestTask.getTask(getContext()).execute();

            int curSize = mRecyclerView.getAdapter().getItemCount();
            mRecyclerView.getAdapter().notifyItemRangeInserted(curSize, mWords.size());
            circularBar.setVisibility(View.GONE);
            swipeRefreshWordList.setRefreshing(false);
        }
    }

    /**
     * Updates a word status to supported.
     *
     * @param position
     */
    public void supportWord(final int position) {
        //TODO: update support functionality to allow multiple support
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
     * Updates a word status to blocked and sets supported to false.
     */
    public void blockWord(final int position) {
        if (PUOHelper.connectionAvailable(getContext())) {
            if (!mWords.get(position).isBlocked()) {
                mWords.get(position).setBlocked(true);
                mWords.get(position).setSupported(false);
                mAdapter.notifyItemChanged(position);
                Backendless.Persistence.of(Word.class).findById(mWords.get(position), new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        if (!word.isBlocked()) {
                            word.setBlocked(true);
                            word.setSupported(false);
                            mAdapter.notifyItemChanged(position);
                            Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                                @Override
                                public void handleResponse(Word word) {
                                    setSnackBar(getView(), word.getWord() + ": is now blocked!",
                                            Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    setSnackBar(getView(), backendlessFault.getMessage(),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else
                            setSnackBar(getView(), word.getWord() + ": is already blocked!",
                                    Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        setSnackBar(rootView, backendlessFault.getMessage(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else
                setSnackBar(getView(), mWords.get(position).getWord() + ": is already blocked!",
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

    public void sortList(final String sortOption) {
        List<Word> sortedList = new ArrayList<>();
        sortedList = mWords;
        Collections.sort(sortedList, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                if (sortOption.equalsIgnoreCase("Date Asc"))
                    return o2.getCreated().compareTo(o1.getCreated());
                else
                    return o1.getCreated().compareTo(o2.getCreated());
            }
        });
        for (Word word:sortedList) {
            Log.d("MYTAG", "sortList: " + word.getWord());
        }
        setmWords(sortedList);
    }

    public Snackbar setSnackBar(View v, String message, int length) {
        Snackbar sb = Snackbar.make(v, message, length);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return sb;
    }

    private void showWordDetailDialogFragment(Word word, ImageView v) {
        FragmentManager fm = getFragmentManager();
        WordDetailFragment wordDetailFragmentDialog = WordDetailFragment.newInstance(word, v);
        // SETS the target fragment for use later when sending results
        wordDetailFragmentDialog.setTargetFragment(WordListFragment.this, 400);
        //fm.beginTransaction().add(wordDetailFragmentDialog,"fragment_wor_detail").commit();
        wordDetailFragmentDialog.show(fm, "fragment_word_detail");
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
