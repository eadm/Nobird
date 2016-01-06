package ru.eadm.nobird.design;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import ru.eadm.nobird.data.FontMgr;

public class RobotoLightTextView extends TextView {
    public RobotoLightTextView(Context context) {
        super(context);
        init();
    }

    public RobotoLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(FontMgr.getInstance().RobotoLight);
    }
}
