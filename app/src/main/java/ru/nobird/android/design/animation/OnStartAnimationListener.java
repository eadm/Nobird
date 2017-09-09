package ru.nobird.android.design.animation;

import android.view.View;
import android.view.animation.Animation;

/**
 * Makes element visible before animation
 */
public final class OnStartAnimationListener implements Animation.AnimationListener {
    public OnStartAnimationListener(final View view) {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationStart(final Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
