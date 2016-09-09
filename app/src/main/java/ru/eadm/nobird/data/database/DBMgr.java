package ru.eadm.nobird.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.data.types.TweetElement;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public final class DBMgr {
    private final String TAG = this.getClass().getName();

    public final static int TYPE_FEED = 0;
    public final static int TYPE_MENTIONS = 1;

    private final Context context;
    private static DBMgr instance;

    private final SQLiteDatabase db;

    private DBMgr(final Context context) {
        this.context = context;
        db = new DBHelper(this.context).getWritableDatabase();
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new DBMgr(context);
        }
    }

    public synchronized static DBMgr getInstance() {
        return instance;
    }

    public boolean isExistWithID(final String table, final long id) {
        final Cursor cursor = db.query(table, null, "id = " + id, null, null, null, "id DESC");
        final boolean r = cursor.moveToFirst();
        cursor.close();
        return r;
    }

    public void saveAccount(final AccessToken accessToken, final User user) {
        final ContentValues cv = new ContentValues();
        cv.put("id", user.getId());
        cv.put("name", user.getName());
        cv.put("username", user.getScreenName());
        cv.put("image_url", user.getOriginalProfileImageURL());
        cv.put("token", accessToken.getToken());
        cv.put("token_secret", accessToken.getTokenSecret());
        db.insert(DBHelper.TABLE_ACCOUNTS, null, cv);
    }

    public AccountElement getAccount(final long userID) {
        final Cursor cursor = db.query(DBHelper.TABLE_ACCOUNTS, null, "id = " + userID, null, null, null, "id DESC");
        AccountElement account = null;
        if (cursor.moveToFirst()) {
            final String name = cursor.getString(cursor.getColumnIndex("name"));
            final String username = cursor.getString(cursor.getColumnIndex("username"));
            final String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
            final String token = cursor.getString(cursor.getColumnIndex("token"));
            final String token_secret = cursor.getString(cursor.getColumnIndex("token_secret"));
            account = new AccountElement(userID, name, username, image_url, token, token_secret);
        }
        cursor.close();
        return account;
    }

    public ArrayList<TweetElement> getCachedStatuses(final int type) {
        return getCachedStatuses(PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID), type);
    }
    public ArrayList<TweetElement> getCachedStatuses(final long userID, final int type) {
        final ArrayList<TweetElement> tweets = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_TWEETS + " " +
                "WHERE type = ? " +
                "AND ownerID = ? " +
                "ORDER BY tweetID DESC " +
                "LIMIT ?", new String[]{type + "", userID + "", TwitterMgr.TWEETS_PER_PAGE + ""});
        if (cursor.moveToFirst()) {
            do {
                tweets.add(new TweetElement(
                        cursor.getLong(cursor.getColumnIndex("tweetID")),
                        cursor.getLong(cursor.getColumnIndex("userID")),

                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        cursor.getString(cursor.getColumnIndex("profile_image_url")),
                        TwitterStatusText.parse(
                                cursor.getString(cursor.getColumnIndex("tweet_text")),
                                cursor.getString(cursor.getColumnIndex("tweet_text_parse_key"))
                        ),
                        new Date(cursor.getLong(cursor.getColumnIndex("pubDate"))),
                        cursor.getString(cursor.getColumnIndex("attachment_url"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tweets;
    }

    public ArrayList<TweetElement> saveStatuses(final List<Status> statuses, final int type) { // converts and saves statuses for N
        final ArrayList<TweetElement> tweets = new ArrayList<>(statuses.size());
        db.beginTransaction();
        final SQLiteStatement st = db.compileStatement(DBHelper.TABLE_TWEETS_PATTERN);
        for (final Status status : statuses) {
            final TweetElement tweet = TwitterUtils.statusToTweetElement(status);
            tweets.add(tweet); // add to array

            Log.d(TAG, tweet.text.getParseKey());
            st.bindLong(1, tweet.tweetID);
            st.bindLong(2, tweet.user.userID);
            st.bindString(3, tweet.user.name);
            st.bindString(4, tweet.user.username);
            st.bindString(5, tweet.user.image);
            st.bindString(6, tweet.text.getText().toString());
            st.bindString(7, tweet.text.getParseKey());
            st.bindString(8, tweet.image);
            st.bindLong(9, tweet.date.getTime());

            st.bindLong(10, PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID));
            st.bindLong(11, type);
            st.executeInsert(); // add to db
        }
        st.close();
        final long count = getCachedStatusesCount(type);
        if (count > TwitterMgr.TWEETS_PER_PAGE) {
            clearCachedStatuses(count - TwitterMgr.TWEETS_PER_PAGE, type); // delete useless statuses
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return tweets;
    }

    private void clearCachedStatuses(final long count, final int type) { // delete last n tweets in table
        db.execSQL(DBHelper.TABLE_TWEET_CLEAR_PATTERN, new Object[]{
                PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID),
                type,
                count
        });
    }

//    public boolean isEmpty(final String table, final String selection, final String[] selectors) {
//        return getItemCount(table, selection, selectors) == 0;
//    }

    public long getItemCount(final String table, final String selection, final String[] selectors) {
        return DatabaseUtils.queryNumEntries(db, table, selection, selectors);
    }

    public long getCachedStatusesCount(final int type) {
        Log.d(TAG, type + "");
        return getItemCount(DBHelper.TABLE_TWEETS, "type = ? AND ownerID = ?",
                new String[]{
                        type + "",
                        PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID) + ""
                });
    }

    public void saveDraft(final String text) {
        final ContentValues cv = new ContentValues();
        cv.put("name", text);
        db.insert(DBHelper.TABLE_DRAFTS, null, cv);
    }

    public List<String> getDrafts() {
        final Cursor cursor = db.query(DBHelper.TABLE_ACCOUNTS, null, null, null, null, null, "id DESC");
        final ArrayList<String> drafts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                drafts.add(cursor.getString(cursor.getColumnIndex("name")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return drafts;
    }

    public void removeElementFromTableByID(final String table, final String fieldName, final long id){
        Log.d(TAG, "removeElementFromTableByID: " + db.delete(table, fieldName + " = " + id, null));
    }
}
