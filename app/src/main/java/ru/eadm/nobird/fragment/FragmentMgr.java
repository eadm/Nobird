package ru.eadm.nobird.fragment;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    public void showDialog(final DialogFragment dialogFragment) {
        if (app.get() == null) return;
        dialogFragment.show(app.get().getSupportFragmentManager(), dialogFragment.getTag());
    }

    public void back() {
        if (app.get() == null) return;

        app.get().getSupportFragmentManager().popBackStack();
    }

    public Context getContext() { return app.get(); }

    public void copyToClipboard(final String text) {
        android.content.ClipboardManager clipboardManager =
                (android.content.ClipboardManager) app.get().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("text", text);
        clipboardManager.setPrimaryClip(clip);
    }
}
