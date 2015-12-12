package ru.eadm.nobird.data;

import android.content.Context;
import android.graphics.Typeface;

public final class FontMgr {
    private final Context context;
    private static FontMgr instance;

    public Typeface RobotoLight, RobotoMedium, RobotoSlabLight, RobotoSlabRegular;

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
        RobotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        RobotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        RobotoSlabLight = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Light.ttf");
        RobotoSlabRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
    }

    public synchronized static FontMgr getInstance() {
        return instance;
    }
}
