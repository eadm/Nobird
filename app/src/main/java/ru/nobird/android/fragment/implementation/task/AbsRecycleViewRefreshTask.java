package ru.nobird.android.fragment.implementation.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ru.nobird.android.R;
import ru.nobird.android.data.PageableArrayList;
import ru.nobird.android.data.types.Element;
import ru.nobird.android.notification.NotificationMgr;

public abstract class AbsRecycleViewRefreshTask<E extends Element> extends AsyncTask<Long, Void, PageableArrayList<E>> {

    public enum Position {
        START,
        END
    }

    private TaskState taskState;
    private final Position position;

    private final WeakReference<AbsRecycleViewFragment<E>> fragmentWeakReference;

    public AbsRecycleViewRefreshTask(final AbsRecycleViewFragment<E> fragment, final Position position) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.position = position;
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
    protected void onPostExecute(final PageableArrayList<E> data) {
        if (data == null) {
            taskState = TaskState.ERROR;
            NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, null);
        } else {
            final AbsRecycleViewFragment<E> fragment = fragmentWeakReference.get();
            if (fragment != null) {
                if (position == Position.END) { // adding to end of list
                    fragment.adapter.addAll(data);
                } else if (position == Position.START) { // adding to start
                    fragment.adapter.addAll(0, data);
                }
                fragment.adapter.getData().setCursors(data); // setting new cursors
                fragment.setRefreshing(false);
            }
            taskState = TaskState.COMPLETED;
        }
    }
}
