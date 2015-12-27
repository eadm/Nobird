package ru.eadm.nobird.design.span;

import android.text.TextPaint;
import android.view.View;

import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.UserFragment;
import twitter4j.UserMentionEntity;

public class UserSpan extends AbsSpan {
    public final static String TAG = "UserSpan";
    public final static char SPAN_TAG = 'u';


    private final UserElement user;
    public UserSpan(final long userID) {
        super();
        this.user = new UserElement(userID, "", "", "");
    }

    public UserSpan(final UserMentionEntity entity) {
        super();
        this.user = new UserElement(entity.getId(), entity.getName(), entity.getScreenName(), "");
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
        ds.setColor(0xFF777777);
    }

    @Override
    public void onClick(View widget) {
        UserFragment.showUser(user);
    }

    @Override
    public String getData() {
        return SPAN_TAG + "|" + user.userID;
    }
}
