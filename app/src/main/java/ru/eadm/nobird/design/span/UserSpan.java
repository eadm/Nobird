package ru.eadm.nobird.design.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class UserSpan extends AbsSpan {
    public final static String TAG = "UserSpan";
    public final static char SPAN_TAG = 'u';


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

    @Override
    public String getData() {
        return SPAN_TAG + "|" + userID;
    }
}
