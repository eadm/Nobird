package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.eadm.nobird.R;
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
        holder.textView.setText(data.get(position).text);
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
        public TextView textView;
        public ViewHolder(final View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.fragment_feed_list_item_text);
        }
    }

}
