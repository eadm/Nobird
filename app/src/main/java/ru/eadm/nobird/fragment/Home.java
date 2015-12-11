package ru.eadm.nobird.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.fragment.adapter.HomeViewPagerAdapter;
import ru.eadm.nobird.notification.NotificationMgr;


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
        nameTextView.setTypeface(FontMgr.getInstance().RobotoLigth);
        usernameTextView = (TextView) page.findViewById(R.id.drawer_info_username);
        usernameTextView.setTypeface(FontMgr.getInstance().RobotoLigth);
        userImageView = (ImageView) page.findViewById(R.id.drawer_info_image);

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


        return page;
    }

    private void updateAccountInfo() {
        if (account != null) {
            nameTextView.setText(account.name);
            usernameTextView.setText("@" + account.username);
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
            final AccountElement accountElement = DBMgr.getInstance()
                    .getAccount(PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID));
            TwitterMgr.getInstance().localAuth(accountElement);
            return accountElement;
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
            case R.id.fragment_home_menu: {
                page.openDrawer(GravityCompat.START); // only open cause we can't press it when drawer opened
            }
            case R.id.fragment_home_search: {
                // open search fragment
            }
        }
    }
}
