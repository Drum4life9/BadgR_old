package com.badgr.scoutPagesAndClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ScoutFrags.MyListFragment;
import Fragments.ScoutFrags.WelcomeFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {


    private static final String[] titles = new String[]{"Welcome", "My Badge List"};

    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new WelcomeFragment();
            case 1:
                return new MyListFragment();
        }
        return new WelcomeFragment();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return titles.length;
    }

    public static String[] getTitles() {
        return titles;
    }

}
