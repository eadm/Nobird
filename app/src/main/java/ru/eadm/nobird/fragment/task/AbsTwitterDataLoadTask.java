package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ru.eadm.nobird.data.twitter.utils.TwitterExceptionResolver;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.TwitterException;

/**
 * Abstract class to load simple data with TwitterMgr
 * @param <A> type of arguments
 * @param <D> type of data to return
 * @param <F> type of fragment or type of object to clojure
 */
public abstract class AbsTwitterDataLoadTask <A, D, F> extends AsyncTask<A, Void, D> {
    protected WeakReference<F> fragmentWeakReference;
    protected TaskState taskState;

    protected AbsTwitterDataLoadTask(final F fragment) {
        taskState = TaskState.PROCESSING;
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    public TaskState getState() {
        return taskState;
    }

    protected abstract D loadData(final A[] params) throws TwitterException;
    protected abstract void obtainData(final F fragment, final D data);

    @SafeVarargs
    @Override
    protected final D doInBackground(final A... params) {
        try {
            return loadData(params);
        } catch (final TwitterException e) {
            NotificationMgr.getInstance().showSnackbar(TwitterExceptionResolver.resolve(e), null);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final D data) {
        final F fragment = fragmentWeakReference.get();
        if (fragment != null && data != null) {
            taskState = TaskState.COMPLETED;
            obtainData(fragment, data);
        } else {
            taskState = TaskState.ERROR;
        }
    }
}
