package ru.eadm.nobird.data.types;

import java.util.Date;

import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;
import ru.eadm.nobird.fragment.StatusFragment;
import twitter4j.Status;

public class TweetElement implements Element {
    public final long tweetID;
    public final String image;
    public final Date date;

    public final UserElement user;

    public final TwitterStatusText text;

    public Status status;

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

    @Override
    public long getID() {
        return tweetID;
    }

    public void onClick() {
        StatusFragment.showStatus(this);
    }
}
