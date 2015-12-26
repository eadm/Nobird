package ru.eadm.nobird.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ruslandavletshin on 06/12/15.
 */
public final class DBHelper extends SQLiteOpenHelper {
    public final static String TABLE_ACCOUNTS = "accounts";

    public final static String TABLE_TWEETS = "tweets";
    public final static String TABLE_TWEETS_PATTERN = "insert into " + TABLE_TWEETS +
            " values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public final static String TABLE_TWEET_CLEAR_PATTERN = "DELETE FROM " + TABLE_TWEETS + " " +
            "WHERE id IN " +
            "(SELECT id FROM " + TABLE_TWEETS + " WHERE ownerID = ? AND type = ? " +
            "ORDER BY tweetID ASC LIMIT ?)";


    public DBHelper(final Context context) {
        super(context, "nobirdDB", null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ACCOUNTS + " ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "username text,"
                + "image_url text,"
                + "token text,"
                + "token_secret text" + ");");

        db.execSQL("create table " + TABLE_TWEETS + " ("
                + "id integer primary key autoincrement,"
                + "tweetID integer,"
                + "userID integer,"
                + "name text,"
                + "username text,"
                + "profile_image_url text,"
                + "tweet_text text,"
                + "tweet_text_parse_key text,"
                + "attachment_url text,"
                + "pubDate integer," +
                "" +
                "ownerID integer," +
                "type integer);"); // 0 for feed, 1 for mentions
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
