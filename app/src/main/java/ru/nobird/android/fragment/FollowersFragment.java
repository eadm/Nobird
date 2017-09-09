package ru.nobird.android.fragment;

import android.os.Bundle;

import ru.nobird.android.R;
import ru.nobird.android.data.PageableArrayList;
import ru.nobird.android.data.twitter.TwitterMgr;
import ru.nobird.android.data.types.UserElement;
import ru.nobird.android.fragment.implementation.FragmentMgr;
import ru.nobird.android.fragment.implementation.adapter.PageableRecyclerViewAdapter;
import ru.nobird.android.fragment.implementation.adapter.UserRecycleViewAdapter;
import ru.nobird.android.fragment.implementation.task.AbsRecycleViewFragment;
import ru.nobird.android.fragment.implementation.task.AbsRecycleViewRefreshTask;
import twitter4j.TwitterException;

public class FollowersFragment extends AbsRecycleViewFragment<UserElement> {

    @Override
    protected AbsRecycleViewRefreshTask<UserElement> createTask(final AbsRecycleViewRefreshTask.Position position) {
        return new FollowersRefreshTask(this, getArguments().getLong("userID"), position);
    }

    @Override
    protected PageableRecyclerViewAdapter<UserElement, ?> createAdapter() {
        return new UserRecycleViewAdapter();
    }

    @Override
    protected String getToolbarTitle() { return getString(R.string.followers); }

    public static void showUserFollowers(final long userID) {
        final FollowersFragment fragment = new FollowersFragment();
        final Bundle bundle = new Bundle();
        bundle.putLong("userID", userID);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    private final class FollowersRefreshTask extends AbsRecycleViewRefreshTask<UserElement> {
        private final long userID;
        private FollowersRefreshTask(final FollowersFragment fragment, final long userID, final Position position) {
            super(fragment, position);
            this.userID = userID;
        }

        @Override
        protected PageableArrayList<UserElement> doInBackground(final Long... params) {
            try {
                return TwitterMgr.getInstance().getUserFollowers(userID, (params[0] != 0 ? params[0] : params[1]));
            } catch (final TwitterException e) {
                return null;
            }
        }
    }
}
