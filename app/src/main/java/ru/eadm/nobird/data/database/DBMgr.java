package ru.eadm.nobird.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.eadm.nobird.Util;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.data.types.ConversationElement;
import ru.eadm.nobird.data.types.MessageElement;
import ru.eadm.nobird.data.types.StringElement;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import twitter4j.DirectMessage;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public final class DBMgr {
    private final String TAG = this.getClass().getName();

    public final static int TYPE_FEED = 0;
    public final static int TYPE_MENTIONS = 1;

    private static DBMgr instance;

    private final SQLiteDatabase db;

    private DBMgr(final Context context) {
        db = new DBHelper(context).getWritableDatabase();
    }

    public synchronized static void init(final Context context) {
        if (instance == null) {
            instance = new DBMgr(context);
        }
    }

    public synchronized static DBMgr getInstance() {
        return instance;
    }

    public boolean isExistWithID(final String table, final String fieldName, final long id) {
        final Cursor cursor = db.query(table, null, fieldName + " = " + id, null, null, null, "id DESC");
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

    public List<AccountElement> getAccounts() {
        final Cursor cursor = db.query(DBHelper.TABLE_ACCOUNTS, null, null, null, null, null, "id DESC");
        final List<AccountElement> accounts = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                final long userID = cursor.getLong(cursor.getColumnIndex("id"));
                final String name = cursor.getString(cursor.getColumnIndex("name"));
                final String username = cursor.getString(cursor.getColumnIndex("username"));
                final String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
                final String token = cursor.getString(cursor.getColumnIndex("token"));
                final String token_secret = cursor.getString(cursor.getColumnIndex("token_secret"));
                accounts.add(new AccountElement(userID, name, username, image_url, token, token_secret));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }

    public ArrayList<TweetElement> getCachedStatuses(final int type) {
        return getCachedStatuses(PreferenceMgr.getInstance().getCurrentAccountID(), type);
    }
    private ArrayList<TweetElement> getCachedStatuses(final long userID, final int type) {
        final ArrayList<TweetElement> tweets = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_TWEETS + " " +
                "WHERE type = ? " +
                "AND ownerID = ? " +
                "ORDER BY tweetID DESC " +
                "LIMIT ?", new String[]{type + "", userID + "", TwitterMgr.TWEETS_PER_PAGE + ""});
        if (cursor.moveToFirst()) {
            do {
                tweets.add(new TweetElement(cursor));
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
            st.bindString(8, Util.join(tweet.images, "|"));
            st.bindLong(9, tweet.date.getTime());

            st.bindLong(10, PreferenceMgr.getInstance().getCurrentAccountID());
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

    /**
     * Saves draft
     * @param text - text of draft
     */
    public void saveDraft(final String text) {
        final ContentValues cv = new ContentValues();
        cv.put("name", text);
        db.insert(DBHelper.TABLE_DRAFTS, null, cv);
    }

    /**
     * Returns list of drafts
     * @return - list of drafts
     */
    public List<StringElement> getDrafts() {
        final Cursor cursor = db.query(DBHelper.TABLE_DRAFTS, null, null, null, null, null, "id DESC");
        final ArrayList<StringElement> drafts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                drafts.add(new StringElement(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return drafts;
    }

    /**
     * Saves search
     * @param search - search to save
     * @return - string element based on saved search
     */
    public StringElement saveSearch(final SavedSearch search) {
        final ContentValues cv = new ContentValues();
        cv.put("id", search.getId());
        cv.put("name", search.getQuery());
        cv.put("userID", PreferenceMgr.getInstance().getCurrentAccountID());
        db.insert(DBHelper.TABLE_SAVED_SEARCHES, null, cv);
        return new StringElement(search);
    }

    /**
     * Saves list of searches and removes old elements
     * @param searches - list of searches
     * @return - list of searches converted to string elements
     */
    public List<StringElement> saveSearches(final List<SavedSearch> searches) {
        db.delete(DBHelper.TABLE_SAVED_SEARCHES, "userID = " + PreferenceMgr.getInstance().getCurrentAccountID(), null);

        final List<StringElement> result = new ArrayList<>(searches.size());

        db.beginTransaction();
        final SQLiteStatement st = db.compileStatement(DBHelper.TABLE_SAVED_SEARCHES_PATTERN);
        for (final SavedSearch search : searches) {
            st.bindLong(1, search.getId());
            st.bindLong(2, PreferenceMgr.getInstance().getCurrentAccountID());
            st.bindString(3, search.getQuery());
            st.executeInsert();

            result.add(new StringElement(search));
        }
        st.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    /**
     * Returns list of saved searches from cache
     * @return list of saved searches
     */
    public List<StringElement> getSearches() {
        final Cursor cursor = db.query(DBHelper.TABLE_SAVED_SEARCHES, null, "userID = " + PreferenceMgr.getInstance().getCurrentAccountID(), null, null, null, "id");
        final List<StringElement> searches = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                final long searchID = cursor.getLong(cursor.getColumnIndex("id"));
                final String name = cursor.getString(cursor.getColumnIndex("name"));
                searches.add(new StringElement(searchID, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searches;
    }

    /**
     * Gets saved search by query
     * @param query - search query
     * @return saved search if exists or null otherwise
     */
    public StringElement getSearchByQuery(final String query) {
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_SAVED_SEARCHES + " " +
                "WHERE userID = ? " +
                "AND name = ?", new String[]{
                Long.toString(PreferenceMgr.getInstance().getCurrentAccountID()),
                query
        });
        StringElement r = null;
        if (cursor.moveToFirst()) {
            final long searchID = cursor.getLong(cursor.getColumnIndex("id"));
            final String name = cursor.getString(cursor.getColumnIndex("name"));
            r = new StringElement(searchID, name);
        }
        cursor.close();
        return r;
    }

    public void removeElementFromTableByID(final String table, final String fieldName, final long id){
        Log.d(TAG, "removeElementFromTableByID: " + db.delete(table, fieldName + " = " + id, null));
    }


    /**
     * Saves list of direct messages to db
     * @param messages - list of DMs
     * @return - list of DM converted to message element
     */
    public List<MessageElement> saveMessages(final List<DirectMessage> messages) {
        final List<MessageElement> result = new ArrayList<>(messages.size());
        final LongSparseArray<Integer> messagesCount = new LongSparseArray<>(); // to count new messages for each sender

        final long currentUser = PreferenceMgr.getInstance().getCurrentAccountID();

        db.beginTransaction();
        final SQLiteStatement st = db.compileStatement(DBHelper.TABLE_MESSAGES_PATTERN);
        final SQLiteStatement senderPattern = db.compileStatement(DBHelper.TABLE_CONVERSATIONS_PATTERN);
        for (final DirectMessage message : messages) {
            final MessageElement element = new MessageElement(message);

            st.bindLong(1, element.getID());
            st.bindLong(2, element.senderID);

            st.bindString(3, element.text.getText().toString());
            st.bindString(4, element.text.getParseKey());
            st.bindString(5, Util.join(element.images, "|"));

            st.bindLong(6, element.date.getTime());
            st.bindLong(7, PreferenceMgr.getInstance().getCurrentAccountID());

            st.executeInsert();

            final long conversationID = element.senderID == currentUser ? message.getRecipientId() : element.senderID;
            messagesCount.put(conversationID, messagesCount.get(conversationID, 0) + 1);
            saveConversation(message, senderPattern);

            result.add(element);
        }
        st.close();
        senderPattern.close();

        updateConversations(messagesCount);

        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    /**
     * Add conversation to db, use only in saveMessages
     * @param message - targeted message
     * @param st - SQLite statement of TABLE_SENDERS_PATTERN
     */
    private void saveConversation(final DirectMessage message, final SQLiteStatement st) {
        final long currentUser = PreferenceMgr.getInstance().getCurrentAccountID();
        final User sender = message.getSenderId() == currentUser ? message.getRecipient() : message.getSender();
        st.bindLong(1, sender.getId());
        st.bindString(2, sender.getName());
        st.bindString(3, sender.getScreenName());
        st.bindString(4, sender.getBiggerProfileImageURLHttps());
        st.bindLong(5, currentUser);
//        st.bindLong(6, 0);
        st.bindLong(6, message.getCreatedAt().getTime());
        st.executeInsert();
    }

    /**
     * Updates count of new messages in specified conversations
     * @param messagesCount map: conversationID -> new messages counts
     */
    private void updateConversations(final LongSparseArray<Integer> messagesCount) {
        final StringBuilder queryCase = new StringBuilder("UPDATE " + DBHelper.TABLE_CONVERSATIONS +
                " SET unread_count = CASE userID");
        final StringBuilder queryWhere = new StringBuilder(" END WHERE recipientID = ");
        queryWhere.append(PreferenceMgr.getInstance().getCurrentAccountID());
        queryWhere.append(" AND userID in (");

        for (int i = 0; i < messagesCount.size(); ++i) {
            queryCase.append(" WHEN ");
            queryCase.append(messagesCount.keyAt(i));
            queryCase.append(" THEN unread_count + ");
            queryCase.append(messagesCount.valueAt(i));

            queryWhere.append(messagesCount.keyAt(i));
            queryWhere.append(", ");
        }
        queryWhere.append(")");

        queryCase.append(queryWhere);
        db.execSQL(queryCase.toString());
    }


    public List<ConversationElement> getConversations() {
        final Cursor cursor = db.query(DBHelper.TABLE_CONVERSATIONS, null, "recipientID = " + PreferenceMgr.getInstance().getCurrentAccountID(), null, null, null, "lastDate DESC");
        final List<ConversationElement> conversations = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                final UserElement user = new UserElement(cursor);
                conversations.add(new ConversationElement(user, getLastMessageBySender(user.getID())));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conversations;
    }

    @Nullable
    private MessageElement getLastMessageBySender(final long senderID) {
        final List<MessageElement> elements = getMessages(senderID, 1);
        if (elements.isEmpty()) return null;
        return elements.get(0);
    }

    /**
     * Gets list of messages from DB
     * @param senderID - id of sender
     * @param limit - limit of messages, if eq 0 -> no limit
     * @return list of cached messages
     */
    public List<MessageElement> getMessages(final long senderID, final int limit) {
        final Cursor cursor = db.query(DBHelper.TABLE_MESSAGES, null,
                "( recipientID = " + PreferenceMgr.getInstance().getCurrentAccountID() + "AND senderID = " + senderID + " ) OR ( " +
                        "recipientID = " + senderID + " AND senderID = " + PreferenceMgr.getInstance().getCurrentAccountID() + " )",
                null, null, null, "lastDate DESC", limit != 0 ? Integer.toString(limit) : "");
        final List<MessageElement> messages = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                messages.add(new MessageElement(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }
}
