package ru.eadm.nobird.design.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

/**
 * Created by ruslandavletshin on 15/12/15.
 */
public class UserSpan extends ClickableSpan {
    public final static String TAG = "LinkSpan";

    private final long userID;
    public UserSpan(final long userID) {
        super();
        this.userID = userID;
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(0xFF777777);
    }

    @Override
    public void onClick(View widget) {
        Log.d(TAG, userID + "");
    }
}
