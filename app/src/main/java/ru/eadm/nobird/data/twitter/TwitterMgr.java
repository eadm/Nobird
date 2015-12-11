package ru.eadm.nobird.data.twitter;

import android.content.Context;
import android.util.Log;

import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBHelper;
import ru.eadm.nobird.data.database.DBMgr;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterMgr {
    private final Context context;
    private static TwitterMgr instance;
    private final TwitterFactory factory;

    public final static String CONSUMER_KEY = "AwfDIqzVmPhU5OzJNzWaSCDAm";
    public final static String CONSUMER_SECRET = "NcZBQS6jFyJnKkM0z0TA3vfwwfKMXgTOjpkab2zMsn6d2EcAMf";
    public final static String CALLBACK = "https://nobird.ru/success/";
    public final static String OAUTH_VERIFIER = "oauth_verifier";

    private final static String TAG = "TwitterMgr";

    private TwitterLogin login;
    private Twitter twitter;

    private TwitterMgr(final Context context) {
        Log.d(TwitterMgr.TAG, "create new twitter");
        this.context = context;
        factory = new TwitterFactory();
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new TwitterMgr(context);
        }
    }

    public synchronized static TwitterMgr getInstance() {
        return instance;
    }

    public synchronized String getAuthURL() throws TwitterException {
        login = new TwitterLogin(factory);
        return login.requestToken.getAuthenticationURL();
    }

    public synchronized void authSuccess(final String verifier) throws TwitterException {
        final AccessToken accessToken = login.getAccessToken(verifier);
        if (!DBMgr.getInstance().isExistWithID(DBHelper.TABLE_ACCOUNTS, accessToken.getUserId())) { // if such user already in db no need to add it again
            DBMgr.getInstance().saveAccount(accessToken, login.twitter.showUser(accessToken.getUserId()));
        }
        PreferenceMgr.getInstance().saveLong(PreferenceMgr.CURRENT_ACCOUNT_ID, accessToken.getUserId()); // making active that user
        this.twitter = login.twitter;
    }

    public synchronized void authFailure() {
        login = null;
    }

//    public User getUser(final long userID) throws TwitterException {
//        if (twitter != null) {
//            return twitter.showUser(userID);
//        } else {
//
//        }
//    }
}