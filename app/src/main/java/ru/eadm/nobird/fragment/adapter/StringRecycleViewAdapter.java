package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.DraftElement;

/**
 * Adapter to display strings
 */
public class StringRecycleViewAdapter extends RecyclerView.Adapter<StringRecycleViewAdapter.ViewHolder> {
    private final List<DraftElement> data;
    private final OnDataClickListener<String> onDataClickListener;

    public StringRecycleViewAdapter(final OnDataClickListener<String> onDataClickListener) {
        this.data = new ArrayList<>();
        this.onDataClickListener = onDataClickListener;
    }

    public void addAll(final List<DraftElement> data) {
        final int start = getItemCount();
        this.data.addAll(data);
        notifyItemRangeInserted(start, data.size());
    }

    public void remove(final int pos) {
        this.data.remove(pos);
        notifyItemRemoved(pos);
    }

    public DraftElement get(final int pos) {
        return data.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_fragment_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textView.setText(data.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnDataClickListener<D> {
        void onClick(final D data);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;

        public ViewHolder(final View textView) {
            super(textView);
            this.textView = (TextView) textView;
            this.textView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            onDataClickListener.onClick(textView.getText().toString());
        }
    }
}
