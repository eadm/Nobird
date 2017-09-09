package ru.nobird.android.data.twitter.utils;

import android.util.Log;

import ru.nobird.android.R;
import twitter4j.TwitterException;

public final class TwitterExceptionResolver {
    public static int resolve(final TwitterException e) {
        try {
            final int code = Integer.parseInt(e.getExceptionCode());
            Log.d("TwitterException", "Error: " + code);
            switch (code) {
                case 88: return R.string.error_twitter_88;
                default: return R.string.error_twitter_api;
            }
        } catch (final NumberFormatException nfe) {
            e.printStackTrace();
        }
        return R.string.error_twitter_api;
    }
}
