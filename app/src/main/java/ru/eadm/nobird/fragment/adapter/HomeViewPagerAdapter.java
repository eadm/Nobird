package ru.eadm.nobird.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base view pager adapter
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter{
    private final List<Fragment> fragments;
    private final List<String> titles;

    public HomeViewPagerAdapter(final FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void add(final Fragment fragment, final String title) {
        fragments.add(fragment);
        titles.add(title);
    }
}
