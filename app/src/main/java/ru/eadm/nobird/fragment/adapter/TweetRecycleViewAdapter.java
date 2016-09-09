package ru.eadm.nobird.fragment.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
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
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feed_list_item, parent, false));
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int p) {
        if (data.get(p).image != null && data.get(p).image.length() > 0) {
            ImageMgr.getInstance().displayImage(data.get(p).image, holder.binding.tweetElementAttachment);
            holder.binding.tweetElementAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tweetElementAttachment.setVisibility(View.GONE);
        }

        holder.binding.setTweet(data.get(p));
        holder.binding.setAdapter(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FragmentFeedListItemBinding binding;

        public ViewHolder(final View v) {
            super(v);
            binding = DataBindingUtil.bind(v);
        }
    }

}
