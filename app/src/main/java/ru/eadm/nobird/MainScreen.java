package ru.eadm.nobird;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.fragment.Account;
import ru.eadm.nobird.fragment.implementation.FragmentMgr;
import ru.eadm.nobird.fragment.Home;
import ru.eadm.nobird.notification.NotificationMgr;

public class MainScreen extends AppCompatActivity {
    public static final String TAG = "mainScreen_activity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Util.initMgr(this);
        FragmentMgr.getInstance().attach(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        NotificationMgr.getInstance().attach(this, findViewById(R.id.fragment_container));

        if (savedInstanceState == null) {
            if (PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID) != 0) {
                FragmentMgr.getInstance().addFragment(0, new Home(), false);
            } else {
                FragmentMgr.getInstance().addFragment(0, new Account(), false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
