package ru.eadm.nobird.data;

import android.content.Context;
import android.graphics.Typeface;

public final class FontMgr {
    private final Context context;
    private static FontMgr instance;

    public Typeface RobotoLigth;

    private FontMgr(final Context context) {
        this.context = context;
        initFonts();
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new FontMgr(context);
        }
    }

    private void initFonts() {
        RobotoLigth = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }

    public synchronized static FontMgr getInstance() {
        return instance;
    }
}
