package ru.eadm.nobird.data.twitter.utils;

import android.text.Spannable;
import android.text.SpannableString;

import ru.eadm.nobird.design.span.AbsSpan;
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
    private static TwitterStatusText getSpannableString(final String string, final AbsSpan click,
                                                      final int start, final int end, final int offset) {
        final SpannableString span = new SpannableString(string);
        span.setSpan(click, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return new TwitterStatusText(span, click.getData() + "|" + (offset + start) + "|" + (offset + end));
    }

    private static TwitterStatusText getSpan(final String string, final Status status, final int offset) {
        for (final URLEntity entity : status.getURLEntities()) {
            final int start = string.indexOf(entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string.substring(0, start) + entity.getDisplayURL() + string.substring(start + entity.getText().length()),
                        new LinkSpan(entity.getExpandedURL()),
                        start,
                        start + entity.getDisplayURL().length(),
                        offset
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
                        start + entity.getDisplayURL().length(),
                        offset
                );
            }
        }

        for (final UserMentionEntity entity : status.getUserMentionEntities()) {
            final int start = string.indexOf('@' + entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string,
                        new UserSpan(entity.getId()),
                        start, // to include @
                        start + entity.getText().length() + 1,
                        offset
                );
            }
        }

        for (final HashtagEntity entity : status.getHashtagEntities()) {
            final int start = string.indexOf('#' + entity.getText());
            if (start != -1) {
                return getSpannableString(
                        string,
                        new HashTagSpan(entity.getText()),
                        start, // to include #
                        start + entity.getText().length() + 1,
                        offset
                );
            }
        }

        return new TwitterStatusText(string);
    }

    public static TwitterStatusText getTweetText(final Status status) {
        final String[] strings = status.getText().split(" ");

        final TwitterStatusText statusText = new TwitterStatusText();


        for (final String string : strings) {
            if (string.length() == 0) continue;
            statusText.append(getSpan(string, status, statusText.length()));
        }

        return statusText;
    }
}
