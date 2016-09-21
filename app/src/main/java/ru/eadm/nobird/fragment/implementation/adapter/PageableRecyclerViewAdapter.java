package ru.eadm.nobird.fragment.implementation.adapter;

import android.support.v7.widget.RecyclerView;

import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.Element;

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
