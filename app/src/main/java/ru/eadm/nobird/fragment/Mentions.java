package ru.eadm.nobird.fragment;

import android.util.Log;

import java.util.ArrayList;

import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragment;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;

public final class Mentions extends AbsTweetRecycleViewFragment{
    public static final String TAG = "mentions_fragment";

    private ArrayList<TweetElement> getData() {
        final ArrayList<TweetElement> data = new ArrayList<>();
        Log.d(Feed.TAG, "Data recreated");
        for (int i = 0; i < 1000; i++) data.add(new TweetElement("Item: " + i));
        return data;
    }

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
        return FeedFragmentState.getInstance();
    }

    private final class MentionsDataGetTask extends AbsTweetRecycleViewRefreshTask {
        private MentionsDataGetTask(final Mentions fragment) {
            super(fragment);
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(Void... params) {
            for (int i = 0; i < 5; i ++) {
                try {
                    Thread.sleep(1000);
                    Log.d(Feed.TAG, "Wait: " + (i + 1) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return getData();
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
