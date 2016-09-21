package ru.eadm.nobird.fragment.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.FragmentMgr;
import ru.eadm.nobird.fragment.adapter.PageableRecyclerViewAdapter;
import ru.eadm.nobird.fragment.adapter.UserRecycleViewAdapter;
import ru.eadm.nobird.fragment.task.AbsRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsRecycleViewRefreshTask;
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
                return TwitterMgr.getInstance().getSearchUsersResults(query, params[1]);
            } catch (final TwitterException e) {
                return null;
            }
        }
    }
}
