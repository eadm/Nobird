package ru.eadm.nobird.data.types;

public class UserElement {
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
}
