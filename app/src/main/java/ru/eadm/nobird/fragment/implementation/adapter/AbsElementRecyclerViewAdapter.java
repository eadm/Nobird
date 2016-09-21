package ru.eadm.nobird.fragment.implementation.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.eadm.nobird.data.types.Element;

/**
 * Abstract adapter for recycler view with Elements
 */
public abstract class AbsElementRecyclerViewAdapter<E extends Element, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected final List<E> data;

    public AbsElementRecyclerViewAdapter() {
        this(null);
    }

    public AbsElementRecyclerViewAdapter(final List<E> data) {
        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
    }

    /**
     * Search and return index of element with targeted id
     * @param targetID - id of target element
     * @return index of element if element exists -1 otherwise
     */
    public int lookup(final long targetID) {
        for (int i = 0; i < data.size(); ++i) if (data.get(i).getID() == targetID) return i;
        return -1;
    }

    /**
     * Check if element with given id exists in data set
     * @param id - id of element
     * @return true if exists false otherwise
     */
    public boolean exists(final long id) {
        return lookup(id) != -1;
    }

    /**
     * Add element
     * @param e - element
     */
    public void add(final E e) {
        data.add(e);
        notifyItemInserted(data.size() - 1);
    }
    public void add(final int pos, final E e) {
        data.add(pos, e);
        notifyItemInserted(pos);
    }

    public void addAll(final List<E> elements) {
        final int start = data.size();
        data.addAll(elements);
        notifyItemRangeInserted(start, elements.size());
    }
    public void addAll(final int pos, final List<E> elements) {
        data.addAll(pos, elements);
        notifyItemRangeInserted(pos, elements.size());
    }

    public void set(final int pos, final E e) {
        data.set(pos, e);
        notifyItemChanged(pos);
    }

    public void remove(final int pos) {
        data.remove(pos);
        notifyItemRemoved(pos);
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public E get(final int pos) {
        return data.get(pos);
    }

    public List<E> getData() { return data; }

    /**
     * Removes element by its element id
     * @param id - id of element
     */
    public void removeByElementID(final long id) {
        final int pos = lookup(id);
        if (pos != -1) remove(pos);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
