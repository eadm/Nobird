package ru.eadm.nobird.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ru.eadm.nobird.data.types.AccountElement;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public final class DBMgr {
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
}
