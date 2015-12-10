package ru.eadm.nobird;

import android.content.Context;

import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.notification.NotificationMgr;

public final class Util {

    public static void initMgr(final Context context) {
        DBMgr.init(context);
        FontMgr.init(context);
        ImageMgr.init(context);
        TwitterMgr.init(context);
        NotificationMgr.init(context);
        PreferenceMgr.init(context);
    }

}
