package ru.eadm.nobird.data.twitter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.eadm.nobird.data.twitter.utils.TwitterStatusParser;
import ru.eadm.nobird.data.types.TweetElement;
import twitter4j.Status;
import twitter4j.URLEntity;

public class TwitterUtils {
    public static final String TAG = "TwitterUtils";

    private static final Pattern[] patterns = {
            Pattern.compile("/(?:(?:http|https)://)?(?:www.)?(?:instagram.com|instagr.am)/.*"),
            Pattern.compile("/(?:(?:http|https)://)?(?:www.)?(?:youtube.com|youtu.be)(?:/watch\\?v=|/)([^&]+)")
    };

    public static String getAttachment(final Status status) {
        if (status.getMediaEntities().length > 0) {
            return status.getMediaEntities()[0].getMediaURLHttps();
        }

        for (final URLEntity urlEntity : status.getURLEntities()) {
            Matcher matcher = patterns[0].matcher(urlEntity.getExpandedURL());
            if (matcher.find()) {
                return urlEntity.getExpandedURL() + "media/?size=l";
            }

            matcher = patterns[1].matcher(urlEntity.getExpandedURL());
            if (matcher.find()) {
                return "http://img.youtube.com/vi/" + matcher.group(1) + "/hqdefault.jpg";
            }

            Log.d(TAG + "-" + status.getId(), urlEntity.getExpandedURL());
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
