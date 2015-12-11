package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.notification.NotificationMgr;

public abstract class AbsTweetRecycleViewRefreshTask extends AsyncTask<Void, Void, ArrayList<TweetElement>> {
    enum TaskState {
        PROCESSING,
        COMPLETED,
        ERROR
    }

    private WeakReference<AbsTweetRecycleViewFragment> fragment;
    private TaskState taskState;

    public AbsTweetRecycleViewRefreshTask(final AbsTweetRecycleViewFragment fragment) {
        this.fragment = new WeakReference<>(fragment);
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

    protected abstract ArrayList<TweetElement> doInBackground(Void... params);

    @Override
    protected void onPostExecute(ArrayList<TweetElement> data) {
        if (data == null) {
            taskState = TaskState.ERROR;
            if (fragment.get() != null) {
                NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, fragment.get().refreshLayout);
            }
        } else {
            if (fragment.get() != null) {
                for (final TweetElement s : data) {
                    fragment.get().adapter.add(s);
                    fragment.get().adapter.notifyItemInserted(fragment.get().adapter.getItemCount() - 1);
                }
            }
            taskState = TaskState.COMPLETED;
        }
        fragment.get().setRefreshing(false);

    }
}
