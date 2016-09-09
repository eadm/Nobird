package ru.eadm.nobird.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusParser;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import ru.eadm.nobird.fragment.task.AbsTwitterDataLoadTask;
import ru.eadm.nobird.fragment.task.TaskState;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserFragment extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private long userID = 0;

    private TextView name, username, description, followers, following;
    private ImageView background, user_image;

    private View user_info_small;
    private TextView name_small, username_small, location, link;
    private ImageView user_image_small;
    private MenuItem
            action_follow,
            action_unfollow,

            action_mute,
            action_unmute,

            action_block,
            action_unblock,

            action_message;

    private Relationship relationship;
    private User user;
    private AbsTwitterDataLoadTask<Long, ?, UserFragment> userTask;
    private PageableArrayList<TweetElement> data;

    private UserTimelineTask timelineTask;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View page = inflater.inflate(R.layout.fragment_user, container, false);

        refreshLayout = (SwipeRefreshLayout) page.findViewById(R.id.fragment_user_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        final Toolbar toolbar = (Toolbar) page.findViewById(R.id.fragment_user_toolbar);
        toolbar.setTitle("");

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (data == null) {
            timelineTask = new UserTimelineTask(this, userID, POSITION_END);
            timelineTask.execute(0L, 0L);
        }
        adapter = new TweetRecycleViewAdapter(data);
        data = adapter.getData();

        initRecycleView(page);
        initUserFields(page);

        if (user != null) {
            setUser(user);
        } else {
            name.setText(getArguments().getString("name"));
            username.setText(String.format(getString(R.string.username_placeholder), getArguments().getString("username")));
            ImageMgr.getInstance().displayRoundImage(getArguments().getString("image"), user_image);

            if (userTask == null) {
                userTask = new UserLoaderTask(this);
                userTask.execute(userID);
            }
        }

        return page;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userID = getArguments().getLong("userID");
        if (userID != PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID)) {
            setHasOptionsMenu(true);
        }
        adapter = new TweetRecycleViewAdapter();
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

        super.onCreateOptionsMenu(menu, inflater);
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
    public void onRefresh() {
        if (timelineTask != null &&
                timelineTask.getState() == TaskState.COMPLETED) {
            timelineTask = new UserTimelineTask(this, userID, POSITION_START);
            timelineTask.execute((adapter.getItemCount() == 0) ? 0 : adapter.getData().get(0).tweetID, 0L);
        }
    }
    private void onScrolledToEnd() {
        if (timelineTask != null &&
            timelineTask.getState() == TaskState.COMPLETED) {
            timelineTask = new UserTimelineTask(this, userID, POSITION_END);
            timelineTask.execute(0L, adapter.getData().get(adapter.getData().size() - 1).tweetID - 1);
        }
    }

    @Override
    public void onDestroyView() { // to avoid leaks
        super.onDestroyView();
        name = null;
        name_small = null;
        username = null;
        username_small = null;
        description = null;
        followers = null;
        following = null;
        background = null;
        user_image = null;
        user_image_small = null;
        user_info_small = null;
        location = null;
        link = null;

        action_follow = null;
        action_unfollow = null;
        action_mute = null;
        action_unmute = null;
        action_block = null;
        action_unblock = null;
        action_message = null;
    }

    @Override
    public void onDestroy() { // to avoid leaks it's calls when fragment completely destroyed
        super.onDestroy();
        data = null;
        timelineTask.cancel(false);
        timelineTask = null;

        user = null;
        userTask.cancel(false);
        userTask = null;

        relationship = null;
    }

    /**
     * Sets all fields with info about given user
     * @param user {User} user to attach
     */
    private void setUser(final User user) {
        if (user == null || !isAdded()) return;
        Log.d(TAG, "Setting up user");

        this.user = user;
        ImageMgr.getInstance().displayRoundImage(user.getOriginalProfileImageURLHttps(), user_image);
        ImageMgr.getInstance().displayRoundImage(user.getOriginalProfileImageURLHttps(), user_image_small);

        name.setText(user.getName());
        name_small.setText(user.getName());

        username.setText(String.format(getString(R.string.username_placeholder), user.getScreenName().toLowerCase()));
        username_small.setText(String.format(getString(R.string.username_placeholder), user.getScreenName().toLowerCase()));

        description.setText(TwitterStatusParser.getUserDescription(user).getText());
        ImageMgr.getInstance().displayImage(user.getProfileBannerIPadRetinaURL(), background);

        followers.setText(String.format(getString(R.string.digit_placeholder), user.getFollowersCount()));
        following.setText(String.format(getString(R.string.digit_placeholder), user.getFriendsCount()));

        if (user.getLocation().length() > 0) {
            location.setVisibility(View.VISIBLE);
            location.setText(user.getLocation());
            location.setOnClickListener(this);
        }

        if (user.getURLEntity().getExpandedURL().length() > 0) {
            link.setVisibility(View.VISIBLE);
            link.setText(user.getURLEntity().getExpandedURL());
            link.setOnClickListener(this);
        }
    }

    /**
     * Initializes all fields on given page, sets up collapsing toolbar layout and set onclick listeners
     * @param page {View} - page to init
     */
    private void initUserFields(final View page) { // init of user info fields
        user_image = (ImageView) page.findViewById(R.id.fragment_user_image);
        user_image.setOnClickListener(this);

        user_image_small = (ImageView) page.findViewById(R.id.fragment_user_image_small);
//        user_image_small = (ImageView) page.findViewById(R.id.fragment_user_image_small);

        name = (TextView) page.findViewById(R.id.fragment_user_name);
        name_small = (TextView) page.findViewById(R.id.fragment_user_name_small);

        username = (TextView) page.findViewById(R.id.fragment_user_username);
        username_small = (TextView) page.findViewById(R.id.fragment_user_username_small);

        description = (TextView) page.findViewById(R.id.fragment_user_description);
        followers = (TextView) page.findViewById(R.id.fragment_user_followers);
        following = (TextView) page.findViewById(R.id.fragment_user_following);
        location = (TextView) page.findViewById(R.id.fragment_user_location);
        link = (TextView) page.findViewById(R.id.fragment_user_link);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) page.findViewById(R.id.fragment_user_info_container);
        user_info_small = page.findViewById(R.id.fragment_user_info_small);
        ((AppBarLayout) page.findViewById(R.id.fragment_user_appbar_layout))
                .addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                        if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
                            user_info_small.animate().alpha(1).setDuration(getResources().getInteger(R.integer.animation_duration));
                        } else {
                            user_info_small.animate().alpha(0).setDuration(getResources().getInteger(R.integer.animation_duration));
                        }
                    }
                });

        background = (ImageView) page.findViewById(R.id.fragment_user_background);

        page.findViewById(R.id.fragment_user_followers_container).setOnClickListener(this);
        page.findViewById(R.id.fragment_user_following_container).setOnClickListener(this);
    }

    /**
     * Initializes the recycle view on given page, sets adapter and scroll listener
     * @param page {View} - view to find a recycle view
     */
    private void initRecycleView(final View page) {
        final RecyclerView recyclerView = (RecyclerView)page.findViewById(R.id.fragment_user_timeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.getItemCount() - recyclerView.getChildCount()
                        <= layoutManager.findFirstVisibleItemPosition()) {
                    onScrolledToEnd();
                }
            }

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ImageMgr.getInstance().listener.onScrollStateChanged(null, newState);

//                hideCounter();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private static final String TAG = "UserFragment";

    public static void showUser(final long userID) {
        showUser(new UserElement(userID, "", "", ""));
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
            case R.id.fragment_user_followers_container:
                FollowersFragment.showUserFollowers(userID);
            break;
            case R.id.fragment_user_following_container:
                FriendsFragment.showUserFriends(userID);
            break;
            case R.id.fragment_user_link:
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(user.getURLEntity().getExpandedURL())));
            break;
            case R.id.fragment_user_location:
                final Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + user.getLocation()));
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);
            break;
            case R.id.fragment_user_image:
                ImagePreview.openImagePreview(user.getOriginalProfileImageURLHttps());
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
                    PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID),
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
        private UserLoaderTask(final UserFragment fragment) {
            super(fragment);
        }

        @Override
        protected User loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().showUser(params[0]);
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
            } catch (TwitterException e) {
                Log.e(Feed.TAG, "Error: " + e.getMessage());
                return null;
            }
        }
    }
}
