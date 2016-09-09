package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;

public abstract class AbsTweetRecycleViewFragmentNested extends AbsTweetRecycleViewFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = this.getClass().getName();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.fragment_feed, container, false);
        refreshLayout = (SwipeRefreshLayout) page.findViewById(R.id.fragment_feed_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        final RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.fragment_feed_recycle_view);


        if (getState().getData() == null) {
            adapter = new TweetRecycleViewAdapter();
            getState().setData(adapter.getData());
            getState().setTask(createTask(POSITION_END, AbsTweetRecycleViewRefreshTask.Source.CACHE));
            getState().getTask().execute(0L, 0L);
        } else {
            adapter = new TweetRecycleViewAdapter(getState().getData());

            if (getState().getTask() != null) {
                getState().getTask().attachFragment(this);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.getItemCount() - recyclerView.getChildCount()
                        <= layoutManager.findFirstVisibleItemPosition()) {
                    onScrolledToEnd();
                }
            }

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ImageMgr.getInstance().listener.onScrollStateChanged(null, newState);

                hideCounter();
            }
        });

        counter = (TextView) page.findViewById(R.id.fragment_feed_counter);

        return page;
    }

    @Override
    public void onRefresh() {
        if (getState().getTask() != null
                && getState().getTask().getState() == TaskState.COMPLETED) {
            getState().setTask(createTask(POSITION_START, AbsTweetRecycleViewRefreshTask.Source.API));
            getState().getTask().execute((adapter.getItemCount() == 0) ? 0 : adapter.getData().get(0).tweetID, 0L);

            Log.d(TAG, "onRefresh");
        }
    }

    public void onScrolledToEnd() {
        if (getState().getTask() != null
                && getState().getTask().getState() == TaskState.COMPLETED) {
            getState().setTask(createTask(POSITION_END, AbsTweetRecycleViewRefreshTask.Source.API));
            getState().getTask().execute(0L, adapter.getData().get(adapter.getData().size() - 1).tweetID - 1);

            Log.d(TAG, "onScrolledToEnd");
        }
    }

    protected abstract AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source);
    protected abstract AbsTweetRecycleViewState getState();

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        if (adapter != null) { // cause sometimes adapter = null, during screen rotation
            getState().setData(adapter.getData());
        }
    }


}
