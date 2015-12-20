package ru.eadm.nobird.data.twitter.utils;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;

import ru.eadm.nobird.design.span.HashTagSpan;
import ru.eadm.nobird.design.span.LinkSpan;
import ru.eadm.nobird.design.span.MediaSpan;
import ru.eadm.nobird.design.span.UserSpan;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

public class TwitterStatusParser {
    private static SpannableString getSpannableString(final String string, final ClickableSpan click,
                                                      final int start, final int end) {
        final SpannableString span = new SpannableString(string);
        span.setSpan(
                click,
                start, //start
                end, //end
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    private static SpannableString getSpan(final String string, final Status status, final Context context) {
        for (final URLEntity entity : status.getURLEntities()) {
            final int start = string.indexOf(entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string.substring(0, start) + entity.getDisplayURL() + string.substring(start + entity.getText().length()),
                        new LinkSpan(entity.getExpandedURL(), context),
                        start,
                        start + entity.getDisplayURL().length()
                );
            }
        }

        for (final MediaEntity entity : status.getMediaEntities()) {
            final int start = string.indexOf(entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string.substring(0, start) + entity.getDisplayURL() + string.substring(start + entity.getText().length()),
                        new MediaSpan(entity.getMediaURLHttps()),
                        start,
                        start + entity.getDisplayURL().length()
                );
            }
        }

        for (final UserMentionEntity entity : status.getUserMentionEntities()) {
            final int start = string.indexOf(entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string,
                        new UserSpan(entity.getId()),
                        start - 1, // to include @
                        start + entity.getText().length()
                );
            }
        }

        for (final HashtagEntity entity : status.getHashtagEntities()) {
            final int start = string.indexOf(entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string,
                        new HashTagSpan(entity.getText()),
                        start - 1, // to include #
                        start + entity.getText().length()
                );
            }
        }

        return new SpannableString(string);
    }

    public static SpannableStringBuilder getTweetText(final Status status, final Context context) {
        final SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        final String[] strings = status.getText().split(" ");

        for (final String string : strings) {
            if (string.length() == 0) continue;
            stringBuilder.append(getSpan(string, status, context));
            stringBuilder.append(" ");
        }

        return stringBuilder;
    }
}
