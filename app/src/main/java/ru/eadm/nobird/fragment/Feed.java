package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.MemoryMgr;
import ru.eadm.nobird.fragment.adapter.FeedRecycleViewAdapter;

public final class Feed extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG = "feed_fragment";
    private static final String DATA_TAG = "feed_data";

    private SwipeRefreshLayout page;
    private FeedRecycleViewAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        page = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_feed, container, false);
        final RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.fragment_feed_recycle_view);

        if (savedInstanceState != null) {
            adapter = new FeedRecycleViewAdapter((ArrayList<String>)MemoryMgr.getInstance().pop(savedInstanceState.getInt(DATA_TAG)));
        } else {
            adapter = new FeedRecycleViewAdapter();
            for (int i = 0; i < 1000; i++) adapter.add("Item: " + i);
        }

        page.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        return page;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(DATA_TAG, MemoryMgr.getInstance().add(adapter.getData()));
    }

    @Override
    public void onRefresh() {
        // do some refresh
        page.setRefreshing(false); // stop refresh animation
    }
}
