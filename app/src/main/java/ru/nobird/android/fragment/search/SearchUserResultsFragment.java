package ru.nobird.android.fragment.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.nobird.android.data.PageableArrayList;
import ru.nobird.android.data.twitter.resources.SearchResources;
import ru.nobird.android.data.types.UserElement;
import ru.nobird.android.fragment.implementation.FragmentMgr;
import ru.nobird.android.fragment.implementation.adapter.PageableRecyclerViewAdapter;
import ru.nobird.android.fragment.implementation.adapter.UserRecycleViewAdapter;
import ru.nobird.android.fragment.implementation.task.AbsRecycleViewFragment;
import ru.nobird.android.fragment.implementation.task.AbsRecycleViewRefreshTask;
import twitter4j.TwitterException;

/**
 * Fragment to display users search queries
 */
public class SearchUserResultsFragment extends AbsRecycleViewFragment<UserElement> {
    public static final String ARG_QUERY = "query";
    private String query;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString(ARG_QUERY);
    }

    @Override
    protected AbsRecycleViewRefreshTask<UserElement> createTask(final AbsRecycleViewRefreshTask.Position position) {
        return new SearchUserResultTask(this, query, position);
    }

    @Override
    protected PageableRecyclerViewAdapter<UserElement, ?> createAdapter() {
        return new UserRecycleViewAdapter();
    }

    @Override
    protected String getToolbarTitle() {
        return query;
    }

    public static void show(final String query) {
        final Fragment fragment = new SearchUserResultsFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_QUERY, query);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    private final class SearchUserResultTask extends AbsRecycleViewRefreshTask<UserElement> {
        private final String query;
        private SearchUserResultTask(final SearchUserResultsFragment fragment, final String query, final Position position) {
            super(fragment, position);
            this.query = query;
        }

        @Override
        protected PageableArrayList<UserElement> doInBackground(final Long... params) {
            try {
                return SearchResources.getSearchUsersResults(query, params[1]);
            } catch (final TwitterException e) {
                return null;
            }
        }
    }
}
