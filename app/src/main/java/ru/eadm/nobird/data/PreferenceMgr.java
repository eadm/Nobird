package ru.eadm.nobird.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceMgr {
    private final Context context;
    private static PreferenceMgr instance;

    public final static String CURRENT_ACCOUNT_ID = "current_account_id";


    private final SharedPreferences sharedPreferences;

    private PreferenceMgr(final Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new PreferenceMgr(context);
        }
    }

    public long getCurrentAccountID() {
        return getLong(CURRENT_ACCOUNT_ID);
    }

    public synchronized static PreferenceMgr getInstance() {
        return instance;
    }

    public void saveBoolean(String name, Boolean data){
        sharedPreferences.edit().putBoolean(name, data).apply();
    }

    public void saveString(final String name, final String data){
        sharedPreferences.edit().putString(name, data).apply();
    }

    public void saveInt(final String name, final int data){
        sharedPreferences.edit().putInt(name, data).apply();
    }

    public void saveLong(final String name, final long data){
        sharedPreferences.edit().putLong(name, data).apply();
    }

    public String getString(final String name){
        return sharedPreferences.getString(name, "");
    }

    public int getInt(final String name){
        return sharedPreferences.getInt(name, 0);
    }

    public long getLong(final String name){
        return sharedPreferences.getLong(name, 0);
    }

    public boolean getBoolean(final String name){
        return sharedPreferences.getBoolean(name, false);
    }

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }
    public void remove(final String name){
        sharedPreferences.edit().remove(name).apply();
    }

}