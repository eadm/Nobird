package ru.eadm.nobird.fragment.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.databinding.FragmentFeedListItemBinding;

public class TweetRecycleViewAdapter extends AbsRecycleViewAdapter<TweetElement, TweetRecycleViewAdapter.ViewHolder> {
    public TweetRecycleViewAdapter() {
        super();
    }

    public TweetRecycleViewAdapter(final PageableArrayList<TweetElement> data) {
        super(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feed_list_item, parent, false);

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        bindViewHolder(holder, data.get(position));
    }

    public static void bindViewHolder(final ViewHolder holder, final TweetElement element) {
        holder.binding.setTweet(element);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FragmentFeedListItemBinding binding;

        public ViewHolder(final View v) {
            super(v);
            binding = DataBindingUtil.bind(v);
        }
    }

}
