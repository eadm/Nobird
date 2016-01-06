package ru.eadm.nobird.fragment.task;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;


public class AbsTweetRecycleViewFragment extends Fragment  {
    private final String TAG = this.getClass().getName();

    public static final int POSITION_START = 0;
    public static final int POSITION_END = 1;

    protected SwipeRefreshLayout refreshLayout;
    protected TweetRecycleViewAdapter adapter;
    protected TextView counter;

    public void setRefreshing(final boolean state) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(state);
            }
        });
    }

    public void showCounter(final int count) {
        if (count == 0 || counter.getVisibility() == View.VISIBLE) return;
        counter.setText(String.format(getString(R.string.digit_placeholder), count));
        counter.setVisibility(View.VISIBLE);
        counter.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
    }

    public void hideCounter() {
        if (counter.getVisibility() == View.GONE) return;
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                counter.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        counter.startAnimation(animation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout = null;
        adapter = null;
        counter = null;
    }
}
