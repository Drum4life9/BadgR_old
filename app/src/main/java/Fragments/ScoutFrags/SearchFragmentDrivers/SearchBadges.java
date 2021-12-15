package Fragments.ScoutFrags.SearchFragmentDrivers;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;

import Fragments.ScoutFrags.MyListDrivers.MyListFragment;
import Fragments.ScoutFrags.SearchFragmentDrivers.SearchExpandListAdapter;
import Fragments.ScoutFrags.SearchFragmentDrivers.SearchListTitles;

import com.badgr.scoutPagesAndClasses.scoutPage;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SearchBadges extends Fragment {

    ExpandableListView accordionList;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<String> badgeTitles;
    ArrayList<meritBadge> badges;
    MutableLiveData<ArrayList<meritBadge>> badgesLiveData = new MutableLiveData<>();

    //runs first
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_badges, container, false);

    }

    //runs after fragment is created
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Finds and stores actionable pieces, such as the search button and the text search box
        TextView searchBar = view.findViewById(R.id.badgeSearchText);
        Button searchBut = view.findViewById(R.id.searchBadgesButton);
        ProgressBar spinner = view.findViewById(R.id.searchProgress);
        TextView noSearchResults = view.findViewById(R.id.noSearchResults);



        //When merit badges are added to the search results update list
        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {
            //sets loading spinner to true
            toggleSpinner(spinner);

            //sets the returned badges to the livedata that was changed
            badges = badgesLiveData.getValue();
            if (badges == null) return;
            if (badges.size() == 0)
            {
                //displays "No Search Results"
                noSearchResults.setVisibility(View.VISIBLE);
            }
            else noSearchResults.setVisibility(View.GONE);


            //sorts badges in name order
            Collections.sort(badges, Comparator.comparing(meritBadge::getName));

            //sets expandableList by view from XML
            accordionList = view.findViewById(R.id.expandableListViewSearch);
            //Sets the badge titles for the accordion list
            badgeTitles = SearchListTitles.getData(badges);
            //Creates an adapter to show the accordion titles
            expandableListAdapter = new SearchExpandListAdapter(getContext(), badgeTitles, badges);
            //sets adapter to the accordion list
            accordionList.setAdapter(expandableListAdapter);

        };

        searchBut.setOnClickListener(v ->
        {
            //toggles the spinner
            toggleSpinner(spinner);
            //gets the badge results and sets it as the returned badges
            setBadges(searchBar.getText().toString());

        });

        badgesLiveData.observe(getViewLifecycleOwner(), badgeChanged);
    }


    public void setBadges(String badgeName) {
        //ASYNC thread
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> {
            //gets the searched merit badges
            setLiveBadges(sqlRunner.getListOfBadges(badgeName));
        });
    }

    public void onPause() {
        super.onPause();
        accordionList = null;
    }

    public void onResume() {
        super.onResume();

        if (getView() == null) return;
        accordionList = getView().findViewById(R.id.expandableListViewSearch);
        //Sets the badge titles for the accordion list
        badgeTitles = SearchListTitles.getData(badges);
        //Creates an adapter to show the accordion titles
        expandableListAdapter = new SearchExpandListAdapter(getContext(), badgeTitles, badges);
        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

    public void setLiveBadges(ArrayList<meritBadge> b) { badgesLiveData.postValue(b); }

    public void toggleSpinner(ProgressBar spinner) {
        //switches spinner
        if (spinner.getVisibility() == View.VISIBLE) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    public static void toggleAddToList(scoutPerson p, int badgeID, boolean add)
    {
        //ASYNC task, adds or removes the merit badge based on whether the box was checked
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> sqlRunner.toggleAddToList(p, badgeID, add));
    }


}