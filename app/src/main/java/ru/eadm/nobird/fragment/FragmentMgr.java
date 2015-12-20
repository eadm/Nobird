package ru.eadm.nobird.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import ru.eadm.nobird.R;

public class FragmentMgr {
    private WeakReference<AppCompatActivity> app;
    private static FragmentMgr instance;

    private FragmentMgr(final AppCompatActivity context) {
        app = new WeakReference<>(context);
    }

    public void attach(final AppCompatActivity context) {
        app = new WeakReference<>(context);
    }

    public synchronized static void init(final AppCompatActivity context) {
        if (instance == null) {
            instance = new FragmentMgr(context);
        }
    }

    public synchronized static FragmentMgr getInstance() {
        return instance;
    }

    public void addFragment(final int rootID, final Fragment fragment, final boolean backStack) {
        if (app.get() == null) return;

        final FragmentTransaction transaction = app.get().getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add((rootID == 0 ? R.id.fragment_container : rootID), fragment, fragment.getTag());
        if (backStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void replaceFragment(final int rootID, final Fragment fragment, final boolean backStack) {
        if (app.get() == null) return;

        final FragmentTransaction transaction = app.get().getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace((rootID == 0 ? R.id.fragment_container : rootID), fragment, fragment.getTag());
        if (backStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void back() {
        if (app.get() == null) return;

        app.get().getSupportFragmentManager().popBackStack();
    }
}
