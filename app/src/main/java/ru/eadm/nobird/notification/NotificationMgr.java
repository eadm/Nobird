package ru.eadm.nobird.notification;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

public final class NotificationMgr {
    private final Context context;
    private static NotificationMgr instance;

    private NotificationMgr(final Context context) {
        this.context = context;
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new NotificationMgr(context);
        }
    }

    public synchronized static NotificationMgr getInstance() {
        return instance;
    }

    public void showSnackbar(final String message, final View container) {
        Snackbar.make(container, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
