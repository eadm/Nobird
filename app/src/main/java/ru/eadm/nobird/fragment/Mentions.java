package ru.eadm.nobird.fragment;

import android.util.Log;

import java.util.ArrayList;

import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragmentNested;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import twitter4j.TwitterException;

public final class Mentions extends AbsTweetRecycleViewFragmentNested {
    public static final String TAG = "mentions_fragment";

    @Override
    protected AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source) {
        return new MentionsDataGetTask(this, position, source);
    }

    @Override
    protected AbsTweetRecycleViewState getState() {
        return MentionFragmentState.getInstance();
    }

    private final class MentionsDataGetTask extends AbsTweetRecycleViewRefreshTask {
        private MentionsDataGetTask(final Mentions fragment, final int position, final Source source) {
            super(fragment, position, source);
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(final Long... params) {
            if (source == Source.CACHE &&
                    DBMgr.getInstance().getCachedStatusesCount(DBMgr.TYPE_MENTIONS) != 0) {
                return DBMgr.getInstance().getCachedStatuses(DBMgr.TYPE_MENTIONS);
            } else {
                try {
                    return TwitterMgr.getInstance().getMentionsTimeline(params[0], params[1]);
                } catch (TwitterException e) {
                    Log.e(Feed.TAG, "Error: " + e.getMessage());
                }
            }
            return null;
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
