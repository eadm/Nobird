package ru.eadm.nobird.design.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import ru.eadm.nobird.fragment.ImagePreview;

public class MediaSpan extends ClickableSpan {
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

}
