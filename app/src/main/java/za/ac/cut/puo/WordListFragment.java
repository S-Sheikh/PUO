package za.ac.cut.puo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
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
        List<Word> newWords = new ArrayList<>(Collections2.filter(words, new Predicate<Word>() {
            @Override
            public boolean apply(Word w) {
                for (Word word : mWords)
                    if (Objects.equal(word.getWord(), w.getWord()))
                        return false;
                return true;
            }
        }));

        /*Filter out blocked words if user is Collector otherwise add new words.*/
        if (Backendless.UserService.CurrentUser().getProperty("role").toString()
                .equalsIgnoreCase("Collector"))
            mWords.addAll(Collections2.filter(newWords, Predicates.not(new Predicate<Word>() {
                @Override
                public boolean apply(Word word) {
                    return word.isBlocked();
                }
            })));
        else
            mWords.addAll(newWords);
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
                mListener.onWordSelected(mWords.get(position));

                WordListItemAdapter.ViewHolder vh = (WordListItemAdapter.ViewHolder)
                        mRecyclerView.getChildViewHolder(itemView);

                showWordDetailDialogFragment(mWords.get(position), vh.wordDescImg, position);
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

    public void sortList(final String sortOption) {
        //List<Word> sortedList = mWords;
        if (!mWords.isEmpty()) {
            if (sortOption.equalsIgnoreCase("Date Asc")) {
                setmWords(Lists.reverse(mWords));
                mAdapter.notifyDataSetChanged();
            }
        }
        for (Word word : mWords) {
            Log.d("WordListFragment", "sortList: " + word.getWord());
        }

    }

    public Snackbar setSnackBar(View v, String message, int length) {
        Snackbar sb = Snackbar.make(v, message, length);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return sb;
    }

    private void showWordDetailDialogFragment(Word word, ImageView v, int position) {
        FragmentManager fm = getFragmentManager();
        WordDetailDialogFragment wordDetailDialogFragmentDialog = WordDetailDialogFragment.newInstance(word, v.getDrawable(), position);
        // SETS the target fragment for use later when sending results
        wordDetailDialogFragmentDialog.setTargetFragment(this, 400);
        wordDetailDialogFragmentDialog.show(fm, "fragment_word_detail");
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

        void onWordSelected(Word word);
    }
}
