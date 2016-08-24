package ru.eadm.nobird.fragment.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.databinding.ListItemUserBinding;

public class UserRecycleViewAdapter extends AbsRecycleViewAdapter<UserElement, UserRecycleViewAdapter.ViewHolder> {

    public UserRecycleViewAdapter() { super(); }
    public UserRecycleViewAdapter(final PageableArrayList<UserElement> data) { super(data); }

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
