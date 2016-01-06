package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.eadm.nobird.R;

public final class CreateStatusFragment extends Fragment {
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.fragment_create_status, container, false);

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        final Toolbar toolbar = (Toolbar) page.findViewById(R.id.fragment_create_status_toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return page;
    }

    public static void open() {
        FragmentMgr.getInstance().replaceFragment(0, new CreateStatusFragment(), true);
    }
}
