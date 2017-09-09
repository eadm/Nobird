package ru.eadm.nobird.data.twitter.resources;

import java.util.List;

import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.MessageElement;
import twitter4j.TwitterException;

/**
 * Class providing resources to work with direct messages
 */

public final class DirectMessageResources {
    /**
     * Loads list of direct messages and saves them to DB
     * @param sinceID {Long} - minimum id of tweet, ignored if equals 0
     * @param maxID {Long} - maximum id of tweet, ignored if equals 0
     * @return list of messages
     * @throws TwitterException
     */
    public static List<MessageElement> getDirectMessages(final long sinceID, final long maxID) throws TwitterException {
        return DBMgr.getInstance().saveMessages(TwitterMgr.getTwitter().getDirectMessages(TwitterMgr.getPaging(sinceID, maxID, 0)));
    }

}
