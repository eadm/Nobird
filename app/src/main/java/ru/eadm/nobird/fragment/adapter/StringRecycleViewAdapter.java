package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.StringElement;

/**
 * Adapter to display strings
 */
public class StringRecycleViewAdapter extends AbsElementRecyclerViewAdapter<StringElement, StringRecycleViewAdapter.ViewHolder> {
    private final OnDataClickListener<StringElement> onDataClickListener;

    public StringRecycleViewAdapter(final OnDataClickListener<StringElement> onDataClickListener) {
        super();
        this.onDataClickListener = onDataClickListener;
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
            onDataClickListener.onClick(data.get(getAdapterPosition()));
        }
    }
}
