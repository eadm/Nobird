package ru.eadm.nobird.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.adapter.listener.UserClickListener;

/**
 * Created by ruslandavletshin on 05/01/16.
 */
public class UserRecycleViewAdapter extends AbsRecycleViewAdapter<UserElement, UserRecycleViewAdapter.ViewHolder> {

    public UserRecycleViewAdapter() { super(); }
    public UserRecycleViewAdapter(final PageableArrayList<UserElement> data) { super(data); }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserClickListener userClickListener = new UserClickListener(data.get(position));

        holder.name.setText(data.get(position).name);
        holder.username.setText("@" + data.get(position).username);
        ImageMgr.getInstance().displayRoundImage(data.get(position).image, holder.user_image);

        holder.page.setOnClickListener(userClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, username;
        public ImageView user_image;
        public View page;

        public ViewHolder(final View v) {
            super(v);
            page = v;

            name = (TextView)v.findViewById(R.id.list_item_user_name);
            name.setTypeface(FontMgr.getInstance().RobotoLight);

            username = (TextView)v.findViewById(R.id.list_item_user_username);
            username.setTypeface(FontMgr.getInstance().RobotoLight);

            user_image = (ImageView)v.findViewById(R.id.list_item_user_image);
        }
    }
}
