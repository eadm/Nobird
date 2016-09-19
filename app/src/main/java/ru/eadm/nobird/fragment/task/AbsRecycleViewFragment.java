package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.Element;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.AbsRecycleViewAdapter;
import ru.eadm.nobird.fragment.listener.RecycleViewOnScrollListener;
import ru.eadm.nobird.fragment.listener.Scrollable;
import twitter4j.CursorSupport;

public abstract class AbsRecycleViewFragment<E extends Element> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Scrollable {
    protected AbsRecycleViewAdapter<E, ?> adapter;
    protected AbsRecycleViewRefreshTask<E> task;

    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.fragment_list, container, false);
        refreshLayout = (SwipeRefreshLayout) page.findViewById(R.id.fragment_list_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        final RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.fragment_list_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addOnScrollListener(new RecycleViewOnScrollListener(this));
        recyclerView.setAdapter(adapter);

        if (task == null) {
            task = createTask(AbsRecycleViewRefreshTask.Position.END);
            task.execute(CursorSupport.START, 0L);
        }

        final Toolbar toolbar = (Toolbar) page.findViewById(R.id.fragment_list_toolbar);
        toolbar.setTitle(getToolbarTitle());

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return page;
    }

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
        if (task != null &&
                task.getState() == TaskState.COMPLETED &&
                adapter.getData().hasPrevious()) {
            task = createTask(AbsRecycleViewRefreshTask.Position.START);
            task.execute(adapter.getData().getPreviousCursor(), 0L);
        } else {
            setRefreshing(false);
        }
    }

    @Override
    public void onScrolledToEnd() {
        if (task != null &&
                task.getState() == TaskState.COMPLETED &&
                adapter.getData().hasNext()) {
            task = createTask(AbsRecycleViewRefreshTask.Position.END);
            task.execute(0L, adapter.getData().getNextCursor());
        }
    }

    protected abstract AbsRecycleViewRefreshTask<E> createTask(final AbsRecycleViewRefreshTask.Position position);
    protected abstract AbsRecycleViewAdapter<E, ?> createAdapter();

    protected abstract String getToolbarTitle();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        adapter = createAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        task.cancel(false);
        task = null;
        adapter = null;
    }
}
