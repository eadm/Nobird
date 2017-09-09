package ru.nobird.android.design.animation;

import android.view.View;
import android.view.animation.Animation;

public class OnEndAnimationListener  implements Animation.AnimationListener {
    private final View view;
    public OnEndAnimationListener(final View view) {
        this.view = view;
    }

    @Override
    public void onAnimationStart(final Animation animation) {}

    @Override
    public void onAnimationEnd(final Animation animation) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}