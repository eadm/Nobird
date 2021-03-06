package ru.eadm.nobird.data.twitter;


import ru.eadm.nobird.data.types.AccountElement;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public final class TwitterLogin {
    public final Twitter twitter;
    public final RequestToken requestToken;

    public TwitterLogin(final TwitterFactory factory) throws TwitterException {
        this.twitter = factory.getInstance();
        this.twitter.setOAuthConsumer(TwitterMgr.CONSUMER_KEY, TwitterMgr.CONSUMER_SECRET);
        this.requestToken = twitter.getOAuthRequestToken();
    }

    public TwitterLogin(final TwitterFactory factory, final AccountElement account) {
        this.twitter = factory.getInstance();
        this.twitter.setOAuthConsumer(TwitterMgr.CONSUMER_KEY, TwitterMgr.CONSUMER_SECRET);
        this.twitter.setOAuthAccessToken(new AccessToken(account.token, account.token_secret, account.userID));

        requestToken = null;
    }

    public AccessToken getAccessToken(final String verifier) throws TwitterException {
        return twitter.getOAuthAccessToken(requestToken, verifier);
    }
}
