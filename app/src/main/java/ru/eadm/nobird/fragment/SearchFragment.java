package ru.eadm.nobird.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.regex.Pattern;

import ru.eadm.nobird.R;
import ru.eadm.nobird.Util;
import ru.eadm.nobird.broadcast.BroadcastReceiver;
import ru.eadm.nobird.data.types.StringElement;
import ru.eadm.nobird.databinding.FragmentSearchBinding;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.design.animation.OnEndAnimationListener;
import ru.eadm.nobird.design.animation.OnStartAnimationListener;
import ru.eadm.nobird.fragment.adapter.StringRecycleViewAdapter;

/**
 * Fragment with search quires
 */
public class SearchFragment extends Fragment implements View.OnClickListener /*, BroadcastReceiver<StringElement>*/ {
    private FragmentSearchBinding binding;
    private String query;
    private Pattern usernamePattern;

    private StringRecycleViewAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle state) {
        super.onCreateView(inflater, container, state);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.fragmentSearchToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.fragmentSearchQuery.setText(query);
        binding.fragmentSearchQuery.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) { setQuery(s.toString()); }
        });
        setQuery(query);
        binding.fragmentSearchActionGoToUser.setOnClickListener(this);
        binding.fragmentSearchActionStatuses.setOnClickListener(this);
        binding.fragmentSearchActionUsers.setOnClickListener(this);

        binding.fragmentSearchSavedQueries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.fragmentSearchSavedQueries.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));
        binding.fragmentSearchSavedQueries.setAdapter(adapter);

        if (state == null) {
            binding.fragmentSearchQuery.requestFocus();
            Util.openKeyboard(getContext(), binding.fragmentSearchQuery);
        }

        return binding.getRoot();
    }

    private void setQuery(final String newQuery) {
        if (newQuery == null) return;
        this.query = newQuery.trim();
        if (query.length() > 0) {
            binding.fragmentSearchActionStatuses.setText(String.format(getString(R.string.search_action_statuses), query));
            binding.fragmentSearchActionUsers.setText(String.format(getString(R.string.search_action_users), query));
            binding.fragmentSearchActionGoToUser.setText(String.format(getString(R.string.search_action_go_to_user), query));

            if (usernamePattern.matcher(query).matches()) {
                binding.fragmentSearchActionGoToUser.setVisibility(View.VISIBLE);
            } else {
                binding.fragmentSearchActionGoToUser.setVisibility(View.GONE);
            }

            if (binding.fragmentSearchActions.getVisibility() == View.GONE) {
                final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_start);
                animation.setAnimationListener(new OnStartAnimationListener(binding.fragmentSearchActions));
                binding.fragmentSearchActions.startAnimation(animation);
            }
        } else {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_end);
            animation.setAnimationListener(new OnEndAnimationListener(binding.fragmentSearchActions));
            binding.fragmentSearchActions.startAnimation(animation);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(SearchResultFragment.ARG_QUERY);
        }
        usernamePattern = Pattern.compile("[A-Za-z0-9_]+");
        adapter = new StringRecycleViewAdapter(new StringRecycleViewAdapter.OnDataClickListener<StringElement>() {
            @Override
            public void onClick(final StringElement data) {
                SearchResultFragment.show(data.getText());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        usernamePattern = null;
        query = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Util.closeKeyboard(getContext(), binding.fragmentSearchQuery.getWindowToken());
        binding.unbind();
        binding = null;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fragment_search_action_go_to_user:
                UserFragment.showUser(query);
            break;
            case R.id.fragment_search_action_statuses:
                SearchResultFragment.show(query);
            break;
            case R.id.fragment_search_action_users:
                SearchUserResultsFragment.show(query);
            break;
        }
    }

    public static void show() {
        FragmentMgr.getInstance().replaceFragment(0, new SearchFragment(), true);
    }

//    @Override
//    public void notifyItemRemoved(long id) {
////        adapter.remove();
//    }


}
