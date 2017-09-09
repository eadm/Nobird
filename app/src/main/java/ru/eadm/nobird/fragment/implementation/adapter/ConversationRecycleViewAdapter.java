package ru.eadm.nobird.fragment.implementation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.data.types.ConversationElement;

/**
 * Adapter to display sender and last message
 */

public class ConversationRecycleViewAdapter extends PageableRecyclerViewAdapter<ConversationElement, ConversationRecycleViewAdapter.ViewHolder> {

    public ConversationRecycleViewAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(final View page) {
            super(page);
        }
    }
}
