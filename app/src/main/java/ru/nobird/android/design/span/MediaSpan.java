package ru.nobird.android.design.span;

import android.text.TextPaint;
import android.view.View;

import ru.nobird.android.fragment.ImagePreview;

public class MediaSpan extends AbsSpan {
    public final static char SPAN_TAG = 'm';

    private final String url;
    public MediaSpan(final String url) {
        this.url = url;
    }

    @Override
    public void onClick(final View widget) {
        ImagePreview.openImagePreview(url);
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(0xFF659dd6);
    }

    @Override
    public String getData() {
        return SPAN_TAG + "|" + url;
    }
}
