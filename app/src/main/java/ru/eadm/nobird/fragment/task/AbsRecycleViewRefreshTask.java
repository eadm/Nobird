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
                    final int start = fragment.adapter.getItemCount();
                    fragment.adapter.addAll(data);
                    fragment.adapter.notifyItemRangeInserted(start, data.size());
                } else if (position == Position.START) { // adding to start
                    fragment.adapter.addAll(0, data);
                    fragment.adapter.notifyItemRangeInserted(0, data.size());
                }
                fragment.adapter.getData().setCursors(data); // setting new cursors
                fragment.setRefreshing(false);
            }
            taskState = TaskState.COMPLETED;
        }
    }
}
