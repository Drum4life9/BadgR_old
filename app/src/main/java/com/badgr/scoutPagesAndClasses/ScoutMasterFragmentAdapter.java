package com.badgr.scoutPagesAndClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.SMFrags.SMInfoFragment;
import Fragments.SMFrags.SMMyScoutsFragment;
import Fragments.SMFrags.SMSearchBadges;
import Fragments.ScoutFrags.SWelcomeFragment;

public class ScoutMasterFragmentAdapter extends FragmentStateAdapter {


    private static final String[] titles = new String[]{"Troop Info", "My Scouts", "Search for Merit Badges"};

    //creates the viewPager, which allows the fragments to be created and destroyed when swiping
    public ScoutMasterFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    //creates the fragment based on the tab position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new SMInfoFragment();
            case 1:
                return new SMMyScoutsFragment();
            case 2:
                return new SMSearchBadges();
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
