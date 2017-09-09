package ru.nobird.android.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ru.nobird.android.R;
import ru.nobird.android.data.ImageMgr;
import ru.nobird.android.data.PreferenceMgr;
import ru.nobird.android.data.twitter.TwitterMgr;
import ru.nobird.android.data.types.AccountElement;
import ru.nobird.android.dialog.AccountPickerDialogFragment;
import ru.nobird.android.fragment.implementation.adapter.HomeViewPagerAdapter;
import ru.nobird.android.fragment.message.ConversationListFragment;
import ru.nobird.android.fragment.search.SearchFragment;
import ru.nobird.android.notification.NotificationMgr;


public class Home extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "home_fragment";
    private TextView nameTextView, usernameTextView;
    private AccountElement account;
    private DrawerLayout page;
    private ImageView userImageView;

    private AccountInitTask accountInitTask;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        page = (DrawerLayout) inflater.inflate(R.layout.fragment_home, container, false);

        final NavigationView navigationView = (NavigationView) page.findViewById(R.id.fragment_home_navigation);
        navigationView.setNavigationItemSelectedListener(this);
        createDrawerHeader(navigationView.getHeaderView(0));

        if (accountInitTask == null) {
            accountInitTask = new AccountInitTask(this);
            accountInitTask.execute();
        } else {
            setAccount(account);
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

    private void createDrawerHeader(final View header) {
        nameTextView = (TextView) header.findViewById(R.id.drawer_info_name);
        usernameTextView = (TextView) header.findViewById(R.id.drawer_info_username);
        userImageView = (ImageView) header.findViewById(R.id.drawer_info_image);

        final View.OnClickListener userClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UserFragment.showUser(PreferenceMgr.getInstance().getCurrentAccountID());
            }
        };
        nameTextView.setOnClickListener(userClickListener);
        usernameTextView.setOnClickListener(userClickListener);
        userImageView.setOnClickListener(userClickListener);

        header.findViewById(R.id.manage_accounts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AccountPickerDialogFragment.show();
            }
        });
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

    private void setAccount(final AccountElement account) {
        if (isAdded() && account != null) {
            this.account = account;
            nameTextView.setText(account.name);
            usernameTextView.setText(String.format(getString(R.string.username_placeholder), account.username));
            ImageMgr.getInstance().displayImage(account.image, userImageView);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_drafts:
                DraftListFragment.show();
                return true;

            case R.id.drawer_messages:
                ConversationListFragment.show();
                return true;
        }
        Log.d(TAG, "item clicked: " + item.getItemId());
        return false;
    }

    private final class AccountInitTask extends AsyncTask<Void, Void, AccountElement> {
        private final WeakReference<Home> fragmentWeakReference;

        public AccountInitTask(final Home fragment) {
            this.fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected AccountElement doInBackground(final Void... params) {
            return TwitterMgr.getInstance().localAuth();
        }

        @Override
        protected void onPostExecute(final AccountElement accountElement) {
            final Home fragment = fragmentWeakReference.get();
            if (fragment != null) {
                if (accountElement != null) {
                    fragment.setAccount(accountElement);
                } else {
                    NotificationMgr.getInstance().showSnackbar(R.string.error_account_init, page);
                }
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
                SearchFragment.show();
            break;
            case R.id.fragment_home_tweet_button:
                ComposeFragment.open();
            break;
        }
    }
}
