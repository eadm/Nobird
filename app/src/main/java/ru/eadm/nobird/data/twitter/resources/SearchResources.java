package ru.eadm.nobird.data.twitter.resources;

import java.util.ArrayList;
import java.util.List;

import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.database.DBHelper;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.TwitterUtils;
import ru.eadm.nobird.data.types.StringElement;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import twitter4j.Query;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Class contains only search resources
 */

public final class SearchResources {
    /**
     * Search twitter for specified query
     * @param queryString - query
     * @param sinceID - min tweet id
     * @param maxID - max tweet id
     * @return Search results
     * @throws TwitterException
     */
    public static ArrayList<TweetElement> getSearchResults(final String queryString,
                                                    final long sinceID, final long maxID) throws TwitterException {
        final Query query = new Query(queryString);
        if (sinceID != 0) query.setSinceId(sinceID);
        if (maxID != 0) query.setMaxId(maxID);

        final ArrayList<TweetElement> result = new ArrayList<>();
        for (final Status status : TwitterMgr.getTwitter().search(query).getTweets()) {
            result.add(TwitterUtils.statusToTweetElement(status));
        }
        return result;
    }

    /**
     * Returns users matching query
     * @param query - query
     * @param page - page
     * @return pageable list of users
     * @throws TwitterException
     */
    public static PageableArrayList<UserElement> getSearchUsersResults(final String query, final long page) throws TwitterException {
        final PageableArrayList<UserElement> elements = new PageableArrayList<>();
        for (final User user : TwitterMgr.getTwitter().searchUsers(query, (int)page)) {
            elements.add(new UserElement(user));
        }
        elements.setCursors(true, false, page + 1, -1);
        return elements;
    }


    /**
     * Creates saved search
     * @param query - text of query
     * @return new saved search
     * @throws TwitterException
     */
    public static StringElement createSavedSearch(final String query) throws TwitterException {
        return DBMgr.getInstance().saveSearch(TwitterMgr.getTwitter().createSavedSearch(query));
    }

    /**
     * Returns saved searches of current user
     * @return list of saved searches
     * @throws TwitterException
     */
    public static List<StringElement> getSavedSearches() throws TwitterException {
        return DBMgr.getInstance().saveSearches(TwitterMgr.getTwitter().getSavedSearches());
    }

    /**
     * Destroy saved search with given id
     * @param searchID - id of search to destroy
     * @throws TwitterException
     */
    public static void destroySavedSearch(final long searchID) throws TwitterException {
        final SavedSearch search = TwitterMgr.getTwitter().destroySavedSearch(searchID);
        DBMgr.getInstance().removeElementFromTableByID(DBHelper.TABLE_SAVED_SEARCHES, "id", search.getId());
    }
}
