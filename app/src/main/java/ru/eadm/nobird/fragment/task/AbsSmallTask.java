package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;

import ru.eadm.nobird.notification.NotificationMgr;

public abstract class AbsSmallTask extends AsyncTask<Long, Void, Integer> {
    @Override
    protected void onPostExecute(final Integer resID) {
        NotificationMgr.getInstance().showSnackbar(resID, null);
    }
}
