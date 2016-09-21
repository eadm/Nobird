package ru.eadm.nobird.data.types;


import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.dialog.TweetActionsDialogFragment;
import ru.eadm.nobird.fragment.ComposeFragment;
import ru.eadm.nobird.fragment.implementation.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.implementation.task.AbsTwitterDataLoadTask;
import twitter4j.TwitterException;

/**
 * To perform operations on TweetElement
 */
public class TweetElementObserver {
    private static final String TAG = "TweetElementObserver";

    public static void export(final TweetElement element) {
        TweetActionsDialogFragment.showExportDialog(element);
    }

    public static void like(final TweetRecycleViewAdapter adapter, final TweetElement element) {
        if (element.lock) return;
        element.lock = true;
        new LikeTask(adapter).execute(element.getID(), element.status.isFavorited() ? 0 : 1L);
    }

    public static void retweet(final TweetRecycleViewAdapter adapter, final TweetElement element) {
        if (element.lock) return;
        element.lock = true;
        new RetweetTask(adapter).execute(element.getID());
    }

    public static void reply(final TweetElement element) {
        ComposeFragment.open(element.getID(), "@" + element.user.username + " ");
    }


    /**
     * Task to retweet statuses
     */
    private static class RetweetTask extends AbsTwitterDataLoadTask<Long, TweetElement, TweetRecycleViewAdapter>  {
        protected int pos = -1;
        private RetweetTask(final TweetRecycleViewAdapter adapter) {
            super(adapter);
        }

        @Override
        protected TweetElement loadData(final Long[] params) throws TwitterException {
            final TweetElement data = TwitterUtils.statusToTweetElement(TwitterMgr.getInstance().retweet(params[0]), true);
            if (fragmentWeakReference.get() != null) pos = fragmentWeakReference.get().lookup(params[0]);
            return data;
        }

        @Override
        protected void obtainData(final TweetRecycleViewAdapter adapter, final TweetElement data) {
            if (pos != -1) {
                adapter.set(pos, data);
            }
        }
    }

    /**
     * Task to like target status, if second param == 1 creates like otherwise destroys it
     */
    private static class LikeTask extends RetweetTask {
        private LikeTask(final TweetRecycleViewAdapter adapter) { super(adapter); }

        @Override
        protected TweetElement loadData(final Long[] params) throws TwitterException {
            final TweetElement data =  TwitterUtils.statusToTweetElement(TwitterMgr.getInstance().like(params[0], params[1] == 1), true);
            if (fragmentWeakReference.get() != null) pos = fragmentWeakReference.get().lookup(params[0]);
            return data;
        }
    }
}
