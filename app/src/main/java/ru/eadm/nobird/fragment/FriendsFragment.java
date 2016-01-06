package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.adapter.AbsRecycleViewAdapter;
import ru.eadm.nobird.fragment.adapter.UserRecycleViewAdapter;
import ru.eadm.nobird.fragment.task.AbsRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsRecycleViewRefreshTask;
import twitter4j.TwitterException;

public class FriendsFragment extends AbsRecycleViewFragment<UserElement> {
    @Override
    protected AbsRecycleViewRefreshTask<UserElement> createTask(final AbsRecycleViewRefreshTask.Position position) {
        return new FriendsRefreshTask(this, getArguments().getLong("userID"), position);
    }

    @Override
    protected AbsRecycleViewAdapter<UserElement, ?> createAdapter(final PageableArrayList<UserElement> data) {
        return new UserRecycleViewAdapter(data);
    }

    @Override
    protected int getToolbarTitleID() { return R.string.following; }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {}

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
