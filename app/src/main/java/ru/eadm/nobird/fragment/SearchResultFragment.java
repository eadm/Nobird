package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.utils.TwitterExceptionResolver;
import ru.eadm.nobird.data.types.TweetElement;
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

    @Nullable
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
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search_results_menu, menu);
    }

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
}
