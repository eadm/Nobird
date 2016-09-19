package ru.eadm.nobird.notification;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.lang.ref.WeakReference;

public final class NotificationMgr {
    private WeakReference<Context> context;
    private View root;
    private static NotificationMgr instance;

    private final static String TAG = "NotificationMgr";

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
        this.root = rootView;
    }


    public void showSnackbar(final String message, final View container) {
        if (context.get() == null || root == null) return;
        Snackbar.make((container == null ? root : container), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showSnackbar(final int stringID, final View container) {
        if (context.get() == null || root == null) return;
        Snackbar.make((container == null ? root : container), context.get().getText(stringID), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showInfiniteSnackbar(final int stringID, final View container) {
        if (context.get() == null || root == null) return;
        Snackbar.make((container == null ? root : container), context.get().getText(stringID), Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show();
    }
}
