package ru.eadm.nobird.fragment;

import android.util.Log;

import java.util.ArrayList;

import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import twitter4j.TwitterException;

public final class Mentions extends AbsTweetRecycleViewFragment{
    public static final String TAG = "mentions_fragment";

    @Override
    public void onRefresh() {
        // do some refresh
        Log.d(Feed.TAG, "refreshing");
//        page.setRefreshing(false); // stop refresh animation
    }

    @Override
    protected AbsTweetRecycleViewRefreshTask createTask() {
        return new MentionsDataGetTask(this);
    }

    @Override
    protected AbsTweetRecycleViewState getState() {
        return MentionFragmentState.getInstance();
    }

    private final class MentionsDataGetTask extends AbsTweetRecycleViewRefreshTask {
        private MentionsDataGetTask(final Mentions fragment) {
            super(fragment);
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(Void... params) {
            try {
                return TwitterMgr.getInstance().getMentionsTimeline(0, 0);
            } catch (TwitterException e) {
                Log.e(Mentions.TAG, "Error: " + e.getMessage());
                return null;
            }
        }
    }

    private final static class MentionFragmentState extends AbsTweetRecycleViewState {
        private static MentionFragmentState instance;

        private static MentionFragmentState getInstance() {
            if (instance == null) instance = new MentionFragmentState();
            return instance;
        }
    }
}
