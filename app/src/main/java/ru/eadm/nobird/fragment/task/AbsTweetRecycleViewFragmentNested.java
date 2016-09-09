package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.eadm.nobird.R;

public abstract class AbsTweetRecycleViewFragmentNested extends AbsTweetRecycleViewFragment {
    protected TextView counter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.fragment_feed, container, false);
        setRefreshLayout((SwipeRefreshLayout) page.findViewById(R.id.fragment_feed_swipe_refresh_layout));
        setRecyclerView((RecyclerView) page.findViewById(R.id.fragment_feed_recycle_view));

        if (refreshTask == null) {
            refreshTask = createTask(POSITION_END, AbsTweetRecycleViewRefreshTask.Source.CACHE);
            refreshTask.execute(0L, 0L);
        }

        counter = (TextView) page.findViewById(R.id.fragment_feed_counter);

        return page;
    }

    protected abstract AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source);

    @Override
    protected AbsTweetRecycleViewRefreshTask createRefreshTask(int pos) {
        return createTask(pos, AbsTweetRecycleViewRefreshTask.Source.API);
    }
}
