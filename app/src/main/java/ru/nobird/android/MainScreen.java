package ru.nobird.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.nobird.android.data.SharedPreferenceHelper;
import ru.nobird.android.fragment.LoginFragment;
import ru.nobird.android.fragment.implementation.FragmentMgr;
import ru.nobird.android.fragment.Home;
import ru.nobird.android.notification.NotificationMgr;

public class MainScreen extends AppCompatActivity {
    public static final String TAG = "mainScreen_activity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        FragmentMgr.init(this);
        FragmentMgr.getInstance().attach(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        NotificationMgr.getInstance().attach(this, findViewById(R.id.fragment_container));

        if (savedInstanceState == null) {
            FragmentMgr.getInstance().addFragment(0, new Home(), false);
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
