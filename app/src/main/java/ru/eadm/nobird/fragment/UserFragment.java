package ru.eadm.nobird.fragment;

import android.os.AsyncTask;
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


import java.lang.ref.WeakReference;
import java.util.Date;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.FontMgr;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.data.twitter.utils.TwitterStatusText;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.data.types.UserElement;
import ru.eadm.nobird.fragment.adapter.TweetRecycleViewAdapter;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserFragment extends Fragment {
    private TextView name, username;
    private ImageView background;
    private User user;

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

        background = (ImageView) page.findViewById(R.id.fragment_user_background);

        if (savedInstanceState == null) {
            new UserLoaderTask(this).execute(getArguments().getLong("userID"));
        } else {
            setUser((User) savedInstanceState.getSerializable("user"));
        }

        return page;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (user != null) {
            outState.putSerializable("user", user);
        }
    }

    private void setUser(final User user) {
        if (user == null) return;
        this.user = user;
        ImageMgr.getInstance().displayImage(user.getProfileBannerIPadRetinaURL(), background);
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

    private final class UserLoaderTask extends AsyncTask<Long, Void, User> {
        private WeakReference<UserFragment> fragmentWeakReference;
        private UserLoaderTask(final UserFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected User doInBackground(Long... params) {
            try {
                return TwitterMgr.getInstance().getUser(params[0]);
            } catch (TwitterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final User user) {
            if (fragmentWeakReference.get() != null) {
                if (user != null) {
                    fragmentWeakReference.get().setUser(user);
                } else {
                    Log.d(TAG, "user lost");
                }
            } else {
                Log.d(TAG, "fragment lost");
            }
        }
    }
}
