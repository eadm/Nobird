package ru.eadm.nobird.data.twitter.utils;

import ru.eadm.nobird.R;
import twitter4j.TwitterException;

public final class TwitterExceptionResolver {
    public static int resolve(final TwitterException e) {
        final int code = Integer.parseInt(e.getExceptionCode());
        switch (code) {
            case 88: return R.string.error_twitter_88;
            default: return R.string.error_twitter_api;
        }
    }
}
