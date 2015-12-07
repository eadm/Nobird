package ru.eadm.nobird.data.types;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountElement implements Parcelable{
    public final long userID;
    public final String name, username, image, token, token_secret;
    public AccountElement(
            final long userID,
            final String name,
            final String username,
            final String image,
            final String token,
            final String token_secret) {
        this.userID = userID;
        this.name = name;
        this.username = username;
        this.image = image;
        this.token = token;
        this.token_secret = token_secret;
    }

    protected AccountElement(Parcel in) {
        userID = in.readLong();
        name = in.readString();
        username = in.readString();
        image = in.readString();
        token = in.readString();
        token_secret = in.readString();
    }

    public static final Creator<AccountElement> CREATOR = new Creator<AccountElement>() {
        @Override
        public AccountElement createFromParcel(final Parcel in) {
            return new AccountElement(in);
        }

        @Override
        public AccountElement[] newArray(final int size) {
            return new AccountElement[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(userID);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(image);
        dest.writeString(token);
        dest.writeString(token_secret);
    }
}
