package ru.eadm.nobird.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Default class that contains table names constants and creates/updates DB
 */
public final class DBHelper extends SQLiteOpenHelper {
    public final static String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_DRAFTS = "drafts";

    public final static String TABLE_TWEETS = "tweets";
    public final static String TABLE_TWEETS_PATTERN = "insert into " + TABLE_TWEETS +
            " values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public final static String TABLE_TWEET_CLEAR_PATTERN = "DELETE FROM " + TABLE_TWEETS + " " +
            "WHERE id IN " +
            "(SELECT id FROM " + TABLE_TWEETS + " WHERE ownerID = ? AND type = ? " +
            "ORDER BY tweetID ASC LIMIT ?)";

    public final static String TABLE_SAVED_SEARCHES = "saved_searches";
    public final static String TABLE_SAVED_SEARCHES_PATTERN = "insert into " + TABLE_SAVED_SEARCHES +
            " values (?, ?, ?)";

    public final static String TABLE_MESSAGES = "messages";
    public final static String TABLE_MESSAGES_PATTERN = "insert into " + TABLE_MESSAGES +
            " values (?, ?, ?, ?, ?, ?, ?, 0)";

    public final static String TABLE_CONVERSATIONS = "message_senders";
    public final static String TABLE_CONVERSATIONS_PATTERN = "INSERT OR REPLACE INTO " + TABLE_CONVERSATIONS +
            " values (?1, " +
            "?2, " +
            "?3, " +
            "?4, " +
            "?5, " +
            "COALESCE((SELECT unread_count FROM " + TABLE_CONVERSATIONS + " WHERE userID = ?1 AND recipientID = ?5), 0), " +
            "COALESCE((SELECT lastDate FROM " + TABLE_CONVERSATIONS + " WHERE userID = ?1 AND recipientID = ?5), ?6))";

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
                "ownerID integer," +
                "type integer);"); // 0 for feed, 1 for mentions

        db.execSQL("create table " + TABLE_MESSAGES + " ("
                + "id integer primary key autoincrement,"
                + "senderID integer,"
                + "message_text text,"
                + "message_text_parse_key text,"
                + "attachment_url text,"
                + "pubDate integer,"
                + "recipientID integer, "
                + "read integer);"); // 0 - not read, 1 - read

        db.execSQL("create table " + TABLE_CONVERSATIONS + " ("
                + "userID integer," // meant sender id to construct UserElement object, always != currentAccID
                + "name text,"
                + "username text,"
                + "profile_image_url text," // these fields has such names to match userElement constructor
                + "recipientID integer," // id of local user
                + "unread_count integer,"
                + "lastDate integer," // last update date
                + "PRIMARY KEY (userID, recipientID));");

        db.execSQL("create table " + TABLE_DRAFTS + " ("
                + "id integer primary key autoincrement,"
                + "name text" + ");");

        db.execSQL("create table " + TABLE_SAVED_SEARCHES + " ("
                + "id integer primary key autoincrement,"
                + "userID integer,"
                + "name text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
