package ru.eadm.nobird.fragment;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBHelper;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.databinding.FragmentStatusBinding;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragmentNested;
import ru.eadm.nobird.fragment.task.AbsTwitterDataLoadTask;
import ru.eadm.nobird.fragment.task.TaskState;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatusFragment extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "StatusFragment";

    private PageableArrayList<TweetElement> data;
    private StatusLoadTask statusLoadTask;
    private RepliesLoadTask repliesLoadTask;

    private long statusID;
    private int conversationEnd = 0;
    private String username;

    private MenuItem action_delete;

    private FragmentStatusBinding binding;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_status, container, false);

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.fragmentStatusToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshLayout = binding.fragmentStatusSwipeRefreshLayout;
        refreshLayout.setOnRefreshListener(this);

        if (data == null && statusLoadTask == null) {
            statusLoadTask = new StatusLoadTask(this);
            statusLoadTask.execute(statusID);
        }

        adapter = new TweetRecycleViewAdapter(data);
        data = adapter.getData();

        initRecycleView(binding);

        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
        refreshLayout = null;
    }

    private void initRecycleView(final FragmentStatusBinding binding) {
        final RecyclerView recyclerView = binding.fragmentStatusTimeline;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean scrolledToEnd = false;

            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return; // ignore if you scrolls up
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.getItemCount() - recyclerView.getChildCount()
                        <= layoutManager.findFirstVisibleItemPosition()) {
                    if (!scrolledToEnd) onScrolledToEnd();
                    scrolledToEnd = true;
                } else {
                    scrolledToEnd = false;
                }
            }

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ImageMgr.getInstance().listener.onScrollStateChanged(null, newState);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        this.statusID = getArguments().getLong("statusID");
    }

//    TODO: NOT IMPLEMENTED IN TWITTER API
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_status_menu, menu);
        action_delete = menu.findItem(R.id.action_destroy_status);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_show_likers:
//                return true;
//            case R.id.action_show_retweeters:
//                return true;
            case R.id.action_destroy_status:
                new DestroyStatusTask().execute(getArguments().getLong("statusID"));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRefresh() {
        if (repliesLoadTask != null &&
                repliesLoadTask.getState() == TaskState.COMPLETED) {
            repliesLoadTask = new RepliesLoadTask(this, username, statusID, POSITION_START);
            repliesLoadTask.execute((adapter.getItemCount() == conversationEnd) ? conversationEnd : adapter.getData().get(conversationEnd).tweetID, 0L);
        } else {
            refreshLayout.setRefreshing(false);
        }
    }
    private void onScrolledToEnd() {
        Log.d(TAG, "onScrolledToEnd");
        if (repliesLoadTask != null &&
                repliesLoadTask.getState() == TaskState.COMPLETED) {
            repliesLoadTask = new RepliesLoadTask(this, username, statusID, POSITION_END);
            repliesLoadTask.execute(0L, adapter.getData().get(adapter.getData().size() - 1).tweetID - 1);
        }
    }


    public static void showStatus(final long statusID) {
        Log.d(TAG, "showStatus: " + statusID);

        final Bundle bundle = new Bundle();
        final Fragment fragment = new StatusFragment();

        bundle.putLong("statusID", statusID);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }
    public static void showStatus(final TweetElement tweet) {
        showStatus(tweet.getID());
    }

    /**
     * Requests and sets status info and recursively loads it's replies
     */
    private class StatusLoadTask extends AbsTwitterDataLoadTask<Long, Status, StatusFragment> {
        private StatusLoadTask(final StatusFragment fragment) { super(fragment); }

        @Override
        protected twitter4j.Status loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().showStatus(params[0]);
        }

        @Override
        protected void obtainData(final StatusFragment fragment, final twitter4j.Status status) {
            if (!fragment.isAdded()) return;

            final TweetElement tweetElement = TwitterUtils.statusToTweetElement(status, fragment.username == null);
            if (fragment.username == null) {
                if (fragment.action_delete != null && status.getUser().getId() == PreferenceMgr.getInstance().getCurrentAccountID()) {
                    action_delete.setVisible(true);
                }

                fragment.username = tweetElement.user.username;
                fragment.repliesLoadTask = new RepliesLoadTask(
                        fragment, tweetElement.user.username,
                        fragment.statusID, POSITION_END);
                fragment.repliesLoadTask.execute(0L, 0L);
            }

            fragment.adapter.add(0, tweetElement);
            fragment.adapter.notifyItemInserted(0);
            conversationEnd++;

            if (status.getInReplyToStatusId() != -1) {
                new StatusLoadTask(fragment).execute(status.getInReplyToStatusId());
            }
        }
    }
    private final class RepliesLoadTask extends AbsTwitterDataLoadTask<Long, ArrayList<TweetElement>, StatusFragment> {
        final long statusID;
        final String username;
        final int pos;
        private RepliesLoadTask(final StatusFragment fragment, final String username, final long statusID, final int pos) {
            super(fragment);
            this.username = username;
            this.statusID = statusID;
            this.pos = pos;
            fragment.setRefreshing(true);
        }

        @Override
        protected ArrayList<TweetElement> loadData(final Long[] params) throws TwitterException {
            return TwitterMgr.getInstance().getReplies(statusID, username, params[0], params[1]);
        }

        @Override
        protected void onPostExecute(final ArrayList<TweetElement> data) {
            if (fragmentWeakReference.get() != null) {
                if (data != null) {
                    obtainData(fragmentWeakReference.get(), data);
                    taskState = TaskState.COMPLETED;
                }
                fragmentWeakReference.get().setRefreshing(false);
            }
            if (taskState != TaskState.COMPLETED) taskState = TaskState.ERROR;
        }

        @Override
        protected void obtainData(final StatusFragment fragment, final ArrayList<TweetElement> data) {
            if (!fragment.isAdded()) return; // if fragment removed
            if (pos == AbsTweetRecycleViewFragmentNested.POSITION_START) {
                fragment.adapter.addAll(conversationEnd, data);
                fragment.adapter.notifyItemRangeInserted(conversationEnd, data.size());
            } else {
                final int start = fragment.adapter.getItemCount();
                fragment.adapter.addAll(data);
                fragment.adapter.notifyItemRangeInserted(start, data.size());
            }
        }
    }


    /**
     * Task to destroy statuses
     */
    private final class DestroyStatusTask extends AsyncTask<Long, Void, Status> {
        @Override
        protected twitter4j.Status doInBackground(final Long... params) {
            try {
                final twitter4j.Status status = TwitterMgr.getInstance().destroyStatus(params[0]);
                DBMgr.getInstance().removeElementFromTableByID(DBHelper.TABLE_TWEETS, "tweetID", status.getId());
                    // removes destroyed element from cache

                return status;
            } catch (final TwitterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final twitter4j.Status status) {
            FragmentMgr.getInstance().back();
            NotificationMgr.getInstance().showSnackbar(status != null ? R.string.success_status_destroyed : R.string.error_twitter_api, null);
        }
    }
}
