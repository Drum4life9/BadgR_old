package com.badgr.scoutPagesAndClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.badgr.scoutClasses.scoutMaster;

import Fragments.SMFrags.SMMyScoutsFragment;
import Fragments.SMFrags.SMRecentFragment;
import Fragments.SMFrags.SMSearchBadgesFragment;
import Fragments.SMFrags.SMWelcomeFragment;


public class ScoutMasterFragmentAdapter extends FragmentStateAdapter {


    private static final String[] titles = new String[]{"Troop Info", "Recent changes", "My Scouts", "Search for Merit Badges"};
    private final scoutMaster user;

    //creates the viewPager, which allows the fragments to be created and destroyed when swiping
    public ScoutMasterFragmentAdapter(@NonNull FragmentActivity fragmentActivity, scoutMaster u) {
        super(fragmentActivity);
        user = u;
    }

    //returns the titles of the tabs
    public static String[] getTitles() {
        return titles;
    }

    //creates the fragment based on the tab position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SMWelcomeFragment(user);
            case 1:
                return new SMRecentFragment(user);
            case 2:
                return new SMMyScoutsFragment(user);
            case 3:
                return new SMSearchBadgesFragment(user);
        }
        return new SMWelcomeFragment(user);
    }

    //returns total number of available tabs
    @Override
    public int getItemCount() {
        return titles.length;
    }

}
