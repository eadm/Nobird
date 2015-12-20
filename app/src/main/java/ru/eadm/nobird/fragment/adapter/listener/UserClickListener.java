package ru.eadm.nobird.fragment.adapter.listener;

import android.view.View;

import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.UserFragment;

public class UserClickListener implements View.OnClickListener {
    private final UserElement user;
    public UserClickListener(final UserElement user) {
        this.user = user;
    }

    @Override
    public void onClick(View v) {
        UserFragment.showUser(user);
    }
}
