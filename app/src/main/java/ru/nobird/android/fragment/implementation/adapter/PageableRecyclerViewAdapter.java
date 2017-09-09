package ru.nobird.android.fragment.implementation.adapter;

import android.support.v7.widget.RecyclerView;

import ru.nobird.android.data.PageableArrayList;
import ru.nobird.android.data.types.Element;

/**
 * Adapter with pages support
 */
public abstract class PageableRecyclerViewAdapter<E extends Element, VH extends RecyclerView.ViewHolder> extends AbsElementRecyclerViewAdapter<E, VH> {
    public PageableRecyclerViewAdapter() {
        super(new PageableArrayList<E>());
    }

    @Override
    public PageableArrayList<E> getData() {
        return (PageableArrayList<E>) data;
    }
}
