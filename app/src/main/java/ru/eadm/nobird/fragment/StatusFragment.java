package ru.eadm.nobird.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PageableArrayList;
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
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatusFragment extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "StatusFragment";

    private PageableArrayList<TweetElement> data;
    private StatusLoadTask statusLoadTask;
    private RepliesLoadTask repliesLoadTask;

    private long statusID;
    private int conversationEnd = 0;
    private String username;

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

    private void initRecycleView(final FragmentStatusBinding binding) {
        final RecyclerView recyclerView = binding.fragmentStatusTimeline;
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
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        this.statusID = getArguments().getLong("statusID");
    }

    @Override
    public void onRefresh() {
        if (repliesLoadTask != null &&
                repliesLoadTask.getState() == TaskState.COMPLETED) {
            repliesLoadTask = new RepliesLoadTask(this, username, statusID, POSITION_START);
            repliesLoadTask.execute((adapter.getItemCount() == conversationEnd) ? conversationEnd : adapter.getData().get(conversationEnd).tweetID, 0L);
        }
    }

    private void onScrolledToEnd() {
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
        protected void obtainData(final twitter4j.Status status) {
            final TweetElement tweetElement = TwitterUtils.statusToTweetElement(status, true);
            if (fragmentWeakReference.get().username == null) {
                fragmentWeakReference.get().username = tweetElement.user.username;
                fragmentWeakReference.get().repliesLoadTask = new RepliesLoadTask(
                        fragmentWeakReference.get(), tweetElement.user.username,
                        fragmentWeakReference.get().statusID, POSITION_END);
                fragmentWeakReference.get().repliesLoadTask.execute(0L, 0L);
            }

            fragmentWeakReference.get().adapter.add(0, tweetElement);
            conversationEnd++;
            fragmentWeakReference.get().adapter.notifyDataSetChanged();

            if (status.getInReplyToStatusId() != -1) {
                new StatusLoadTask(fragmentWeakReference.get()).execute(status.getInReplyToStatusId());
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
            Log.d(TAG, username);
            return TwitterMgr.getInstance().getReplies(statusID, username, params[0], params[1]);
        }

        @Override
        protected void obtainData(final ArrayList<TweetElement> data) {
            Log.d(TAG, data.size()+"");
            if (pos == AbsTweetRecycleViewFragmentNested.POSITION_START) {
                fragmentWeakReference.get().adapter.addAll(conversationEnd, data);
            } else {
                fragmentWeakReference.get().adapter.addAll(data);
            }
            fragmentWeakReference.get().adapter.notifyDataSetChanged();
            fragmentWeakReference.get().setRefreshing(false);
        }
    }
}
