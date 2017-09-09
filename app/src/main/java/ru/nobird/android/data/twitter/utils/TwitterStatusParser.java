package ru.nobird.android.data.twitter.utils;

import android.text.Spannable;
import android.text.SpannableString;

import ru.nobird.android.design.span.AbsSpan;
import ru.nobird.android.design.span.HashTagSpan;
import ru.nobird.android.design.span.LinkSpan;
import ru.nobird.android.design.span.MediaSpan;
import ru.nobird.android.design.span.UserSpan;

import twitter4j.EntitySupport;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
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
            final int start = string.toLowerCase().indexOf(entity.getText().toLowerCase());
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
            final int start = string.toLowerCase().indexOf(entity.getText().toLowerCase());
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
            final int start = string.toLowerCase().indexOf('@' + entity.getText().toLowerCase());

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
            final int start = string.toLowerCase().indexOf('#' + entity.getText().toLowerCase());
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
     * @param text - text to parse
     * @param status - EntitySupport element
     * @return twitter status parser text
     */
    public static TwitterStatusText getTweetText(final String text, final EntitySupport status) {
        return getParsedText(text,
                status.getURLEntities(), status.getMediaEntities(),
                status.getUserMentionEntities(), status.getHashtagEntities());
    }

    /**
     * Parsing user description
     * @param user - user
     * @return twitter status parser text
     */
    public static TwitterStatusText getUserDescription(final User user) {
        if (user == null)
            return new TwitterStatusText();
        else
            return getParsedText(user.getDescription(), user.getDescriptionURLEntities(), null, null, null);
    }

    public static TwitterStatusText getParsedText(final String text,
                                                  final URLEntity[] urlEntities, final MediaEntity[] mediaEntities,
                                                  final UserMentionEntity[] userMentionEntities, final HashtagEntity[] hashtagEntities) {
        final String[] strings = text.split(" ");
        final TwitterStatusText statusText = new TwitterStatusText();

        for (final String string : strings) {
            if (string.length() == 0) continue;
            statusText.append(
                    getSpan(string, urlEntities, mediaEntities, userMentionEntities, hashtagEntities, statusText.length())
            );
        }

        return statusText;
    }
}
