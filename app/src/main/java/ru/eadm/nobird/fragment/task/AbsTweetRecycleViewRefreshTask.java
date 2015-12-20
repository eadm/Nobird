package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.notification.NotificationMgr;

public abstract class AbsTweetRecycleViewRefreshTask extends AsyncTask<Long, Void, ArrayList<TweetElement>> {
    enum TaskState {
        PROCESSING,
        COMPLETED,
        ERROR
    }

    public enum Source { // to get statuses from db an network
        CACHE,
        API
    }

    private WeakReference<AbsTweetRecycleViewFragment> fragment;
    private TaskState taskState;

    private final int position;
    protected final Source source;

    public AbsTweetRecycleViewRefreshTask(
            final AbsTweetRecycleViewFragment fragment,
            final int position,
            final Source source) {
        this.fragment = new WeakReference<>(fragment);
        this.position = position;
        this.source = source;
        taskState = TaskState.PROCESSING;
    }

    public void attachFragment(final AbsTweetRecycleViewFragment fragment) {
        this.fragment = new WeakReference<>(fragment);
        if (getState() == TaskState.PROCESSING) {
            fragment.setRefreshing(true);
        }
    }

    public TaskState getState() {
        return taskState;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fragment.get() != null) {
            fragment.get().setRefreshing(true);
        }
    }

    protected abstract ArrayList<TweetElement> doInBackground(Long... params);

    @Override
    protected void onPostExecute(ArrayList<TweetElement> data) {
        if (data == null) {
            taskState = TaskState.ERROR;
            if (fragment.get() != null) {
                NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, fragment.get().refreshLayout);
            }
        } else {
            if (fragment.get() != null) {
                if (position == AbsTweetRecycleViewFragment.POSITION_END) {
                    final int start = fragment.get().adapter.getItemCount();
                    fragment.get().adapter.addAll(data);
                    fragment.get().adapter.notifyItemRangeInserted(start, data.size());
                } else if (position == AbsTweetRecycleViewFragment.POSITION_START) {
                    fragment.get().adapter.addAll(0, data);
                    fragment.get().adapter.notifyItemRangeInserted(0, data.size());
                }
            }
            taskState = TaskState.COMPLETED;
        }
        fragment.get().setRefreshing(false);

    }
}
