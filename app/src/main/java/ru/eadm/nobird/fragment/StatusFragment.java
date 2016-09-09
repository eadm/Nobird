package ru.eadm.nobird.fragment;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBHelper;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.data.twitter.utils.TwitterExceptionResolver;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.databinding.FragmentStatusBinding;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import ru.eadm.nobird.fragment.task.AbsTwitterDataLoadTask;
import ru.eadm.nobird.fragment.task.TaskState;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatusFragment extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "StatusFragment";

    private StatusLoadTask statusLoadTask;

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

        setRefreshLayout(binding.fragmentStatusSwipeRefreshLayout);
        setRecyclerView(binding.fragmentStatusTimeline);

        if (statusLoadTask == null) {
            statusLoadTask = new StatusLoadTask(this);
            statusLoadTask.execute(statusID);
        }

        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            case R.id.action_destroy_status:
                new DestroyStatusTask().execute(getArguments().getLong("statusID"));
                return true;
            default:
                return false;
        }
    }

    @Override
    protected AbsTweetRecycleViewRefreshTask createRefreshTask(int pos) {
        return new RepliesLoadTask(this, username, statusID, pos);
    }

    @Override
    public void onRefresh() {
        if (refreshTask != null &&
                refreshTask.getState() == TaskState.COMPLETED) {
            refreshTask = createRefreshTask(POSITION_START);
            refreshTask.execute((adapter.getItemCount() == conversationEnd) ? conversationEnd : adapter.getData().get(conversationEnd).tweetID, 0L);
        } else {
            refreshLayout.setRefreshing(false);
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
                fragment.refreshTask = new RepliesLoadTask(
                        fragment, tweetElement.user.username,
                        fragment.statusID, POSITION_END);
                fragment.refreshTask.execute(0L, 0L);
            }

            fragment.adapter.add(0, tweetElement);
            fragment.adapter.notifyItemInserted(0);
            conversationEnd++;

            if (status.getInReplyToStatusId() != -1) {
                new StatusLoadTask(fragment).execute(status.getInReplyToStatusId());
            }
        }
    }
    private final class RepliesLoadTask extends AbsTweetRecycleViewRefreshTask {
        final long statusID;
        final String username;
        private RepliesLoadTask(final StatusFragment fragment, final String username, final long statusID, final int pos) {
            super(fragment, pos, Source.API);
            this.username = username;
            this.statusID = statusID;
            fragment.setRefreshing(true);
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(Long... params) {
            try {
                return TwitterMgr.getInstance().getReplies(statusID, username, params[0], params[1]);
            } catch (final TwitterException e) {
                NotificationMgr.getInstance().showSnackbar(TwitterExceptionResolver.resolve(e), null);
                e.printStackTrace();
                return null;
            }
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

        protected void obtainData(final AbsTweetRecycleViewFragment fragment, final ArrayList<TweetElement> data) {
            if (!fragment.isAdded()) return; // if fragment removed
            if (position == POSITION_START) {
                fragment.getAdapter().addAll(conversationEnd, data);
                fragment.getAdapter().notifyItemRangeInserted(conversationEnd, data.size());
            } else {
                final int start = fragment.getAdapter().getItemCount();
                fragment.getAdapter().addAll(data);
                fragment.getAdapter().notifyItemRangeInserted(start, data.size());
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
