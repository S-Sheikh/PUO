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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

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

    public WordListFragment mInstance = this;
    private static List<Word> mWords;
    private RecyclerView mRecyclerView;
    private WordListItemAdapter mAdapter;
    private View rootView;

    private OnWordListItemClickListener mListener;
    private SwipeRefreshLayout swipeRefreshWordList;
    private ProgressBar circularBar;
    private Spinner spSortOptions;
    static BackendlessUser user;


    public WordListFragment() {
        // Required empty public constructor
    }

    public static void setmWords(List<Word> words) {

        /*Filter out existing words.*/
        List<Word> newWords = new ArrayList<>(Collections2.filter(words, w -> {
            for (Word word : mWords)
                if (Objects.equal(word.getWord(), w.getWord()))
                    return false;
            return true;
        }));

        /*Filter out blocked words if user is Collector otherwise add new words.*/
        if (Backendless.UserService.CurrentUser().getProperty("role").toString()
                .equalsIgnoreCase("Collector"))
            mWords.addAll(Collections2.filter(newWords, Predicates.not(Word::isBlocked)));
        else
            mWords.addAll(newWords);

        Log.d("setmWords: ", "Filtered List isEmpty: " + newWords.isEmpty());
        for (Word word : newWords) {
            Log.d("setmWords: ", "FilteredList: " + word.getWord());
        }
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public WordListItemAdapter getmAdapter() {
        return mAdapter;
    }

    /**
     * @return A new instance of fragment WordListFragment.
     */
    public static WordListFragment newInstance() {
        return new WordListFragment();
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
        mWords = new ArrayList<>();
        mAdapter = new WordListItemAdapter(mWords, getContext());
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
        setUpRecyclerView();
        swipeRefreshWordList.setColorSchemeResources(R.color.colorAccent);
         /*load words*/
        loadData();
        return rootView;
    }

    public void setUpRecyclerView() {
        mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(mAdapter));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*set adapter click listener*/
        mAdapter.setOnItemClickListener(new WordListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View itemView) {
                int position = mRecyclerView.getChildAdapterPosition(itemView);
                mListener.onWordSelected(position);

                WordListItemAdapter.ViewHolder vh = (WordListItemAdapter.ViewHolder)
                        mRecyclerView.getChildViewHolder(itemView);

                showWordDetailDialogFragment(mWords.get(position), vh.wordDescImg, position);
            }

            @Override
            public void onOverflowClicked(final ImageView v) {
                final PopupMenu wordOptions = new PopupMenu(getContext(), v);
                MenuInflater inflater = wordOptions.getMenuInflater();
                inflater.inflate(R.menu.menu_popup, wordOptions.getMenu());

                /**
                 * Check if user is Collector and remove menu options accordingly.*/
                if (Backendless.UserService.CurrentUser().getProperty("role").toString()
                        .equalsIgnoreCase("Collector")) {
                    wordOptions.getMenu().removeItem(R.id.unsupport);
                    wordOptions.getMenu().removeItem(R.id.support);
                    wordOptions.getMenu().removeItem(R.id.block);
                } else {
                    /**
                     * Check if word is supported and remove menu options accordingly.*/
                    if (!mWords.get((mRecyclerView.findContainingViewHolder(v)
                            .getAdapterPosition())).isSupported())
                        wordOptions.getMenu().removeItem(R.id.unsupport);
                    else
                        wordOptions.getMenu().removeItem(R.id.support);
                }

                wordOptions.setOnMenuItemClickListener(item -> {
                    int position = mRecyclerView.findContainingViewHolder(v).getAdapterPosition();
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
                            rateWord(position);
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
                });
                wordOptions.show();
            }
        });

        /*Set pull to refresh.*/
        swipeRefreshWordList.setOnRefreshListener(this::loadData);

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
            swipeRefreshWordList.setRefreshing(false);
            Backendless.Persistence.of(Word.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Word>>() {
                @Override
                public void handleResponse(BackendlessCollection<Word> puoWordList) {
                    setmWords(puoWordList.getData());
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mWords.size());
                    circularBar.setVisibility(View.GONE);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    setSnackBar(getView(), backendlessFault.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        } else if (getContext() instanceof WordChestActivity) {
            swipeRefreshWordList.setRefreshing(false);
            PUOHelper.LoadFromWordChestTask.initialize(getContext(), mAdapter).execute();
            circularBar.setVisibility(View.GONE);
        }
    }

    /**
     * Updates a word status to supported.
     *
     * @param position positions of the selected word in the list.
     */
    public void supportWord(final int position) {
        //TODO: update support functionality to allow multiple support
        if (PUOHelper.connectionAvailable(getContext())) {
            Word word = mWords.get(position);
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
                Backendless.Persistence.save(mWords.get(position), new AsyncCallback<Word>() {
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

    public void rateWord(final int position) {
        final PopupWindow ratingPopup = PUOHelper.getPopup(getContext(), null);
        ratingPopup.showAtLocation(new View(getContext()), Gravity.CENTER, 0, 0);
        final RatingBar ratingBar = (RatingBar) ratingPopup.getContentView().findViewById(R.id.rating_bar);
        Button rate = (Button) ratingPopup.getContentView().findViewById(R.id.btn_rate);
        rate.setOnClickListener(v -> {
            Word word = mWords.get(position);
            word.setRating(ratingBar.getRating());
            mAdapter.notifyItemChanged(position);
            Backendless.Persistence.save(word, new AsyncCallback<Word>() {
                @Override
                public void handleResponse(Word word) {
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    setSnackBar(rootView, backendlessFault.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
            ratingPopup.dismiss();
        });

    }

    public void sortList(final String sortOption) {
        List<Word> sortedList = mWords;
        if (!sortedList.isEmpty()) {
            if (sortOption.equalsIgnoreCase("Date Asc")) {
                setmWords(Lists.reverse(sortedList));
                mAdapter.notifyItemRangeChanged(0, mWords.size());
            }
        }
        for (Word word : sortedList) {
            Log.d("WordListFragment", "sortList: " + word.getWord() + word.getCreated());
        }

    }

    public Snackbar setSnackBar(View v, String message, int length) {
        Snackbar sb = Snackbar.make(v, message, length);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return sb;
    }

    private void showWordDetailDialogFragment(Word word, ImageView v, int position) {
        FragmentManager fm = getFragmentManager();
        WordDetailFragment wordDetailFragmentDialog = WordDetailFragment.newInstance(word, v.getDrawable(), position);
        // SETS the target fragment for use later when sending results
        wordDetailFragmentDialog.setTargetFragment(WordListFragment.this, 400);
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
}
