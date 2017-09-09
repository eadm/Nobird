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

public class FriendsFragment extends AbsRecycleViewFragment<UserElement> {
    @Override
    protected AbsRecycleViewRefreshTask<UserElement> createTask(final AbsRecycleViewRefreshTask.Position position) {
        return new FriendsRefreshTask(this, getArguments().getLong("userID"), position);
    }

    @Override
    protected PageableRecyclerViewAdapter<UserElement, ?> createAdapter() {
        return new UserRecycleViewAdapter();
    }

    @Override
    protected String getToolbarTitle() { return getString(R.string.following); }

    public static void showUserFriends(final long userID) {
        final FriendsFragment fragment = new FriendsFragment();
        final Bundle bundle = new Bundle();
        bundle.putLong("userID", userID);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    private final class FriendsRefreshTask extends AbsRecycleViewRefreshTask<UserElement> {
        private final long userID;
        private FriendsRefreshTask(final FriendsFragment fragment, final long userID, final Position position) {
            super(fragment, position);
            this.userID = userID;
        }

        @Override
        protected PageableArrayList<UserElement> doInBackground(final Long... params) {
            try {
                return TwitterMgr.getInstance().getUserFriends(userID, (params[0] != 0 ? params[0] : params[1]));
            } catch (final TwitterException e) {
                return null;
            }
        }
    }
}
