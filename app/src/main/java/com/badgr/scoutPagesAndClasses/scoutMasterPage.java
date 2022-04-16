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
import com.badgr.scoutClasses.scoutMaster;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class scoutMasterPage extends AppCompatActivity {

    ScoutMasterFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private final String[] titles = ScoutMasterFragmentAdapter.getTitles();

    private final scoutMaster user = (scoutMaster) LoginRepository.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoutmaster_tab);


        //sets viewPager (a.k.a tab scroller), tabLayout (houses the tabs at the top of screen), and fragmentAdapter (creates new fragments when scrolled)
        viewPager2 = findViewById(R.id.SMview_pager);
        tabLayout = findViewById(R.id.SMtab_layout);
        viewPagerFragmentAdapter = new ScoutMasterFragmentAdapter(this);
        Activity a = this;

        //sets the bottom part of the screen to whichever fragment is active
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


    }

    //sets welcome message to user's name
    public void setUserText() {
        TextView welcome = findViewById(R.id.SMwelcomeScout);
        String welcomeS = "Welcome " + user.getFName() + " " + user.getLName() + "!";
        welcome.setText(welcomeS);
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
