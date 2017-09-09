package ru.eadm.nobird.data.types;

import android.database.Cursor;

import ru.eadm.nobird.fragment.UserFragment;
import twitter4j.User;

public class UserElement implements Element {
    public final long userID;
    public final String name, username, image;
    public final User user;

    public UserElement(final long userID,
                       final String name,
                       final String username,
                       final String image) {
        this.userID = userID;
        this.name = name;
        this.username = username;
        this.image = image;

        this.user = null;
    }

    public UserElement(final User user) {
        this.userID = user.getId();
        this.name = user.getName();
        this.username = user.getScreenName();
        this.image = user.getOriginalProfileImageURLHttps();

        this.user = user;
    }

    public UserElement(final Cursor cursor) {
        this.userID = cursor.getLong(cursor.getColumnIndex("userID"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.username = cursor.getString(cursor.getColumnIndex("username"));
        this.image = cursor.getString(cursor.getColumnIndex("profile_image_url"));

        this.user = null;
    }

    @Override
    public long getID() {
        return userID;
    }

    public void onClick() {
        UserFragment.showUser(this);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(userID).hashCode();
    }
}
