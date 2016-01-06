package ru.eadm.nobird.notification;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.lang.ref.WeakReference;

public final class NotificationMgr {
    private WeakReference<Context> context;
    private WeakReference<View> root;
    private static NotificationMgr instance;

    private NotificationMgr(final Context context) {
        this.context = new WeakReference<>(context);
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new NotificationMgr(context);
        }
    }

    public synchronized static NotificationMgr getInstance() {
        return instance;
    }

    public void attach(final Context context, final View rootView) {
        this.context = new WeakReference<>(context);
        this.root = new WeakReference<>(rootView);
    }


    public void showSnackbar(final String message, final View container) {
        if (context.get() == null || root.get() == null) return;
        Snackbar.make((container == null ? root.get() : container), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showSnackbar(final int messageID, final View container) {
        if (context.get() == null || root.get() == null) return;
        Snackbar.make((container == null ? root.get() : container), context.get().getText(messageID), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
