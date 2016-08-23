package ru.eadm.nobird.fragment.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.Element;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.AbsRecycleViewAdapter;
import twitter4j.CursorSupport;

public abstract class AbsRecycleViewFragment<E extends Element> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    protected AbsRecycleViewAdapter<E, ?> adapter;
    protected PageableArrayList<E> data;
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
            }
        });

        if (data == null) {
            task = createTask(AbsRecycleViewRefreshTask.Position.END);
            task.execute(CursorSupport.START, 0L);
        }
        adapter = createAdapter(data);
        data = adapter.getData();
        recyclerView.setAdapter(adapter);

        final Toolbar toolbar = (Toolbar) page.findViewById(R.id.fragment_list_toolbar);
        toolbar.setTitle(getToolbarTitleID());
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
                refreshLayout.setRefreshing(state);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (task != null &&
                task.getState() == TaskState.COMPLETED &&
                data.hasPrevious()) {
            task = createTask(AbsRecycleViewRefreshTask.Position.START);
            task.execute(data.getPreviousCursor(), 0L);
        } else {
            setRefreshing(false);
        }
    }

    private void onScrolledToEnd() {
        if (task != null &&
                task.getState() == TaskState.COMPLETED &&
                data.hasNext()) {
            task = createTask(AbsRecycleViewRefreshTask.Position.END);
            task.execute(0L, data.getNextCursor());
        }
    }

    protected abstract AbsRecycleViewRefreshTask<E> createTask(final AbsRecycleViewRefreshTask.Position position);
    protected abstract AbsRecycleViewAdapter<E, ?> createAdapter(final PageableArrayList<E> data);

    protected abstract int getToolbarTitleID();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout = null;
        adapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        data = null;
        task.cancel(false);
        task = null;
    }

    @Override
    public abstract void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater);

}
