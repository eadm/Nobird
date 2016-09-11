package ru.eadm.nobird.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.eadm.nobird.R;
import ru.eadm.nobird.fragment.adapter.HomeViewPagerAdapter;

/**
 * Image gallery fragment
 */
public class ImageGallery extends Fragment {
    public final static String ARG_IMAGES = "arg_images";
    public final static String ARG_POSITION = "arg_pos";
    private HomeViewPagerAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final ViewPager viewPager = (ViewPager) inflater.inflate(R.layout.base_view_pager, container, false);
        viewPager.setAdapter(adapter);
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(getArguments().getInt(ARG_POSITION));
        }
        return viewPager;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        adapter = new HomeViewPagerAdapter(getChildFragmentManager());
        for (final String image : getArguments().getStringArrayList(ARG_IMAGES)) {
            adapter.add(ImagePreview.createImagePreview(image), null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
    }

    public static void show(final ArrayList<String> images, final int pos) {
        if (images == null || images.isEmpty() || images.size() <= pos) return;

        final Fragment fragment = new ImageGallery();
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList(ARG_IMAGES, images);
        bundle.putInt(ARG_POSITION, pos);
        fragment.setArguments(bundle);
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }
}
