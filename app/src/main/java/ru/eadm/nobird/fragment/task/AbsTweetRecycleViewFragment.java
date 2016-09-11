package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;

/**
 * Abs class to create fragments with recycle view with tweets
 */
public abstract class AbsTweetRecycleViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int POSITION_START = 0;
    public static final int POSITION_END = 1;

    protected AbsTweetRecycleViewRefreshTask refreshTask;
    protected SwipeRefreshLayout refreshLayout;
    protected TweetRecycleViewAdapter adapter;

    /**
     * Should be called from onCreateView method
     * @param refreshLayout - refresh layout object
     */
    protected void setRefreshLayout(final SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        refreshLayout.setOnRefreshListener(this);
    }
    /**
     * Should be called from onCreateView method
     * @param recyclerView - recycle view object
     */
    protected void setRecyclerView(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean scrolledToEnd = false;

            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return; // ignore if you scrolls up
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.getItemCount() - recyclerView.getChildCount()
                        <= layoutManager.findFirstVisibleItemPosition()) {
                    if (!scrolledToEnd) onScrolledToEnd();
                    scrolledToEnd = true;
                } else {
                    scrolledToEnd = false;
                }
            }

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ImageMgr.getInstance().listener.onScrollStateChanged(null, newState);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets refreshing animation
     * @param state - state of animation
     */
    public void setRefreshing(final boolean state) {
        if (refreshLayout == null) return;
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null) refreshLayout.setRefreshing(state);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (refreshTask != null &&
                refreshTask.getState() == TaskState.COMPLETED) {
            refreshTask = createRefreshTask(POSITION_START);
            refreshTask.execute((adapter.getItemCount() == 0) ? 0 : adapter.get(0).tweetID, 0L);
        } else {
            setRefreshing(false);
        }
    }

    private void onScrolledToEnd() {
        if (refreshTask != null &&
                refreshTask.getState() == TaskState.COMPLETED) {
            refreshTask = createRefreshTask(POSITION_END);
            refreshTask.execute(0L, adapter.getData().get(adapter.getData().size() - 1).tweetID - 1);
        }
    }

    /**
     * Creates new refresh task with given position
     * @param pos if equals POSITION_START appends data to start otherwise to end
     * @return new task
     */
    protected abstract AbsTweetRecycleViewRefreshTask createRefreshTask(final int pos);

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        adapter = new TweetRecycleViewAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;

        if (refreshTask != null)  {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout = null;
    }

    /**
     * Returns current adapter
     * @return adapter of recycler view
     */
    public TweetRecycleViewAdapter getAdapter() {
        return adapter;
    }
}
