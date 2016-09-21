package ru.eadm.nobird.fragment.search;

import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Pattern;

import ru.eadm.nobird.R;
import ru.eadm.nobird.Util;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.StringElement;
import ru.eadm.nobird.databinding.FragmentSearchBinding;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.design.animation.OnEndAnimationListener;
import ru.eadm.nobird.design.animation.OnStartAnimationListener;
import ru.eadm.nobird.fragment.FragmentMgr;
import ru.eadm.nobird.fragment.UserFragment;
import ru.eadm.nobird.fragment.adapter.StringRecycleViewAdapter;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import twitter4j.TwitterException;

/**
 * Fragment with search quires
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    private FragmentSearchBinding binding;
    private String query;
    private Pattern usernamePattern;

    private SavedSearchesLoadTask task;

    private StringRecycleViewAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle state) {
        super.onCreateView(inflater, container, state);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.fragmentSearchToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.fragmentSearchQuery.setText(query);
        binding.fragmentSearchQuery.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) { setQuery(s.toString()); }
        });
        setQuery(query);
        binding.fragmentSearchActionGoToUser.setOnClickListener(this);
        binding.fragmentSearchActionStatuses.setOnClickListener(this);
        binding.fragmentSearchActionUsers.setOnClickListener(this);

        binding.fragmentSearchSavedQueries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.fragmentSearchSavedQueries.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));
        binding.fragmentSearchSavedQueries.setAdapter(adapter);
        binding.fragmentSearchSavedQueries.setHasFixedSize(true);

        if (state == null) {
            binding.fragmentSearchQuery.requestFocus();
            Util.openKeyboard(getContext(), binding.fragmentSearchQuery);
        }

        if (task == null) {
            task = new SavedSearchesLoadTask(this, AbsTweetRecycleViewRefreshTask.Source.CACHE);
            task.execute();
        }

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView a, RecyclerView.ViewHolder b, RecyclerView.ViewHolder c) { return false; }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int swipeDir) {
                final int pos = viewHolder.getAdapterPosition();
                new SearchResultFragment.DestroySavedSearchTask(null, adapter.get(pos).getID()).execute();
                adapter.remove(pos);
            }

            @Override
            public void onChildDraw(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    final float width = (float) viewHolder.itemView.getWidth();
                    final float alpha = 1.0f - Math.abs(dX) / width;
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                            actionState, isCurrentlyActive);
                }
            }
        };
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.fragmentSearchSavedQueries);

        return binding.getRoot();
    }

    /**
     * Set current query
     * @param newQuery - new query
     */
    private void setQuery(final String newQuery) {
        if (newQuery == null) return;
        this.query = newQuery.trim();
        if (query.length() > 0) {
            binding.fragmentSearchActionStatuses.setText(String.format(getString(R.string.search_action_statuses), query));
            binding.fragmentSearchActionUsers.setText(String.format(getString(R.string.search_action_users), query));
            binding.fragmentSearchActionGoToUser.setText(String.format(getString(R.string.search_action_go_to_user), query));

            if (usernamePattern.matcher(query).matches()) {
                binding.fragmentSearchActionGoToUser.setVisibility(View.VISIBLE);
            } else {
                binding.fragmentSearchActionGoToUser.setVisibility(View.GONE);
            }

            if (binding.fragmentSearchActions.getVisibility() == View.GONE) {
                final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_start);
                animation.setAnimationListener(new OnStartAnimationListener(binding.fragmentSearchActions));
                binding.fragmentSearchActions.startAnimation(animation);
            }
        } else {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_end);
            animation.setAnimationListener(new OnEndAnimationListener(binding.fragmentSearchActions));
            binding.fragmentSearchActions.startAnimation(animation);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            query = getArguments().getString(SearchResultFragment.ARG_QUERY);
        }
        usernamePattern = Pattern.compile("[A-Za-z0-9_]+");
        adapter = new StringRecycleViewAdapter(new StringRecycleViewAdapter.OnDataClickListener<StringElement>() {
            @Override
            public void onClick(final StringElement data) {
                SearchResultFragment.show(data.getText());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        usernamePattern = null;
        query = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Util.closeKeyboard(getContext(), binding.fragmentSearchQuery.getWindowToken());
        binding.unbind();
        binding = null;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fragment_search_action_go_to_user:
                UserFragment.showUser(query);
            break;
            case R.id.fragment_search_action_statuses:
                SearchResultFragment.show(query);
            break;
            case R.id.fragment_search_action_users:
                SearchUserResultsFragment.show(query);
            break;
        }
    }

    public static void show() {
        FragmentMgr.getInstance().replaceFragment(0, new SearchFragment(), true);
    }

    /**
     * Removes from current adapter saved search with given id
     * @param id - id of target saved search
     */
    public void destroySavedSearch(final long id) {
        if (adapter != null) adapter.removeByElementID(id);
    }

    /**
     * Adds saved search to current adapter
     * @param stringElement - element to add
     */
    public void createSavedSearch(final StringElement stringElement) {
        if (adapter != null) adapter.add(stringElement);
    }

    /**
     * Loads saved searches from DB or API, if searches were loaded from DB tries to load them from API
     */
    private final class SavedSearchesLoadTask extends AsyncTask<Void, Void, List<StringElement>> {
        private final WeakReference<SearchFragment> fragmentWeakReference;
        private final AbsTweetRecycleViewRefreshTask.Source source;

        private SavedSearchesLoadTask(final SearchFragment fragment, final AbsTweetRecycleViewRefreshTask.Source source) {
            fragmentWeakReference = new WeakReference<>(fragment);
            this.source = source;
        }

        @Override
        protected List<StringElement> doInBackground(final Void... params) {
            if (source == AbsTweetRecycleViewRefreshTask.Source.CACHE) {
                return DBMgr.getInstance().getSearches();
            } else try {
                return TwitterMgr.getInstance().getSavedSearches();
            } catch (final TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<StringElement> data) {
            final SearchFragment fragment = fragmentWeakReference.get();
            if (fragment != null && data != null) {
                Log.d("SavedSearchesLoadTask", "saved searches: " + data.size());
                fragment.adapter.clear();
                fragment.adapter.addAll(data);
                if (source == AbsTweetRecycleViewRefreshTask.Source.CACHE) {
                    fragment.task = new SavedSearchesLoadTask(fragment, AbsTweetRecycleViewRefreshTask.Source.API);
                    fragment.task.execute();
                }
            }
        }
    }
}
