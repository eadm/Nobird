package ru.eadm.nobird.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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


import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserFragment extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private long userID = 0;

    private TextView name, username, description, followers, following;
    private ImageView background, user_image;

    private View user_info_small;
    private TextView name_small, username_small, location, link;
    private ImageView user_image_small;

    private User user;
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
//        toolbar.setTitle(getArguments().getString("name"));
        toolbar.setTitle("");

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        appBarLayout = (AppBarLayout) page.findViewById(R.id.fragment_user_appbar_layout);

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
            new UserLoaderTask(this).execute(userID);
        }

        return page;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        userID = getArguments().getLong("userID");
        adapter = new TweetRecycleViewAdapter();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_block:
                Log.d(TAG, "We want block him");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        if (timelineTask != null &&
                timelineTask.getState() == AbsTweetRecycleViewRefreshTask.TaskState.COMPLETED) {
            timelineTask = new UserTimelineTask(this, userID, POSITION_END);
            timelineTask.execute((adapter.getItemCount() == 0) ? 0 : adapter.getData().get(0).tweetID, 0L);
        }
    }

    private void onScrolledToEnd() {
        if (timelineTask != null &&
            timelineTask.getState() == AbsTweetRecycleViewRefreshTask.TaskState.COMPLETED) {
            timelineTask = new UserTimelineTask(this, userID, POSITION_END);
            timelineTask.execute(0L, adapter.getData().get(adapter.getData().size() - 1).tweetID - 1);
        }
    }



    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putBoolean("collapsed", appBarLayout.);
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
    }

    @Override
    public void onDestroy() { // to avoid leaks it's calls when fragment completely destroyed
        super.onDestroy();
        data = null;
        timelineTask.cancel(false);
        timelineTask = null;
        user = null;
    }

    private void setUser(final User user) {
        if (user == null || !isAdded()) return;
        this.user = user;
        ImageMgr.getInstance().displayRoundImage(user.getOriginalProfileImageURLHttps(), user_image);
        ImageMgr.getInstance().displayRoundImage(user.getOriginalProfileImageURLHttps(), user_image_small);

        name.setText(user.getName());
        name_small.setText(user.getName());

        username.setText(String.format(getString(R.string.username_placeholder), user.getScreenName().toLowerCase()));
        username_small.setText(String.format(getString(R.string.username_placeholder), user.getScreenName().toLowerCase()));

        description.setText(user.getDescription());
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

    // UI Methods
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

    private final class UserLoaderTask extends AsyncTask<Long, Void, User> {
        private WeakReference<UserFragment> fragmentWeakReference;
        private UserLoaderTask(final UserFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected User doInBackground(Long... params) {
            try {
                return TwitterMgr.getInstance().showUser(params[0]);
            } catch (TwitterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final User user) {
            if (fragmentWeakReference.get() != null) {
                if (user != null) {
                    fragmentWeakReference.get().setUser(user);
                } else {
                    Log.d(TAG, "user lost");
                }
            } else {
                Log.d(TAG, "fragment lost");
            }
        }
    }

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
