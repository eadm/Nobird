package ru.eadm.nobird.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.fragment.adapter.HomeViewPagerAdapter;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.TwitterException;


public class Home extends Fragment implements View.OnClickListener{
    public static final String TAG = "home_fragment";
    private TextView nameTextView, usernameTextView;
    private AccountElement account;
    private DrawerLayout page;
    private ImageView userImageView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        page = (DrawerLayout) inflater.inflate(R.layout.fragment_home, container, false);

        nameTextView = (TextView) page.findViewById(R.id.drawer_info_name);
        usernameTextView = (TextView) page.findViewById(R.id.drawer_info_username);

        userImageView = (ImageView) page.findViewById(R.id.drawer_info_image);

        page.findViewById(R.id.drawer_info_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFragment.showUser(PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID));
            }
        });

        if (savedInstanceState != null) {
            account = savedInstanceState.getParcelable("account");
            updateAccountInfo();
        } else {
            new AccountInitTask().execute();
        }

        final ViewPager viewPager = (ViewPager) page.findViewById(R.id.fragment_home_view_pager);
        final HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(getChildFragmentManager());

        adapter.add(new Feed(), getString(R.string.fragment_home_tab_feed));
        adapter.add(new Mentions(), getString(R.string.fragment_home_tab_mentions));

        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) page.findViewById(R.id.fragment_home_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        page.findViewById(R.id.fragment_home_menu).setOnClickListener(this);
        page.findViewById(R.id.fragment_home_search).setOnClickListener(this);

        final FloatingActionButton fab = (FloatingActionButton) page.findViewById(R.id.fragment_home_tweet_button);
        fab.setOnClickListener(this);

        return page;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nameTextView = null;
        usernameTextView = null;
        page = null;
        userImageView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        account = null;
    }

    private void updateAccountInfo() {
        if (account != null) {
            nameTextView.setText(account.name);
            usernameTextView.setText(String.format(getString(R.string.username_placeholder), account.username));
            ImageMgr.getInstance().displayImage(account.image, userImageView);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("account", account);
    }

    private final class AccountInitTask extends AsyncTask<Void, Void, AccountElement> {
        @Override
        protected AccountElement doInBackground(Void... params) {
            return TwitterMgr.getInstance().localAuth();
        }

        @Override
        protected void onPostExecute(final AccountElement accountElement) {
            if (accountElement != null) {
                account = accountElement;
                updateAccountInfo();
            } else {
                NotificationMgr.getInstance().showSnackbar(R.string.error_account_init, page);
            }
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fragment_home_menu:
                page.openDrawer(GravityCompat.START); // only open cause we can't press it when drawer opened
            break;
            case R.id.fragment_home_search:
                Log.d("fragment_home_search", "hello");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TwitterMgr.getInstance().getRateLimits();
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                // open search fragment
            break;
            case R.id.fragment_home_tweet_button:
                CreateStatusFragment.open();
            break;
        }
    }
}
