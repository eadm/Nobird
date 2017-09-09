package ru.nobird.android.fragment.implementation.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ru.nobird.android.R;
import ru.nobird.android.data.ImageMgr;
import ru.nobird.android.data.types.TweetElement;
import ru.nobird.android.databinding.FragmentFeedListItemBinding;
import ru.nobird.android.fragment.implementation.listener.ImageOnClickListener;

public class TweetRecycleViewAdapter extends AbsElementRecyclerViewAdapter<TweetElement, TweetRecycleViewAdapter.ViewHolder> {
    private final LinearLayout.LayoutParams imageLayoutParams;

    public TweetRecycleViewAdapter() {
        super();
        this.imageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        this.imageLayoutParams.setMargins(1, 0, 1, 0);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feed_list_item, parent, false));
    }

    public ImageView createImageView(final String image, final Context context) {
        final ImageView view = new ImageView(context);
        view.setLayoutParams(imageLayoutParams);
        view.setAdjustViewBounds(true);
        view.setCropToPadding(true);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageMgr.displayImage(view, image, false);
        return view;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int p) {
        if (!data.get(p).images.isEmpty()) {
            holder.binding.tweetElementAttachments.removeAllViews();
            for (int i = 0; i < data.get(p).images.size(); ++i) { // adding new image to list of attachments and set click listener
                final String image = data.get(p).images.get(i);
                final View view = createImageView(image, holder.binding.getRoot().getContext());
                view.setOnClickListener(new ImageOnClickListener(data.get(p).images, i));
                view.setClickable(true);
                holder.binding.tweetElementAttachments.addView(view);
            }
            holder.binding.tweetElementAttachments.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tweetElementAttachments.setVisibility(View.GONE);
        }

        holder.binding.tweetElementActions.setVisibility(data.get(p).status == null ? View.GONE : View.VISIBLE);
        holder.binding.tweetElementInReplyTo.setVisibility(
                (data.get(p).status == null || data.get(p).status.getInReplyToStatusId() == -1) ? View.GONE : View.VISIBLE);

        holder.binding.setTweet(data.get(p));
        holder.binding.setAdapter(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final FragmentFeedListItemBinding binding;

        public ViewHolder(final View v) {
            super(v);
            binding = DataBindingUtil.bind(v);
        }
    }

}
