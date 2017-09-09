package ru.eadm.nobird.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to listen notifications
 */

public class StreamService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
