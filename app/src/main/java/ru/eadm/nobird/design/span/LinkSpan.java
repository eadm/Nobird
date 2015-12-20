package ru.eadm.nobird.design.span;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public class LinkSpan extends ClickableSpan {

    private final Context context;
    private final String url;
    public LinkSpan(final String url, final Context context) {
        super();
        this.url = url;
        this.context = context;
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setColor(0xFF659dd6);
    }

    @Override
    public void onClick(final View widget) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
