package ru.eadm.nobird.data.twitter;

import android.content.Context;
import android.util.Log;

import ru.eadm.nobird.data.twitter.utils.TwitterStatusParser;
import ru.eadm.nobird.data.types.TweetElement;
import twitter4j.Status;
import twitter4j.URLEntity;

public class TwitterUtils {
    public static final String TAG = "TwitterUtils";

    public static String getAttachment(final Status status) {
        if (status.getMediaEntities().length > 0) {
            return status.getMediaEntities()[0].getMediaURLHttps();
        }

        for (final URLEntity urlEntity : status.getURLEntities()) {
            Log.d(TAG + "-" + status.getId(), urlEntity.getText());
        }
        return "";
    }

    public static TweetElement statusToTweetElement(final Status status, final Context context) {
        return new TweetElement(
                status.getId(),
                status.getUser().getId(),

                status.getUser().getName(),
                status.getUser().getScreenName(),
                status.getUser().getOriginalProfileImageURL(),

                TwitterStatusParser.getTweetText(status, context),
                status.getCreatedAt(),
                getAttachment(status));
    }
}
