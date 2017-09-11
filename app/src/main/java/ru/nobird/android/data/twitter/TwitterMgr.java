package ru.nobird.android.data.twitter;

import android.location.Location;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import ru.nobird.android.data.PageableArrayList;
import ru.nobird.android.data.SharedPreferenceHelper;
import ru.nobird.android.data.database.DBHelper;
import ru.nobird.android.data.database.DBMgr;
import ru.nobird.android.data.types.AccountElement;
import ru.nobird.android.data.types.TweetElement;
import ru.nobird.android.data.types.UserElement;
import twitter4j.GeoLocation;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterMgr {
    public final static int TWEETS_PER_PAGE = 200;
    public final static int USER_PER_PAGE = 100;

    private static TwitterMgr instance;
    private final TwitterFactory factory;

    public final static String CONSUMER_KEY = "AwfDIqzVmPhU5OzJNzWaSCDAm";
    public final static String CONSUMER_SECRET = "NcZBQS6jFyJnKkM0z0TA3vfwwfKMXgTOjpkab2zMsn6d2EcAMf";
    public final static String CALLBACK = "https://nobird.ru/success/";
    public final static String OAUTH_VERIFIER = "oauth_verifier";

    private final static String TAG = "TwitterMgr";

    private TwitterLogin login;
    private Twitter twitter;
    public AccountElement account;

    private TwitterMgr() {
        Log.d(TwitterMgr.TAG, "create new twitter");
        factory = new TwitterFactory();
    }

    public synchronized static void init() {
        if (instance == null) {
            instance = new TwitterMgr();
        }
    }

    public synchronized static TwitterMgr getInstance() {
        return instance;
    }

    /**
     * Return twitter object
     * @return twitter object
     */
    public static Twitter getTwitter() {
        final TwitterMgr mgr = getInstance();
        if (mgr.twitter == null) mgr.localAuth();
        return mgr.twitter;
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
        if (!DBMgr.getInstance().isExistWithID(DBHelper.TABLE_ACCOUNTS, "id", accessToken.getUserId())) { // if such user already in db no need to add it again
            DBMgr.getInstance().saveAccount(accessToken, login.twitter.showUser(accessToken.getUserId()));
        }
        SharedPreferenceHelper.getInstance().setCurrentAccountID(accessToken.getUserId()); // making active that user
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
        account = DBMgr.getInstance()
                .getAccount(SharedPreferenceHelper.getInstance().getCurrentAccountID());
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
        return DBMgr.getInstance().saveStatuses(twitter.getHomeTimeline(getPaging(sinceID, maxID, TWEETS_PER_PAGE)), DBMgr.TYPE_FEED);
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
        return DBMgr.getInstance().saveStatuses(twitter.getMentionsTimeline(getPaging(sinceID, maxID, TWEETS_PER_PAGE)), DBMgr.TYPE_MENTIONS);
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
        return TwitterUtils.statusToTweetElement(twitter.getUserTimeline(userID, getPaging(sinceID, maxID, TWEETS_PER_PAGE)));
    }

    /**
     * Generates paging for request with given parameters
     * @param sinceID {Long} - minimum id of tweet, ignored if equals 0
     * @param maxID {Long} - maximum id of tweet, ignored if equals 0
     * @return {Paging} - paging for request
     */
    public static Paging getPaging(final long sinceID, final long maxID, final int number) {
        final Paging paging = new Paging();
        if (number != 0) paging.setCount(number);
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

    /**
     * Loads info about user with given user name
     * @param username {String} - name of user
     * @return {User} - user info
     * @throws TwitterException
     */
    public User showUser(final String username) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.showUser(username);
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

    /**
     * Requests status with specified id
     * @param statusID - id of tweet
     * @return {Status} - converted element
     * @throws TwitterException
     */
    public Status showStatus(final long statusID) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.showStatus(statusID);
    }


    /**
     * Search for replies to specified tweet
     * @param statusID - id of target tweet
     * @param username - username of author of targeted tweet
     * @param sinceID - min id
     * @param maxID - max id
     * @return - replies to specified tweet
     * @throws TwitterException
     */
    public ArrayList<TweetElement> getReplies(final long statusID, final String username,
                                              final long sinceID, final long maxID) throws TwitterException {
        if (twitter == null) localAuth();
        final Query query = new Query("to:" + username);
        query.setSinceId(sinceID != 0 ? sinceID : statusID);
        if (maxID != 0) {
            query.setMaxId(maxID);
        }

        final ArrayList<TweetElement> result = new ArrayList<>();
        for (final Status status : twitter.search(query).getTweets()) {
            if (status.getInReplyToStatusId() == statusID) result.add(TwitterUtils.statusToTweetElement(status));
        }
        return result;
    }

    /**
     * Retweets target status
     * @param statusID - id of target status
     * @return - updated status
     * @throws TwitterException
     */
    public Status retweet(final long statusID) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.retweetStatus(statusID);
    }

    /**
     * Likes target status
     * @param statusID - id of target status
     * @return - updated status
     * @throws TwitterException
     */
    public Status like(final long statusID, final boolean create) throws TwitterException {
        if (twitter == null) localAuth();
        if (create) {
            return twitter.createFavorite(statusID);
        } else {
            return twitter.destroyFavorite(statusID);
        }
    }

    /**
     * Creates status with given parameters
     * @param text - text of status
     * @param attachment - image attachment if null no attachment will be added
     * @param location - location of tweet if null location will not be added
     * @param inReplyTo - id of status of in reply to, ignored if 0
     */
    public Status createStatus(final String text, final String attachment, final Location location, final long inReplyTo) throws TwitterException {
        final StatusUpdate statusUpdate = new StatusUpdate(text);
        if (attachment != null) {
            final File file = new File(attachment);
            statusUpdate.setMedia(file);
        }

        if (location != null) {
            statusUpdate.setLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
        }

        if (inReplyTo != 0) statusUpdate.setInReplyToStatusId(inReplyTo);
        return twitter.updateStatus(statusUpdate);
    }

    /**
     * Tries to destroy status with given id
     * @param statusID - id of status to destroy
     * @return - destroyed status
     * @throws TwitterException
     */
    public Status destroyStatus(final long statusID) throws TwitterException {
        if (twitter == null) localAuth();
        return twitter.destroyStatus(statusID);
    }
}
