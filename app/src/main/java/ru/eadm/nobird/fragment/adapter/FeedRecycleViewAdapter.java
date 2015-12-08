package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.eadm.nobird.R;

public class FeedRecycleViewAdapter extends RecyclerView.Adapter<FeedRecycleViewAdapter.ViewHolder> {
    private final ArrayList<String> data;
    public FeedRecycleViewAdapter() {
        this(null);
    }

    public FeedRecycleViewAdapter(final ArrayList<String> data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data = new ArrayList<>();
        }
    }

    public ArrayList<String> getData() {
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
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(final String s) {
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
