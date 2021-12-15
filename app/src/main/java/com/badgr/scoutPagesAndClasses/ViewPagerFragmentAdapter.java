package com.badgr.scoutPagesAndClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ScoutFrags.CompletedBadges;
import Fragments.ScoutFrags.EmailSM;
import Fragments.ScoutFrags.MyListDrivers.MyListFragment;
import Fragments.ScoutFrags.SearchFragmentDrivers.SearchBadges;
import Fragments.ScoutFrags.WelcomeFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    //List of titles for tabs
    private static final String[] titles = new String[]{"Welcome", "My Badge List", "Completed Badges", "Search for Badge", "Email Scoutmaster"};

    //creates the viewPager, which allows the fragments to be created and destroyed when swiping
    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    //creates the fragment based on the tab position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new WelcomeFragment();
            case 1:
                return new MyListFragment();
            case 2:
                return new CompletedBadges();
            case 3:
                return new SearchBadges();
            case 4:
                return new EmailSM();
        }
        return new WelcomeFragment();
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
