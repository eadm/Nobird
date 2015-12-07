package ru.eadm.nobird.listener;

import android.support.v4.app.Fragment;

public interface FragmentManager {
    void replaceFragment(final int rootID, final Fragment fragment, final boolean backStack);
    void addFragment(final int rootID, final Fragment fragment, final boolean backStack);
}
