package ru.eadm.nobird.fragment.implementation.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.eadm.nobird.data.ImageMgr;

/**
 * Default onScroll listener for recycle view
 */
public class RecycleViewOnScrollListener extends RecyclerView.OnScrollListener {
    private final Scrollable scrollable;

    public RecycleViewOnScrollListener(final Scrollable scrollable) {
        this.scrollable = scrollable;
    }

    boolean scrolledToEnd = false;

    @Override
    public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy <= 0) return; // ignore if you scrolls up
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager.getItemCount() - recyclerView.getChildCount()
                <= layoutManager.findFirstVisibleItemPosition()) {
            if (!scrolledToEnd) scrollable.onScrolledToEnd();
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
}
