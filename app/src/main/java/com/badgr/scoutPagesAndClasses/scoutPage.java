package com.badgr.scoutPagesAndClasses;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.scoutPerson;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import Fragments.ScoutFrags.SCompletedBadges;
import Fragments.ScoutFrags.SMyListExpandListAdapter;
import Fragments.ScoutFrags.SMyListFragment;
import Fragments.ScoutFrags.SSearchExpandListAdapter;


public class scoutPage extends AppCompatActivity {

    ScoutFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    //gets titles for tabs and user from LoginRepo
    private final String[] titles = ScoutFragmentAdapter.getTitles();
    private final scoutPerson user = LoginRepository.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_tab);


        SMyListExpandListAdapter.pullFinishedReqs(user);

        //sets viewPager (a.k.a tab scroller), tabLayout (houses the tabs at the top of screen), and fragmentAdapter (creates new fragments when scrolled)
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPagerFragmentAdapter = new ScoutFragmentAdapter(this);
        Activity a = this;

        //sets the bottom part of the screen to whatever fragment is active
        viewPager2.setAdapter(viewPagerFragmentAdapter);

        //sync the ViewPager2 position with the selected tab when a tab is selected
        new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles[position]))).attach();


        //when tab is changed, dismiss the soft keyboard so the user cannot type in other fragments
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { hideKeyboard(a); }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                hideKeyboard(a);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { hideKeyboard(a); }
        });

        //sets welcome message
        setUserText();

        //runs some initial database connections to make future loading of lists faster
        SMyListFragment.getBadgesAdded();
        SCompletedBadges.getFinishedBadges();
        SSearchExpandListAdapter.pullAddedBadges(user);
        SSearchExpandListAdapter.pullFinishedBadges(user);

    }

    //sets welcome message to user's name
    public void setUserText() {
        TextView welcome = findViewById(R.id.welcomeScout);
        String welcomeS = "Welcome " + user.getFName() + " " + user.getLName() + "!";
        welcome.setText(welcomeS);
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            //hide soft keyboard
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}