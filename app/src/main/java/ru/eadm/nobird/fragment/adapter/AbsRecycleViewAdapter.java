package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.Element;


public abstract class AbsRecycleViewAdapter<E extends Element, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected final PageableArrayList<E> data;

    public AbsRecycleViewAdapter() {
        this(null);
    }

    public AbsRecycleViewAdapter(final PageableArrayList<E> data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data = new PageableArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public PageableArrayList<E> getData() {
        return data;
    }

    public void addAll(final ArrayList<E> elements) {
        data.addAll(elements);
    }
    public void addAll(final int index, final ArrayList<E> elements) {
        data.addAll(index, elements);
    }

    public void add(final E e) {
        data.add(e);
    }

    public void add(final int index, final E e) {
        data.add(index, e);
    }
    public void set(final int index, final E e) {
        data.set(index, e);
    }

    public E get(final int pos) {
        return data.get(pos);
    }

    /**
     * Removes element by its element id
     * @param id - id of element
     */
    public void removeByElementID(final long id) {
        int i = 0;
        for (; i < data.size(); ++i) if (data.get(i).getID() == id) break; // find pos of target
        if (i != data.size()) {
            data.remove(i);
            notifyItemRemoved(i);
        }
    }
}
