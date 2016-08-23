package ru.eadm.nobird.data.twitter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBHelper;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import twitter4j.CursorSupport;
import twitter4j.Friendship;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterMgr {
    public final static int TWEETS_PER_PAGE = 100;
    public final static int USER_PER_PAGE = 100;

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

    /**
     * Generates auth url
     * @return {String} auth url
     * @throws TwitterException
     */
    public synchronized String getAuthURL() throws TwitterException {
        login = new TwitterLogin(factory);
        return login.requestToken.getAuthenticationURL();
    }


    /**
     * Gets OAUTH_VERIFIER and sets up new user
     *  if user don't exists adds it to DB
     *  then makes it active
     * @param verifier {String} OAUTH_VERIFIER to auth user
     * @throws TwitterException
     */
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

    /**
     * Performs local auth and returns AccountElement
     * @return {AccountElement} - data about user
     */
    public synchronized AccountElement localAuth() {
        final AccountElement account = DBMgr.getInstance()
                .getAccount(PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID));
        this.twitter = new TwitterLogin(factory, account).twitter;

        return account;
    }

    /**
     * Loads requested tweets as home timeline, saves them to DB and returns
     * @param sinceID {Long} - minimum id of tweet, ignored if equals 0
     * @param maxID {Long} - maximum id of tweet, ignored if equals 0
     * @return {ArrayList<TweetElement>} - list of requested tweets
     * @throws TwitterException
     */
    public ArrayList<TweetElement> getHomeTimeline (final long sinceID, final long maxID) throws TwitterException {
        if (twitter == null) localAuth();
        return DBMgr.getInstance().saveStatuses(twitter.getHomeTimeline(getPaging(sinceID, maxID)), DBMgr.TYPE_FEED);
    }

    /**
     * Loads requested tweets as mentions timeline, saves them to DB and returns
     * @param sinceID {Long} - minimum id of tweet, ignored if equals 0
     * @param maxID {Long} - maximum id of tweet, ignored if equals 0
     * @return {ArrayList<TweetElement>} - list of requested tweets
     * @throws TwitterException
     */
    public ArrayList<TweetElement> getMentionsTimeline (final long sinceID, final long maxID) throws TwitterException {
        if (twitter == null) localAuth();
        return DBMgr.getInstance().saveStatuses(twitter.getMentionsTimeline(getPaging(sinceID, maxID)), DBMgr.TYPE_MENTIONS);
    }

    /**
     * Loads requested tweets as user's timeline, saves them to DB and returns
     * @param sinceID {Long} - minimum id of tweet, ignored if equals 0
     * @param maxID {Long} - maximum id of tweet, ignored if equals 0
     * @return {ArrayList<TweetElement>} - list of requested tweets
     * @throws TwitterException
     */
    public ArrayList<TweetElement> getUserTimeline(final long userID, final long sinceID, final long maxID) throws TwitterException {
        if (twitter == null) localAuth();
        return TwitterUtils.statusToTweetElement(twitter.getUserTimeline(userID, getPaging(sinceID, maxID)));
    }

    /**
     * Generates paging for request with given parameters
     * @param sinceID {Long} - minimum id of tweet, ignored if equals 0
     * @param maxID {Long} - maximum id of tweet, ignored if equals 0
     * @return {Paging} - paging for request
     */
    private Paging getPaging(final long sinceID, final long maxID) {
        final Paging paging = new Paging();
        paging.setCount(TWEETS_PER_PAGE);
        if (sinceID != 0) paging.setSinceId(sinceID);
        if (maxID != 0) paging.setMaxId(maxID);
        return paging;
    }

    /**
     * Loads info about user with given ID
     * @param userID {Long} - id of user
     * @return {User} - user info
     * @throws TwitterException
     */
    public User showUser(final long userID) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.showUser(userID);
    }

    public PageableArrayList<UserElement> getUserFollowers(final long userID, final long cursor) throws TwitterException {
        final PagableResponseList<User> users = twitter.getFollowersList(userID, cursor, USER_PER_PAGE, true, false);

        final PageableArrayList<UserElement> result = new PageableArrayList<>(users.size());
        for (final User user : users) {
            result.add(new UserElement(user));
        }
        result.setCursors(users);
        return result;
    }

    public PageableArrayList<UserElement> getUserFriends(final long userID, final long cursor) throws TwitterException {
        final PagableResponseList<User> users = twitter.getFriendsList(userID, cursor, USER_PER_PAGE, true, false);

        final PageableArrayList<UserElement> result = new PageableArrayList<>(users.size());
        for (final User user : users) {
            result.add(new UserElement(user));
        }
        result.setCursors(users);
        return result;
    }

    public Relationship getRelationship(final long sourceID, final long targetID) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.showFriendship(sourceID, targetID);
    }

    /**
     * Changes friendship status
     * @param targetID - id of target user
     * @param create - if true creates friendship destroys otherwise
     * @return updated target user object
     * @throws TwitterException
     */
    public User changeFriendship(final long targetID, final boolean create) throws TwitterException {
        if (twitter == null) localAuth();
        if (create) {
            return twitter.createFriendship(targetID);
        } else {
            return twitter.destroyFriendship(targetID);
        }
    }

    /**
     * Changes mute status
     * @param targetID - id of target user
     * @param create - if true creates mute destroys otherwise
     * @return updated target user object
     * @throws TwitterException
     */
    public User changeMuteStatus(final long targetID, final boolean create) throws TwitterException {
        if (twitter == null) localAuth();
        if (create) {
            return twitter.createMute(targetID);
        } else {
            return twitter.destroyMute(targetID);
        }
    }

    /**
     * Changes mute status
     * @param targetID - id of target user
     * @param create - if true creates mute destroys otherwise
     * @return updated target user object
     * @throws TwitterException
     */
    public User changeBlockStatus(final long targetID, final boolean create) throws TwitterException {
        if (twitter == null) localAuth();
        if (create) {
            return twitter.createBlock(targetID);
        } else {
            return twitter.destroyBlock(targetID);
        }
    }

    /**
     * Report about spam from user with specified id
     * @param targetID - id of target user
     * @return updated target user object
     * @throws TwitterException
     */
    public User reportSpam(final long targetID) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.reportSpam(targetID);
    }

    public void getRateLimits() throws TwitterException {
        final Map<String, RateLimitStatus> stats  = twitter.getRateLimitStatus();
        for (final Map.Entry<String, RateLimitStatus> stat: stats.entrySet()) {
            Log.d("LIMITS", stat.getKey() + ": " + stat.getValue().getRemaining() + "/" + stat.getValue().getLimit());
        }
    }


}
