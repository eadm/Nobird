package ru.eadm.nobird.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ruslandavletshin on 06/12/15.
 */
public final class DBHelper extends SQLiteOpenHelper {
    public final static String TABLE_ACCOUNTS = "accounts";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
