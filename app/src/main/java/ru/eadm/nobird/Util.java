package ru.eadm.nobird;

import android.content.Context;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import java.util.Date;

import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.fragment.FragmentMgr;
import ru.eadm.nobird.notification.NotificationMgr;

public final class Util {

    public static void initMgr(final AppCompatActivity context) {
        DBMgr.init(context);
        FontMgr.init(context);
        ImageMgr.init(context);
        TwitterMgr.init(context);
        NotificationMgr.init(context);
        PreferenceMgr.init(context);
        FragmentMgr.init(context);
    }

    private static char[] numberShortcuts = { 0, 'k', 'm', 'b'};
    public static String numberShortcut(long number) {
        int i = 0;
        for (; number / 1000 > 0; ++i) {
            number /= 1000;
        }
        return Long.toString(number) + numberShortcuts[i];
    }

    public static String dateDifference(final Date d2){
        if (d2 == null) return "";
        final long now = System.currentTimeMillis();
        final Date d1 = new Date (now);
        final long difference = d1.getTime() - d2.getTime();

        long differenceBack = difference / 1000;

        int d = (int)(differenceBack / (24*60*60));
        if(d>0)return d+"d";

        int h = (int)(differenceBack / (60*60));
        if(h>0)return h+"h";

        int m = (int)(differenceBack / (60));
        if(m>0)return m+"m";

        if(differenceBack<10)
            return "now";
        else
            return differenceBack+"s";
    }

    public static void closeKeyboard(final Context c, final IBinder windowToken) {
        final InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}
