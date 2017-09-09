package ru.eadm.nobird.data.types;

import android.database.Cursor;

import java.util.Date;
import java.util.List;

import ru.eadm.nobird.Util;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;
import ru.eadm.nobird.fragment.StatusFragment;
import twitter4j.Status;

public class TweetElement implements Element {
    public final long tweetID;
    public final List<String> images;
    public final Date date;

    public final UserElement user;

    public final TwitterStatusText text;

    public Status status;

    /**
     * Displays if current tweet element participate in some data update task
     *  You should avoid all network operation while lock == true
     */
    public boolean lock = false;

    public TweetElement(final long tweetID,
                        final long userID,

                        final String name,
                        final String username,
                        final String user_image,

                        final TwitterStatusText text,
                        final Date date,
                        final List<String> images) {
        this.tweetID = tweetID;

        this.user = new UserElement(
                userID,
                name,
                username,
                user_image
        );

        this.images = images;
        this.text = text;
        this.date = date;
    }

    public TweetElement(final Cursor cursor) {
        this.tweetID = cursor.getLong(cursor.getColumnIndex("tweetID"));
        this.user = new UserElement(cursor);

        this.text = TwitterStatusText.parse(
                cursor.getString(cursor.getColumnIndex("tweet_text")),
                cursor.getString(cursor.getColumnIndex("tweet_text_parse_key"))
        );
        this.date = new Date(cursor.getLong(cursor.getColumnIndex("pubDate")));
        this.images = Util.split(cursor.getString(cursor.getColumnIndex("attachment_url")), "\\|");
    }

    @Override
    public long getID() {
        return tweetID;
    }

    public void onClick() {
        StatusFragment.showStatus(this);
    }
}
