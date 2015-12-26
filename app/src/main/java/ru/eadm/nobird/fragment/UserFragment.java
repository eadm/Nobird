package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Date;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;

public class UserFragment extends Fragment {
    private TextView name, username;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View page = inflater.inflate(R.layout.fragment_user, container, false);

        final RecyclerView recyclerView = (RecyclerView)page.findViewById(R.id.fragment_user_actions);
        final TweetRecycleViewAdapter adapter = new TweetRecycleViewAdapter();
        adapter.add(new TweetElement(0, 0, "TEST", "test", "", new TwitterStatusText("1111"), new Date(), ""));
        adapter.add(new TweetElement(0, 0, "TEST", "test", "", new TwitterStatusText("1111"), new Date(), ""));
        adapter.add(new TweetElement(0, 0, "TEST", "test", "", new TwitterStatusText("1111"), new Date(), ""));
        adapter.add(new TweetElement(0, 0, "TEST", "test", "", new TwitterStatusText("1111"), new Date(), ""));
        recyclerView.setAdapter(new TweetRecycleViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageMgr.getInstance().displayRoundImage(getArguments().getString("image"), (ImageView) page.findViewById(R.id.fragment_user_image));

        name = (TextView) page.findViewById(R.id.fragment_user_name);
        name.setTypeface(FontMgr.getInstance().RobotoLight);
        name.setText(getArguments().getString("name"));

        username = (TextView) page.findViewById(R.id.fragment_user_username);
        username.setTypeface(FontMgr.getInstance().RobotoLight);
        username.setText(String.format(getString(R.string.username_placeholder), getArguments().getString("username")));

        Log.d(TAG, "user: " + getArguments().getString("username"));
        return page;
    }

    private static final String TAG = "UserFragment";

    public static void showUser(final UserElement user) {
        Log.d(TAG, user.username);

        final Fragment fragment = new UserFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("name", user.name);
        bundle.putString("username", user.username);
        bundle.putString("image", user.image);
        bundle.putLong("userID", user.userID);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }
}
