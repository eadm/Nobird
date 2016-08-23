package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import ru.eadm.nobird.data.twitter.utils.TwitterExceptionResolver;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.TwitterException;

/**
 * Abstract class to load simple data with TwitterMgr
 * @param <A> type of arguments
 * @param <D> type of data to return
 * @param <F> type of fragment
 */
public abstract class AbsTwitterDataLoadTask <A, D, F extends Fragment> extends AsyncTask<A, Void, D> {
    protected WeakReference<F> fragmentWeakReference;
    private TaskState taskState;

    protected AbsTwitterDataLoadTask(final F fragment) {
        taskState = TaskState.PROCESSING;
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    public TaskState getState() {
        return taskState;
    }

    protected abstract D loadData(final A[] params) throws TwitterException;
    protected abstract void obtainData(final D data);

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
        if (fragmentWeakReference.get() != null && data != null) {
            taskState = TaskState.COMPLETED;
            obtainData(data);
        } else {
            taskState = TaskState.ERROR;
        }
    }
}
