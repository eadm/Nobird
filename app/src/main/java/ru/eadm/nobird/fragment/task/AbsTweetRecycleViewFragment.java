package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import ru.eadm.nobird.fragment.state.AbsTweetRecycleViewState;

public abstract class AbsTweetRecycleViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout refreshLayout;
    public TweetRecycleViewAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_feed, container, false);
        refreshLayout.setOnRefreshListener(this);

        final RecyclerView recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.fragment_feed_recycle_view);

        if (savedInstanceState == null) {
            adapter = new TweetRecycleViewAdapter();
            getState().setTask(createTask());
            getState().getTask().execute();
        } else {
            adapter = new TweetRecycleViewAdapter(getState().getData());

            if (getState().getTask() != null) {
                getState().getTask().attachFragment(this);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return refreshLayout;
    }

    protected abstract AbsTweetRecycleViewRefreshTask createTask();
    protected abstract AbsTweetRecycleViewState getState();

    public void setRefreshing(final boolean state) {
        refreshLayout.post(new Runnable() {
            @Override public void run() { refreshLayout.setRefreshing(state); }
        });
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        getState().setData(adapter.getData());
    }

}
