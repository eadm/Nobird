package ru.eadm.nobird.fragment;

import android.util.Log;

import java.util.ArrayList;

import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import twitter4j.TwitterException;

public final class Feed extends AbsTweetRecycleViewFragment{
    public static final String TAG = "feed_fragment";

    @Override
    public void onRefresh() {
        // do some refresh
        Log.d(Feed.TAG, "refreshing");
//        page.setRefreshing(false); // stop refresh animation
    }

    @Override
    protected AbsTweetRecycleViewRefreshTask createTask() {
        return new FeedDataGetTask(this);
    }

    @Override
    protected AbsTweetRecycleViewState getState() {
        return FeedFragmentState.getInstance();
    }

    private final class FeedDataGetTask extends AbsTweetRecycleViewRefreshTask {
        private FeedDataGetTask(final Feed feed) {
            super(feed);
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(Void... params) {
            try {
                return TwitterMgr.getInstance().getHomeTimeline(0, 0);
            } catch (TwitterException e) {
                Log.e(Feed.TAG, "Error: " + e.getMessage());
                return null;
            }
        }
    }

    private final static class FeedFragmentState extends AbsTweetRecycleViewState {
        private static FeedFragmentState instance;

        private static FeedFragmentState getInstance() {
            if (instance == null) instance = new FeedFragmentState();
            return instance;
        }
    }
}
