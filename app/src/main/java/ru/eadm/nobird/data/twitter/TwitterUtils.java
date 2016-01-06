package ru.eadm.nobird.data.twitter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    public static ArrayList<TweetElement> statusToTweetElement(final List<Status> statuses) {
        final ArrayList<TweetElement> tweets = new ArrayList<>(statuses.size());
        for (final Status status : statuses) tweets.add(statusToTweetElement(status));
        return tweets;
    }

    public static TweetElement statusToTweetElement(final Status status) {
        return new TweetElement(
                status.getId(),
                status.getUser().getId(),

                status.getUser().getName(),
                status.getUser().getScreenName().toLowerCase(),
                status.getUser().getOriginalProfileImageURL(),

                TwitterStatusParser.getTweetText(status),
                status.getCreatedAt(),
                getAttachment(status));
    }
}
