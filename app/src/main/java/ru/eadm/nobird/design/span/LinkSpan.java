package ru.eadm.nobird.design.span;

import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.eadm.nobird.fragment.FragmentMgr;
import ru.eadm.nobird.fragment.StatusFragment;


public class LinkSpan extends AbsSpan {

    public final static char SPAN_TAG = 'l';
    private final static Pattern statusPattern = Pattern.compile("/(?:(?:http|https)://)?(?:www.)?.*twitter.com/.*/status/([^&]+)");

    private final String url;
    public LinkSpan(final String url) {
        super();
        this.url = url;
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
        ds.setColor(0xFF659dd6);
    }

    @Override
    public void onClick(final View widget) {
        final Matcher matcher = statusPattern.matcher(url);
        if (matcher.find() && matcher.groupCount() > 0) try {
            StatusFragment.showStatus(Long.parseLong(matcher.group(1)));
        } catch (final NumberFormatException nfe) {
            FragmentMgr.getInstance().getContext()
                    .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            FragmentMgr.getInstance().getContext()
                    .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    @Override
    public String getData() {
        return SPAN_TAG + "|" + url;
    }
}
