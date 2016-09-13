package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.design.animation.OnEndAnimationListener;
import ru.eadm.nobird.design.animation.OnStartAnimationListener;

public abstract class AbsTweetRecycleViewFragmentNested extends AbsTweetRecycleViewFragment {
    protected TextView counter;
    private int count;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.fragment_feed, container, false);
        final RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.fragment_feed_recycle_view);

        setRefreshLayout((SwipeRefreshLayout) page.findViewById(R.id.fragment_feed_swipe_refresh_layout));
        setRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                hideCounter();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        if (refreshTask == null) {
            refreshTask = createTask(POSITION_END, AbsTweetRecycleViewRefreshTask.Source.CACHE);
            refreshTask.execute(0L, 0L);
        }

        counter = (TextView) page.findViewById(R.id.fragment_feed_counter);
        counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                recyclerView.scrollToPosition(0);
                hideCounter();
            }
        });

        if (count != 0) showCounter(count);

        return page;
    }

    public void showCounter(final int count) {
        this.count = count;
        if (counter != null) {
            counter.setText(String.format(getString(R.string.feed_counter_text), count));
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_start);
            animation.setAnimationListener(new OnStartAnimationListener(counter));
            counter.startAnimation(animation);
        }
    }

    public void hideCounter() {
        if (counter != null && count != 0) {
            count = 0;
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_end);
            animation.setAnimationListener(new OnEndAnimationListener(counter));
            counter.startAnimation(animation);
        }
    }

    protected abstract AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source);

    @Override
    protected AbsTweetRecycleViewRefreshTask createRefreshTask(int pos) {
        return createTask(pos, AbsTweetRecycleViewRefreshTask.Source.API);
    }
}
