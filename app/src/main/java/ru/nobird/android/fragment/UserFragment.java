package ru.nobird.android.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.nobird.android.R;
import ru.nobird.android.data.ImageMgr;
import ru.nobird.android.data.SharedPreferenceHelper;
import ru.nobird.android.data.twitter.TwitterMgr;
import ru.nobird.android.data.types.TweetElement;
import ru.nobird.android.data.types.UserElement;
import ru.nobird.android.databinding.FragmentUserBinding;
import ru.nobird.android.fragment.implementation.FragmentMgr;
import ru.nobird.android.fragment.implementation.task.AbsTweetRecycleViewFragment;
import ru.nobird.android.fragment.implementation.task.AbsTweetRecycleViewRefreshTask;
import ru.nobird.android.fragment.implementation.task.AbsTwitterDataLoadTask;
import ru.nobird.android.notification.NotificationMgr;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserFragment extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private long userID = 0;
    private String userName;

    private MenuItem
            action_follow,
            action_unfollow,

            action_mute,
            action_unmute,

            action_block,
            action_unblock,

            action_message,
            action_reply;

    private Relationship relationship;
    private User user;
    private AbsTwitterDataLoadTask<Long, ?, UserFragment> userTask;
    private FragmentUserBinding binding;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);

        setRefreshLayout(binding.fragmentUserSwipeRefreshLayout);
        setRecyclerView(binding.fragmentUserTimeline);

        binding.fragmentUserToolbar.setTitle("");
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.fragmentUserToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.fragmentUserAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                if (binding.fragmentUserInfoContainer.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(binding.fragmentUserInfoContainer)) {
                    binding.fragmentUserInfoSmall.animate().alpha(1).setDuration(getResources().getInteger(R.integer.animation_duration));
                } else {
                    binding.fragmentUserInfoSmall.animate().alpha(0).setDuration(getResources().getInteger(R.integer.animation_duration));
                }
            }
        });

        if (user != null) {
            setUser(user);
        } else {
            binding.fragmentUserName.setText(getArguments().getString("name"));
            binding.fragmentUserUsername.setText(String.format(getString(R.string.username_placeholder), getArguments().getString("username")));
            ImageMgr.getInstance().displayRoundImage(getArguments().getString("image"), binding.fragmentUserImage);

            if (userTask == null) {
                userTask = new UserLoaderTask(this, userName);
                userTask.execute(userID);
            }
        }

        return binding.getRoot();
    }

    @Override
    protected AbsTweetRecycleViewRefreshTask createRefreshTask(final int pos) {
        return new UserTimelineTask(this, userID, pos);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getLong("userID");
        userName = getArguments().getString("username");
        if (userID != SharedPreferenceHelper.getInstance().getCurrentAccountID()) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_menu, menu);

        action_follow = menu.findItem(R.id.action_follow);
        action_unfollow = menu.findItem(R.id.action_unfollow);

        action_mute = menu.findItem(R.id.action_mute);
        action_unmute = menu.findItem(R.id.action_unmute);

        action_block = menu.findItem(R.id.action_block);
        action_unblock = menu.findItem(R.id.action_unblock);

        action_message = menu.findItem(R.id.action_message);

        if (relationship != null) {
            setRelationship(relationship);
        }

        action_reply = menu.findItem(R.id.action_reply);
        if (user != null) {
            action_reply.setVisible(user.getId() != SharedPreferenceHelper.getInstance().getCurrentAccountID());
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        item.setVisible(false); // hide item to prevent same requests
        switch (item.getItemId()) {
            case R.id.action_follow:
                new ChangeFriendshipTask(this, true).execute(userID);
                return true;
            case R.id.action_unfollow:
                new ChangeFriendshipTask(this, false).execute(userID);
                return true;

            case R.id.action_mute:
                new ChangeMuteStatusTask(this, true).execute(userID);
                return true;
            case R.id.action_unmute:
                new ChangeMuteStatusTask(this, false).execute(userID);
                return true;

            case R.id.action_block:
                new ChangeBlockStatusTask(this, true).execute(userID);
                return true;
            case R.id.action_unblock:
                new ChangeBlockStatusTask(this, false).execute(userID);
                return true;

            case R.id.action_spam:
                new SpamReportTask(this).execute(userID);
                return true;

            case R.id.action_message:
                item.setVisible(true);
                return true;

            case R.id.action_reply:
                item.setVisible(true);
                ComposeFragment.open(0, '@' + user.getName() + ' ');
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();

        action_follow = null;
        action_unfollow = null;
        action_mute = null;
        action_unmute = null;
        action_block = null;
        action_unblock = null;
        action_message = null;
        action_reply = null;
    }

    @Override
    public void onDestroyView() { // to avoid leaks
        super.onDestroyView();
        binding.unbind();
        binding = null;
    }

    @Override
    public void onDestroy() { // to avoid leaks it's calls when fragment completely destroyed
        super.onDestroy();
        user = null;
        userTask.cancel(false);
        userTask = null;

        relationship = null;
    }

    /**
     * Sets up visibility of follow/unfollow button based on given relationship object
     * @param relationship {Relationship} - info about friendship between current and target user
     */
    private void setRelationship(final Relationship relationship) {
        if (relationship == null || !isAdded()) return;
        this.relationship = relationship;

        if (action_follow != null)      action_follow.setVisible( !relationship.isSourceFollowingTarget());
        if (action_unfollow != null)    action_unfollow.setVisible(relationship.isSourceFollowingTarget());

        if (action_mute != null)   action_mute.setVisible( !relationship.isSourceMutingTarget());
        if (action_unmute != null) action_unmute.setVisible(relationship.isSourceMutingTarget());

        if (action_block != null)   action_block.setVisible( !relationship.isSourceBlockingTarget());
        if (action_unblock != null) action_unblock.setVisible(relationship.isSourceBlockingTarget());

        if (action_message != null) action_message.setVisible(relationship.canSourceDm());
    }

    /**
     * Sets all fields with info about given user
     * @param user {User} user to attach
     */
    private void setUser(final User user) {
        if (user == null || !isAdded()) return;
        Log.d(TAG, "Setting up user");
        this.user = user;
        this.userName = user.getScreenName();
        this.userID = user.getId();

        binding.setUser(user);

        if (user.getLocation().length() > 0) {
            binding.fragmentUserLocation.setVisibility(View.VISIBLE);
            binding.fragmentUserLocation.setText(user.getLocation());
            binding.fragmentUserLocation.setOnClickListener(this);
        }

        if (user.getURLEntity().getExpandedURL().length() > 0) {
            binding.fragmentUserLink.setVisibility(View.VISIBLE);
            binding.fragmentUserLink.setText(user.getURLEntity().getExpandedURL());
            binding.fragmentUserLink.setOnClickListener(this);
        }

        if (action_reply != null) {
            action_reply.setVisible(user.getId() != SharedPreferenceHelper.getInstance().getCurrentAccountID());
        }
    }

    private static final String TAG = "UserFragment";

    public static void showUser(final long userID) {
        showUser(new UserElement(userID, "", "", ""));
    }
    public static void showUser(final String username) {
        if (username != null) showUser(new UserElement(0, "", username, ""));
    }
    public static void showUser(final UserElement user) {
        Log.d(TAG, user.username);

        final Fragment fragment = new UserFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("name", user.name);
        bundle.putString("username", user.username);
        bundle.putString("image", user.image);
        bundle.putLong("userID", user.userID);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    @Override
    public void onClick(final View v) {
        if (user == null) return;
        switch (v.getId()) {
            case R.id.fragment_user_link:
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(user.getURLEntity().getExpandedURL())));
            break;
            case R.id.fragment_user_location:
                final Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + user.getLocation()));
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);
            break;
        }
    }

    private class ChangeBlockStatusTask extends AbsUserLoadTask {
        private ChangeBlockStatusTask(final UserFragment fragment, final boolean create) {
            super(fragment);
            this.createID = R.string.success_blocked;
            this.destroyID = R.string.success_unblocked;
            this.create = create;
        }

        @Override
        protected User loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().changeBlockStatus(params[0], create);
        }
    }
    private class ChangeMuteStatusTask extends AbsUserLoadTask {
        private ChangeMuteStatusTask(final UserFragment fragment, final boolean create) {
            super(fragment);
            this.createID = R.string.success_muted;
            this.destroyID = R.string.success_unmuted;
            this.create = create;
        }

        @Override
        protected User loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().changeMuteStatus(params[0], create);
        }
    }
    private class SpamReportTask extends AbsUserLoadTask {
        private SpamReportTask(final UserFragment fragment) {
            super(fragment);
            this.createID = R.string.success_spam_reported;
            this.create = true;
        }

        @Override
        protected User loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().reportSpam(params[0]);
        }
    }

    /**
     * Changes friendship status, if second argument > 0 creates friendship destroys otherwise
     */
    private class ChangeFriendshipTask extends AbsUserLoadTask {
        private ChangeFriendshipTask(final UserFragment fragment, final boolean create) {
            super(fragment);
            this.createID = R.string.success_followed;
            this.destroyID = R.string.success_unfollowed;
            this.create = create;
        }

        @Override
        protected User loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().changeFriendship(params[0], create);
        }
    }

    private abstract class AbsUserLoadTask extends AbsTwitterDataLoadTask<Long, User, UserFragment> {
        protected int createID = 0, destroyID;
        protected boolean create;
        private AbsUserLoadTask(final UserFragment fragment) { super(fragment); }

        @Override
        protected void obtainData(final UserFragment fragment, final User data) {
            fragment.setUser(data);

            new RelationshipLoaderTask(fragment).execute(
                    SharedPreferenceHelper.getInstance().getLong(SharedPreferenceHelper.CURRENT_ACCOUNT_ID),
                    data.getId()
            );

            if (createID != 0) {
                NotificationMgr.getInstance().showSnackbar(
                        String.format(getString(create ? createID : destroyID), user.getScreenName()), null);
            }
        }
    }

    /**
     * Loads info about user and sets up views
     *  then calls RelationshipTask
     */
    private class UserLoaderTask extends AbsUserLoadTask {
        private final String username;
        private UserLoaderTask(final UserFragment fragment, final String username) {
            super(fragment);
            this.username = username;
        }

        @Override
        protected User loadData(final Long[] params) throws TwitterException {
            if (params[0] == 0) {
                return TwitterMgr.getInstance().showUser(username);
            } else {
                return TwitterMgr.getInstance().showUser(params[0]);
            }
        }

        @Override
        protected void obtainData(final UserFragment fragment, final User data) {
            super.obtainData(fragment, data);
            if (fragment.refreshTask == null) {
                fragment.refreshTask = fragment.createRefreshTask(POSITION_END);
                fragment.refreshTask.execute(0L, 0L);
            }
        }
    }

    /**
     * Loads info about friendship and sets up views
     */
    private final class RelationshipLoaderTask extends AbsTwitterDataLoadTask<Long, Relationship, UserFragment> {
        private RelationshipLoaderTask(final UserFragment fragment) {
            super(fragment);
        }

        @Override
        protected Relationship loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().getRelationship(params[0], params[1]);
        }

        @Override
        protected void obtainData(final UserFragment fragment, final Relationship data) {
            fragment.setRelationship(data);
        }
    }

    /**
     * Loads user timeline
     */
    private final class UserTimelineTask extends AbsTweetRecycleViewRefreshTask {
        final long userID;
        private UserTimelineTask(final UserFragment fragment, final long userID, final int position) {
            super(fragment, position, Source.API);
            this.userID = userID;
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(final Long... params) {
            try {
                return TwitterMgr.getInstance().getUserTimeline(userID, params[0], params[1]);
            } catch (final TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
