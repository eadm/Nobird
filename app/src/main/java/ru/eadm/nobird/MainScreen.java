package ru.eadm.nobird;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.fragment.Account;
import ru.eadm.nobird.fragment.Home;
import ru.eadm.nobird.listener.FragmentManager;
import ru.eadm.nobird.notification.NotificationMgr;

public class MainScreen extends AppCompatActivity implements FragmentManager {
    public static final String TAG = "mainScreen_activity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initMgr(this);

        setContentView(R.layout.activity_main_screen);
        if (savedInstanceState == null) {
            if (PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID) != 0) {
                addFragment(0, new Home(), false);
                NotificationMgr.getInstance().showSnackbar("Hello " + PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID), findViewById(R.id.fragment_container));
            } else {
                addFragment(0, new Account(), false);
            }
        } else {
            Log.d(MainScreen.TAG, "rotated: " + savedInstanceState.getBoolean("rotated"));
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("rotated", true);
    }

    @Override
    public void addFragment(final int rootID, final Fragment fragment, final boolean backStack) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add((rootID == 0 ? R.id.fragment_container : rootID), fragment, fragment.getTag());
        if (backStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void replaceFragment(final int rootID, final Fragment fragment, final boolean backStack) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace((rootID == 0 ? R.id.fragment_container : rootID), fragment, fragment.getTag());
        if (backStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
