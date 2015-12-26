package ru.eadm.nobird.data.types;

import java.util.Date;

import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;

public class TweetElement {
    public final long tweetID;
    public final String image;
    public final Date date;

    public final UserElement user;

    public final TwitterStatusText text;

    public TweetElement(final long tweetID,
                        final long userID,

                        final String name,
                        final String username,
                        final String user_image,

                        final TwitterStatusText text,
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
