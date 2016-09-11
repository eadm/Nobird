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
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class TwitterStatusParser {

    /**
     * Sets given span with given part of string
     * @param string - targeted part of string
     * @param click - span
     * @param start - start pos
     * @param end - end pos
     * @param offset - offset in base string
     * @return twitter status parser text
     */
    private static TwitterStatusText getSpannableString(final String string, final AbsSpan click,
                                                      final int start, final int end, final int offset) {
        final SpannableString span = new SpannableString(string);
        span.setSpan(click, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return new TwitterStatusText(span, click.getData() + "|" + (offset + start) + "|" + (offset + end));
    }

    /**
     * Matching given part of string with entities
     * @param string - part of string
     * @param urlEntities - url entities
     * @param mediaEntities - media entities
     * @param userMentionEntities - user entities
     * @param hashtagEntities - tags entities
     * @param offset - offset in base string
     * @return - twitter status parser text
     */
    private static TwitterStatusText getSpan(final String string,
                                             final URLEntity[] urlEntities, final MediaEntity[] mediaEntities,
                                             final UserMentionEntity[] userMentionEntities, final HashtagEntity[] hashtagEntities,
                                             final int offset) {
        if (urlEntities != null)
        for (final URLEntity entity : urlEntities) {
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

        if (mediaEntities != null)
        for (final MediaEntity entity : mediaEntities) {
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

        if (userMentionEntities != null)
        for (final UserMentionEntity entity : userMentionEntities) {
            final int start = string.indexOf('@' + entity.getText());

            if (start != -1) {
                return getSpannableString(
                        string,
                        new UserSpan(entity),
                        start, // to include @
                        start + entity.getText().length() + 1,
                        offset
                );
            }
        }

        if (hashtagEntities != null)
        for (final HashtagEntity entity : hashtagEntities) {
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

    /**
     * Parsing status text
     * @param status - status
     * @return twitter status parser text
     */
    public static TwitterStatusText getTweetText(final Status status) {
        return getParsedText(status.getText(),
                status.getURLEntities(), status.getMediaEntities(),
                status.getUserMentionEntities(), status.getHashtagEntities());
    }

    /**
     * Parsing user description
     * @param user - user
     * @return twitter status parser text
     */
    public static TwitterStatusText getUserDescription(final User user) {
        return getParsedText(user.getDescription(), user.getDescriptionURLEntities(), null, null, null);
    }

    public static TwitterStatusText getParsedText(final String text,
                                                  final URLEntity[] urlEntities, final MediaEntity[] mediaEntities,
                                                  final UserMentionEntity[] userMentionEntities, final HashtagEntity[] hashtagEntities) {
        final String[] strings = text.split(" ");
        final TwitterStatusText statusText = new TwitterStatusText();

        for (final String string : strings) {
            if (string.length() == 0) continue;
            statusText.append(getSpan(string, urlEntities, mediaEntities, userMentionEntities, hashtagEntities, statusText.length()));
        }

        return statusText;
    }
}
