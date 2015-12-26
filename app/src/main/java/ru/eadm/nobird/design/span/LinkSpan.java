package ru.eadm.nobird.design.span;

import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;

import ru.eadm.nobird.fragment.FragmentMgr;


public class LinkSpan extends AbsSpan {
    public final static char SPAN_TAG = 'l';

    private final String url;
    public LinkSpan(final String url) {
        super();
        this.url = url;
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(0xFF659dd6);
    }

    @Override
    public void onClick(final View widget) {
        FragmentMgr.getInstance().getContext()
                .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public String getData() {
        return SPAN_TAG + "|" + url;
    }
}
