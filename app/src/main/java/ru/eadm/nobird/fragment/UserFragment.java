package ru.eadm.nobird.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import ru.eadm.nobird.data.types.UserElement;

public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";

    public static void showUser(final UserElement user) {
        Log.d(TAG, user.username);
    }
}
