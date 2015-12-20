package ru.eadm.nobird.design.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

/**
 * Created by ruslandavletshin on 15/12/15.
 */
public class HashTagSpan extends ClickableSpan {
    public final static String TAG = "LinkSpan";

    private final String hashTag;
    public HashTagSpan(final String hashTag) {
        super();
        this.hashTag = hashTag;
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(0xFF777777);
    }

    @Override
    public void onClick(View widget) {
        Log.d(TAG, hashTag);
    }
}
