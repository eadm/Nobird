package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
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
        holder.name.setText(data.get(position).name);
        holder.username.setText(data.get(position).username);
        holder.text.setText(data.get(position).text);
        holder.date.setText(Util.dateDifference(data.get(position).date));
        ImageMgr.getInstance().displayImage(data.get(position).user_image, holder.user_image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(final TweetElement s) {
        data.add(s);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, username, text, date;
        public ImageView user_image;

        public ViewHolder(final View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.tweet_element_name);

            username = (TextView)v.findViewById(R.id.tweet_element_username);
            username.setTypeface(FontMgr.getInstance().RobotoLight);

            text = (TextView)v.findViewById(R.id.tweet_element_text);
            text.setTypeface(FontMgr.getInstance().RobotoSlabLight);

            date = (TextView)v.findViewById(R.id.tweet_element_date);
            date.setTypeface(FontMgr.getInstance().RobotoLight);


            user_image = (ImageView)v.findViewById(R.id.tweet_element_user_image);
        }
    }

}
