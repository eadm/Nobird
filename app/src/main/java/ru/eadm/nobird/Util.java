package ru.eadm.nobird;

import android.support.v7.app.AppCompatActivity;

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


    public static String dateDifference(final Date d2){
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
}
