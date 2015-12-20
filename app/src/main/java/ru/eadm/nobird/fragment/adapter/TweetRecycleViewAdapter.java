package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.Util;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.adapter.listener.ImageClickListener;
import ru.eadm.nobird.fragment.adapter.listener.TweetItemClickListener;
import ru.eadm.nobird.fragment.adapter.listener.UserClickListener;

public class TweetRecycleViewAdapter extends RecyclerView.Adapter<TweetRecycleViewAdapter.ViewHolder> {
    private final ArrayList<TweetElement> data;
    public TweetRecycleViewAdapter() {
        this(null);
    }

    public TweetRecycleViewAdapter(final ArrayList<TweetElement> data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data = new ArrayList<>();
        }
    }

    public ArrayList<TweetElement> getData() {
        return data;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feed_list_item, parent, false);

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final UserClickListener userClickListener = new UserClickListener(data.get(position).user);
        holder.name.setText(data.get(position).user.name);
        holder.name.setOnClickListener(userClickListener);

        holder.username.setText("@" + data.get(position).user.username);
        holder.username.setOnClickListener(userClickListener);

        ImageMgr.getInstance().displayRoundImage(data.get(position).user.image, holder.user_image);
        holder.user_image.setOnClickListener(userClickListener);

        final TweetItemClickListener tweetItemClickListener = new TweetItemClickListener(data.get(position));

        holder.text.setText(data.get(position).text);
        holder.text.setOnClickListener(tweetItemClickListener);
        holder.date.setText(Util.dateDifference(data.get(position).date));
        holder.date.setOnClickListener(tweetItemClickListener);

        if (data.get(position).image != null) {
            holder.attachment.setOnClickListener(new ImageClickListener(data.get(position).image));
            ImageMgr.getInstance().displayImage(data.get(position).image, holder.attachment);
        }

        holder.page.setOnClickListener(tweetItemClickListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(final TweetElement s) {
        data.add(s);
    }
    public void addAll(final ArrayList<TweetElement> elements) {
        data.addAll(elements);
    }

    public void add(final int index, final TweetElement element) {
        data.add(index, element);
    }

    public void addAll(final int index, final ArrayList<TweetElement> elements) {
        data.addAll(index, elements);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, username, text, date;
        public ImageView user_image, attachment;
        public View page;

        public ViewHolder(final View v) {
            super(v);
            page = v;

            name = (TextView)v.findViewById(R.id.tweet_element_name);

            username = (TextView)v.findViewById(R.id.tweet_element_username);
            username.setTypeface(FontMgr.getInstance().RobotoLight);

            text = (TextView)v.findViewById(R.id.tweet_element_text);
            text.setTypeface(FontMgr.getInstance().RobotoSlabLight);
            text.setMovementMethod(LinkMovementMethod.getInstance());

            date = (TextView)v.findViewById(R.id.tweet_element_date);
            date.setTypeface(FontMgr.getInstance().RobotoLight);


            user_image = (ImageView)v.findViewById(R.id.tweet_element_user_image);
            attachment = (ImageView)v.findViewById(R.id.tweet_element_attachment);
        }
    }

}
