package ru.eadm.nobird.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.TwitterException;


public final class Account extends Fragment implements View.OnClickListener {
    public static final String TAG = "account_fragment";
    private WebView webView;
    private TextView sign_in_button, processing;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View page = inflater.inflate(R.layout.fragment_account, container, false);


        ((TextView) page.findViewById(R.id.account_fragment_title)).setTypeface(FontMgr.getInstance().RobotoLight);

        sign_in_button = (TextView) page.findViewById(R.id.account_fragment_sign_in);
        sign_in_button.setTypeface(FontMgr.getInstance().RobotoLight);
        sign_in_button.setOnClickListener(this);

        processing = (TextView) page.findViewById(R.id.account_fragment_processing);
        processing.setTypeface(FontMgr.getInstance().RobotoLight);

        webView = (WebView) page.findViewById(R.id.account_fragment_web);
        webView.setWebViewClient(new WebClient());

        if (savedInstanceState == null) {
            CookieManager.getInstance().removeAllCookie();
        }

        Log.d(Account.TAG, "onCreate");
        return page;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            if (savedInstanceState.getBoolean("isProcessingVisible")) {
                sign_in_button.setVisibility(View.GONE);
                processing.setVisibility(View.VISIBLE);
            } else
            if (savedInstanceState.getBoolean("isWebViewVisible")) {
                webView.setVisibility(View.VISIBLE);
                webView.restoreState(savedInstanceState);
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isWebViewVisible", webView.getVisibility() == View.VISIBLE);
        outState.putBoolean("isProcessingVisible", processing.getVisibility() == View.VISIBLE);
        webView.saveState(outState);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.account_fragment_sign_in: {
                webView.setVisibility(View.VISIBLE);
                new AuthURLTask(this).execute();
            }
        }
    }

    private final static class AuthURLTask extends AsyncTask<Void, Void, String> {
        private Account fragment;

        private AuthURLTask(final Account fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return TwitterMgr.getInstance().getAuthURL();
            } catch (final TwitterException e) {
                Log.e(Account.TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String url) {
            if (url != null) {
                fragment.webView.loadUrl(url);
                fragment.webView.requestFocus(View.FOCUS_DOWN);
            } else {
                NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, fragment.webView);
            }
        }
    }

    private final class AuthSuccessTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                TwitterMgr.getInstance().authSuccess(params[0]);
                return true;
            } catch (TwitterException e) {
                Log.e(Account.TAG, e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                FragmentMgr.getInstance().replaceFragment(0, new Home(), false);
                /// timeline fragment
            } else {
                sign_in_button.setVisibility(View.VISIBLE);
                processing.setVisibility(View.GONE);
                NotificationMgr.getInstance().showSnackbar(R.string.error_twitter_api, webView);
            }
        }
    }

    private final class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            if (url.startsWith(TwitterMgr.CALLBACK)) {
                if (url.contains("success")) {
                    final Uri uri = Uri.parse(url);
                    webView.setVisibility(View.GONE);
                    sign_in_button.setVisibility(View.GONE);
                    processing.setVisibility(View.VISIBLE);

                    new AuthSuccessTask().execute(uri.getQueryParameter(TwitterMgr.OAUTH_VERIFIER)); // async task to add a new account
                } else {
                    TwitterMgr.getInstance().authFailure();
                }
                return true;
            }
            return false;
        }
    }
}