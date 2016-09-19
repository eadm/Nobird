package ru.eadm.nobird.fragment;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.design.animation.OnEndAnimationListener;
import ru.eadm.nobird.design.animation.OnStartAnimationListener;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewFragmentNested;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;
import twitter4j.TwitterException;

public final class Feed extends AbsTweetRecycleViewFragmentNested {
    public static final String TAG = "feed_fragment";

    @Override
    protected AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source) {
        return new FeedDataGetTask(this, position, source);
    }

    private final class FeedDataGetTask extends AbsTweetRecycleViewRefreshTask {
        private FeedDataGetTask(final Feed feed, final int position, final Source source) {
            super(feed, position, source);
        }

        @Override
        protected ArrayList<TweetElement> doInBackground(final Long... params) {
            if (source == Source.CACHE &&
                    DBMgr.getInstance().getCachedStatusesCount(DBMgr.TYPE_FEED) != 0) {
               return DBMgr.getInstance().getCachedStatuses(DBMgr.TYPE_FEED);
            } else {
                try {
                    return TwitterMgr.getInstance().getHomeTimeline(params[0], params[1]);
                } catch (TwitterException e) {
                    Log.e(Feed.TAG, "Error: " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<TweetElement> data) {
            super.onPostExecute(data);
            final AbsTweetRecycleViewFragmentNested feed = ((Feed)fragmentWeakReference.get());
            if (source == Source.API && position == POSITION_START && feed != null && data != null && !data.isEmpty()) {
                feed.showCounter(data.size());
            }
        }
    }
}
