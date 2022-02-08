package com.badgr.scoutPagesAndClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ScoutFrags.SCompletedBadges;
import Fragments.ScoutFrags.SMyListFragment;
import Fragments.ScoutFrags.SResource;
import Fragments.ScoutFrags.SSearchBadges;
import Fragments.ScoutFrags.SWelcomeFragment;

public class ScoutFragmentAdapter extends FragmentStateAdapter {

    //List of titles for tabs
    private static final String[] titles = new String[]{"Welcome", "Search", "My Badge List", "Completed Badges", "Scouts BSA Resources"};

    //creates the viewPager, which allows the fragments to be created and destroyed when swiping
    public ScoutFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    //creates the fragment based on the tab position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SWelcomeFragment();
            case 1:
                return new SSearchBadges();
            case 2:
                return new SMyListFragment();
            case 3:
                return new SCompletedBadges();
            case 4:
                return new SResource();
        }
        return new SWelcomeFragment();
    }

    //returns total number of available tabs
    @Override
    public int getItemCount() {
        return titles.length;
    }

    //returns the titles of the tabs
    public static String[] getTitles() {
        return titles;
    }


}
