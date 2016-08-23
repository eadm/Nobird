package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.Element;
import ru.eadm.nobird.notification.NotificationMgr;

public abstract class AbsRecycleViewRefreshTask<E extends Element> extends AsyncTask<Long, Void, PageableArrayList<E>> {

    public enum Position {
        START,
        END
    }

    private TaskState taskState;
    private final Position position;

    private final WeakReference<AbsRecycleViewFragment<E>> fragment;

    public AbsRecycleViewRefreshTask(final AbsRecycleViewFragment<E> fragment, final Position position) {
        this.fragment = new WeakReference<>(fragment);
        this.position = position;
        taskState = TaskState.PROCESSING;
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

    @Override
    protected void onPostExecute(final PageableArrayList<E> data) {
        if (data == null) {
            taskState = TaskState.ERROR;
            if (fragment.get() != null) {
                NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, null);
            }
        } else {
            if (fragment.get() != null) {
                if (position == Position.END) { // adding to end of list
                    final int start = fragment.get().adapter.getItemCount();
                    fragment.get().adapter.addAll(data);
                    fragment.get().adapter.notifyItemRangeInserted(start, data.size());
                } else if (position == Position.START) { // adding to start
                    fragment.get().adapter.addAll(0, data);
                    fragment.get().adapter.notifyItemRangeInserted(0, data.size());
                }
                fragment.get().adapter.getData().setCursors(data); // setting new cursors
            }
            taskState = TaskState.COMPLETED;
        }
        fragment.get().setRefreshing(false);
    }
}
