package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;

public abstract class AbsTweetRecycleViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = this.getClass().getName();

    public static final int POSITION_START = 0;
    public static final int POSITION_END = 1;

    public SwipeRefreshLayout refreshLayout;
    public TweetRecycleViewAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_feed, container, false);
        refreshLayout.setOnRefreshListener(this);

        final RecyclerView recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.fragment_feed_recycle_view);


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
                final LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                if (layoutManager.getItemCount() - recyclerView.getChildCount()
                        <= layoutManager.findFirstVisibleItemPosition()) {
                    onScrolledToEnd();
                }
            }
        });


        return refreshLayout;
    }

    @Override
    public void onRefresh() {
        if (getState().getTask() != null
                && getState().getTask().getState() == AbsTweetRecycleViewRefreshTask.TaskState.COMPLETED) {
            getState().setTask(createTask(POSITION_START, AbsTweetRecycleViewRefreshTask.Source.API));
            getState().getTask().execute((adapter.getItemCount() == 0) ? 0 : adapter.getData().get(0).tweetID, 0L);

            Log.d(TAG, "onRefresh");
        }
    }

    public void onScrolledToEnd() {
        if (getState().getTask() != null
                && getState().getTask().getState() == AbsTweetRecycleViewRefreshTask.TaskState.COMPLETED) {
            getState().setTask(createTask(POSITION_END, AbsTweetRecycleViewRefreshTask.Source.API));
            getState().getTask().execute(0L, adapter.getData().get(adapter.getData().size() - 1).tweetID - 1);

            Log.d(TAG, "onScrolledToEnd");
        }
    }

    protected abstract AbsTweetRecycleViewRefreshTask createTask(final int position, final AbsTweetRecycleViewRefreshTask.Source source);
    protected abstract AbsTweetRecycleViewState getState();

    public void setRefreshing(final boolean state) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(state);
            }
        });
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        if (adapter != null) { // cause sometimes adapter = null, during screen rotation
            getState().setData(adapter.getData());
        }
    }
}
