package ru.eadm.nobird.design.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import ru.eadm.nobird.fragment.SearchResultFragment;

public class HashTagSpan extends AbsSpan {
    public final static String TAG = "HashTagSpan";
    public final static char SPAN_TAG = 'h';


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
    public void onClick(final View widget) {
        SearchResultFragment.show('#' + hashTag);
    }

    @Override
    public String getData() {
        return SPAN_TAG + "|" + hashTag;
    }
}
