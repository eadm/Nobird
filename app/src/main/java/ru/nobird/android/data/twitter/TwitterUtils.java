package ru.nobird.android.data.twitter;

//import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.nobird.android.data.twitter.utils.TwitterStatusParser;
import ru.nobird.android.data.types.TweetElement;
import twitter4j.EntitySupport;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

public class TwitterUtils {
    public static final String TAG = "TwitterUtils";

    private static final Pattern[] patterns = {
            Pattern.compile("/(?:(?:http|https)://)?(?:www.)?(?:instagram.com|instagr.am)/.*"),
            Pattern.compile("/(?:(?:http|https)://)?(?:www.)?(?:youtube.com/watch\\?v=|youtu.be/)([^&]+)")
    };

    public static List<String> getAttachments(final EntitySupport status) {
        final ArrayList<String> attachments = new ArrayList<>();

        for (final MediaEntity entity : status.getExtendedMediaEntities()) {
            attachments.add(entity.getMediaURLHttps());
//            Log.d(TAG + "-" + status.getId(), entity.getMediaURLHttps());
        }

        for (final URLEntity urlEntity : status.getURLEntities()) {
//            Log.d(TAG + "-" + status.getId(), urlEntity.getExpandedURL());
            Matcher matcher = patterns[0].matcher(urlEntity.getExpandedURL());
            if (matcher.find()) {
                attachments.add(urlEntity.getExpandedURL() + "media/?size=l");
            }

            matcher = patterns[1].matcher(urlEntity.getExpandedURL());
            if (matcher.find()) {
                attachments.add("http://img.youtube.com/vi/" + matcher.group(1) + "/hqdefault.jpg");
            }
        }
        return attachments;
    }

    public static ArrayList<TweetElement> statusToTweetElement(final List<Status> statuses) {
        final ArrayList<TweetElement> tweets = new ArrayList<>(statuses.size());
        for (final Status status : statuses) tweets.add(statusToTweetElement(status));
        return tweets;
    }

    public static TweetElement statusToTweetElement(final Status status) {
        return statusToTweetElement(status, false);
    }

    /**
     * Converts Status to TweetElement
     * @param status - status object to build from
     * @param keepStatus - if true keeps original status object
     * @return converted tweet element
     */
    public static TweetElement statusToTweetElement(final Status status, boolean keepStatus) {
        final TweetElement tweetElement = new TweetElement(
                status.getId(),
                status.getUser().getId(),

                status.getUser().getName(),
                status.getUser().getScreenName().toLowerCase(),
                status.getUser().getOriginalProfileImageURL(),

                TwitterStatusParser.getTweetText(status.getText(), status),
                status.getCreatedAt(),
                getAttachments(status));
        if (keepStatus) tweetElement.status = status;
        return tweetElement;
    }
}
