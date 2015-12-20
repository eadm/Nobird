package ru.eadm.nobird.fragment.adapter.listener;

import android.util.Log;
import android.view.View;

import ru.eadm.nobird.data.types.TweetElement;

/**
 * Created by ruslandavletshin on 20/12/15.
 */
public class TweetItemClickListener implements View.OnClickListener {
    public final static String TAG = "TweetItemClickListener";

    private final TweetElement tweet;
    public TweetItemClickListener(final TweetElement tweet) {
        this.tweet = tweet;
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, tweet.tweetID + " clicked");
    }
}
