package ru.eadm.nobird.data.types;

import ru.eadm.nobird.fragment.UserFragment;
import twitter4j.User;

public class UserElement implements Element {
    public final long userID;
    public final String name, username, image;

    public UserElement(final long userID,
                       final String name,
                       final String username,
                       final String image) {
        this.userID = userID;
        this.name = name;
        this.username = username;
        this.image = image;
    }

    public UserElement(final User user) {
        this.userID = user.getId();
        this.name = user.getName();
        this.username = user.getScreenName();
        this.image = user.getOriginalProfileImageURLHttps();
    }

    @Override
    public long getID() {
        return userID;
    }

    public void onClick() {
        UserFragment.showUser(this);
    }
}
