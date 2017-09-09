package ru.nobird.android.fragment;

import java.util.ArrayList;

import ru.nobird.android.R;
import ru.nobird.android.data.database.DBMgr;
import ru.nobird.android.data.twitter.TwitterMgr;
import ru.nobird.android.data.types.TweetElement;
import ru.nobird.android.fragment.implementation.task.AbsTweetRecycleViewFragmentNested;
import ru.nobird.android.fragment.implementation.task.AbsTweetRecycleViewRefreshTask;
import ru.nobird.android.notification.NotificationMgr;
import twitter4j.TwitterException;

public final class Mentions extends AbsTweetRecycleViewFragmentNested {
    public static final String TAG = "mentions_fragment";

    @Override
    protected AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source) {
        return new MentionsDataGetTask(this, position, source);
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
                } catch (final TwitterException e) {
                    e.printStackTrace();
                    NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, null);
                }
            }
            return null;
        }
    }
}
