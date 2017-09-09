package ru.nobird.android.fragment.implementation.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.nobird.android.R;
import ru.nobird.android.data.types.UserElement;
import ru.nobird.android.databinding.ListItemUserBinding;

public class UserRecycleViewAdapter extends PageableRecyclerViewAdapter<UserElement, UserRecycleViewAdapter.ViewHolder> {
    public UserRecycleViewAdapter() { super(); }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.binding.setUser(data.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ListItemUserBinding binding;

        public ViewHolder(final View v) {
            super(v);
            binding = DataBindingUtil.bind(v);
        }
    }
}
