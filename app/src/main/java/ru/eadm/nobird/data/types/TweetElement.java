package ru.eadm.nobird.data.types;

import android.text.SpannableStringBuilder;

import java.util.Date;

public class TweetElement {
    public final long tweetID;
    public final String image;
    public final Date date;

    public final UserElement user;

    public final SpannableStringBuilder text;

    public TweetElement(final long tweetID,
                        final long userID,

                        final String name,
                        final String username,
                        final String user_image,

                        final SpannableStringBuilder text,
                        final Date date,
                        final String image) {
        this.tweetID = tweetID;

        this.user = new UserElement(
                userID,
                name,
                username,
                user_image
        );

        this.image = image;
        this.text = text;
        this.date = date;
    }
}
