package ru.eadm.nobird.fragment.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.utils.TwitterExceptionResolver;
import ru.eadm.nobird.data.types.StringElement;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.FragmentMgr;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.TwitterException;

/**
 * Fragment with search results
 */
public class SearchResultFragment extends AbsTweetRecycleViewFragment {
    public static final String ARG_QUERY = "query";
    private String query;
    private StringElement savedSearch;

    private MenuItem action_create_saved, action_destroy_saved;

    private CheckSavedSearchTask savedSearchTask;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View page = inflater.inflate(R.layout.fragment_list, container, false);

        final Toolbar toolbar = (Toolbar) page.findViewById(R.id.fragment_list_toolbar);
        toolbar.setTitle(query);

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRefreshLayout((SwipeRefreshLayout) page.findViewById(R.id.fragment_list_swipe_refresh_layout));
        setRecyclerView((RecyclerView) page.findViewById(R.id.fragment_list_recycle_view));

        if (refreshTask == null) {
            refreshTask = createRefreshTask(POSITION_START);
            refreshTask.execute(0L, 0L);
        }

        return page;
    }

    @Override
    protected AbsTweetRecycleViewRefreshTask createRefreshTask(final int pos) {
        return new SearchResultLoadTask(this, pos, query);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        query = getArguments().getString(ARG_QUERY);

        savedSearchTask = new CheckSavedSearchTask(this);
        savedSearchTask.execute(query);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        savedSearchTask.cancel(true);
        savedSearchTask = null;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search_results_menu, menu);

        action_create_saved = menu.findItem(R.id.action_create_saved_search);
        action_destroy_saved = menu.findItem(R.id.action_destroy_saved_search);

        if (savedSearchTask != null && savedSearchTask.getStatus() == AsyncTask.Status.FINISHED) {
            setSavedSearch(savedSearch);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        item.setVisible(false);
        switch (item.getItemId()) {
            case R.id.action_create_saved_search:
                savedSearchTask = new CreateSavedSearchTask(this, query);
                savedSearchTask.execute();
                break;
            case R.id.action_destroy_saved_search:
                savedSearchTask = new DestroySavedSearchTask(this, savedSearch.getID());
                savedSearchTask.execute();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets saved search state
     * @param saved if null means current query isn't saved otherwise it considered as saved
     */
    public void setSavedSearch(final StringElement saved) {
        this.savedSearch = saved;
        if (action_create_saved != null) {
            action_create_saved.setVisible(saved == null);
            action_destroy_saved.setVisible(saved != null);
        }
    }

    /**
     * Loads results to current query
     */
    private final class SearchResultLoadTask extends AbsTweetRecycleViewRefreshTask {
        private final String query;

        public SearchResultLoadTask(final AbsTweetRecycleViewFragment fragment, final int position, final String query) {
            super(fragment, position, Source.API);
            this.query = query;
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(final Long... params) {
            try {
                return TwitterMgr.getInstance().getSearchResults(query, params[0], params[1]);
            } catch (final TwitterException e) {
                NotificationMgr.getInstance().showSnackbar(TwitterExceptionResolver.resolve(e), null);
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Open search results for given query
     * @param query - search query
     */
    public static void show(final String query) {
        final Fragment fragment = new SearchResultFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_QUERY, query);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    /**
     * Task to check if this saved search exists in db
     */
    public static class CheckSavedSearchTask extends AsyncTask<String, Void, StringElement> {
        private final WeakReference<SearchResultFragment> weakReference;

        private CheckSavedSearchTask(final SearchResultFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected StringElement doInBackground(final String... params) {
            return DBMgr.getInstance().getSearchByQuery(params[0]);
        }

        @Override
        protected void onPostExecute(final StringElement stringElement) {
            final SearchResultFragment fragment = weakReference.get();
            if (fragment != null) fragment.setSavedSearch(stringElement);
        }
    }

    /**
     * Destroys saved search on twitter, db and list in SearchFragment
     */
    public static final class DestroySavedSearchTask extends CheckSavedSearchTask {
        private final long searchID;

        public DestroySavedSearchTask(final SearchResultFragment fragment, final long searchID) {
            super(fragment);
            this.searchID = searchID;
        }

        @Override
        protected StringElement doInBackground(final String... params) {
            try {
                TwitterMgr.getInstance().destroySavedSearch(searchID);
            } catch (final TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final StringElement ignore) {
            super.onPostExecute(null);
            final SearchFragment searchFragment = FragmentMgr.getInstance().lookup(SearchFragment.class);
            if (searchFragment != null) searchFragment.destroySavedSearch(searchID);
        }
    }

    /**
     * Creates saved search on twitter, db and list in SearchFragment
     */
    private final class CreateSavedSearchTask extends CheckSavedSearchTask {
        private final String query;

        private CreateSavedSearchTask(final SearchResultFragment fragment, final String query) {
            super(fragment);
            this.query = query;
        }

        @Override
        protected StringElement doInBackground(final String... params) {
            try {
                return TwitterMgr.getInstance().createSavedSearch(query);
            } catch (final TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final StringElement stringElement) {
            super.onPostExecute(stringElement);
            final SearchFragment searchFragment = FragmentMgr.getInstance().lookup(SearchFragment.class);
            if (searchFragment != null) searchFragment.createSavedSearch(stringElement);
        }
    }
}