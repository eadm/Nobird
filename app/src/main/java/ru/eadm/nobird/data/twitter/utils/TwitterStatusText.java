package ru.eadm.nobird.data.twitter.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import ru.eadm.nobird.design.span.AbsSpan;
import ru.eadm.nobird.design.span.HashTagSpan;
import ru.eadm.nobird.design.span.LinkSpan;
import ru.eadm.nobird.design.span.MediaSpan;
import ru.eadm.nobird.design.span.UserSpan;


public class TwitterStatusText {
    private String parseKey;
    private final SpannableStringBuilder text;

    public TwitterStatusText(){
        text = new SpannableStringBuilder();
        parseKey = "";
    }

    public static TwitterStatusText parse(final String string, final String key) {
        if (key.length() == 0) return new TwitterStatusText(string);
        final SpannableString span = new SpannableString(string);
        final String [] keys = key.split("\\|");
        // some parsing
        for (int i = 0; i < keys.length; i += 4) {
            final int start = Integer.parseInt(keys[i + 2]);
            final int end = Integer.parseInt(keys[i + 3]);

            AbsSpan absSpan = null;
            switch (keys[i].charAt(0)) {
                case UserSpan.SPAN_TAG:
                    absSpan = new UserSpan(Long.parseLong(keys[i + 1]));
                break;

                case HashTagSpan.SPAN_TAG:
                    absSpan = new HashTagSpan(keys[i + 1]);
                break;

                case LinkSpan.SPAN_TAG:
                    absSpan = new LinkSpan(keys[i + 1]);
                break;

                case MediaSpan.SPAN_TAG:
                    absSpan = new MediaSpan(keys[i + 1]);
                break;
            }
            span.setSpan(
                    absSpan,
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        return new TwitterStatusText(span, key);
    }

    public TwitterStatusText(final SpannableString string, final String key) {
        text = new SpannableStringBuilder(string);
        parseKey = key;
    }

    public TwitterStatusText(final String string) {
        text = new SpannableStringBuilder(string);
        parseKey = "";
    }

    public void append(final TwitterStatusText statusText) {
        text.append(statusText.text);
        text.append(" ");
        if (statusText.parseKey.length() > 0) {
            parseKey += parseKey.isEmpty() ? "" : "|";
            parseKey += statusText.parseKey;
        }
    }

    public SpannableStringBuilder getText() { return text; }
    public String getParseKey() { return parseKey; }
    public int length() { return text.length(); }
}
