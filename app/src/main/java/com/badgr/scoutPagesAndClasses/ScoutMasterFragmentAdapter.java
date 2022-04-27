package com.badgr.scoutPagesAndClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.SMFrags.SMChatFragment;
import Fragments.SMFrags.SMWelcomeFragment;
import Fragments.SMFrags.SMMyScoutsFragment;
import Fragments.SMFrags.SMRecentFragment;
import Fragments.SMFrags.SMSearchBadgesFragment;


public class ScoutMasterFragmentAdapter extends FragmentStateAdapter {


    private static final String[] titles = new String[]{"Troop Info", "Recent changes", "My Scouts", "Search for Merit Badges", "Chat"};

    //creates the viewPager, which allows the fragments to be created and destroyed when swiping
    public ScoutMasterFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    //creates the fragment based on the tab position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SMWelcomeFragment();
            case 1:
                return new SMRecentFragment();
            case 2:
                return new SMMyScoutsFragment();
            case 3:
                return new SMSearchBadgesFragment();
            case 4:
                return new SMChatFragment();
        }
        return new SMWelcomeFragment();
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
