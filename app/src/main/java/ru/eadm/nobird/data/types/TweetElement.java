package ru.eadm.nobird.data.types;

import java.util.Date;

public class TweetElement {
    public final String name, username, text, user_image;
    public final Date date;

    public TweetElement(final String name,
                        final String username,
                        final String user_image,
                        final String text,
                        final Date date) {
        this.name = name;
        this.username = "@" + username.toLowerCase();
        this.text = text;
        this.user_image = user_image;
        this.date = date;
    }
}
