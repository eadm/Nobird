package ru.eadm.nobird.fragment.implementation.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.notification.NotificationMgr;

public abstract class AbsTweetRecycleViewRefreshTask extends AsyncTask<Long, Void, ArrayList<TweetElement>> {

    public enum Source { // to get statuses from db an network
        CACHE,
        API
    }

    protected WeakReference<AbsTweetRecycleViewFragment> fragmentWeakReference;
    protected TaskState taskState;

    protected final int position;
    protected final Source source;

    public AbsTweetRecycleViewRefreshTask(
            final AbsTweetRecycleViewFragment fragment,
            final int position,
            final Source source) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.position = position;
        this.source = source;
        taskState = TaskState.PROCESSING;
    }

    public TaskState getState() {
        return taskState;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fragmentWeakReference.get() != null) {
            fragmentWeakReference.get().setRefreshing(true);
        }
    }

    @Override
    protected void onPostExecute(final ArrayList<TweetElement> data) {
        final AbsTweetRecycleViewFragment fragment = fragmentWeakReference.get();
        if (fragment == null) return;
        if (data == null) {
            taskState = TaskState.ERROR;
            NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, null);
        } else {
            if (fragment.adapter != null) {
                if (position == AbsTweetRecycleViewFragmentNested.POSITION_END) { // adding to end of list
                    fragment.adapter.addAll(data);
                } else if (position == AbsTweetRecycleViewFragmentNested.POSITION_START) { // adding to start
                    fragment.adapter.addAll(0, data);
//                    fragment.showCounter(data.size());
                }
            }
            taskState = TaskState.COMPLETED;
        }
        if (fragment.adapter != null) {
            fragment.setRefreshing(false);
        }
    }
}
