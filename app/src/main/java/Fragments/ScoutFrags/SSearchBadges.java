package Fragments.ScoutFrags;


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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SSearchBadges extends Fragment {

    ExpandableListView accordionList;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<String> badgeTitles;
    ArrayList<meritBadge> badges;
    MutableLiveData<ArrayList<meritBadge>> badgesLiveData = new MutableLiveData<>();
    scoutPerson user = LoginRepository.getUser();

    //runs first
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_search_badges, container, false);

    }

    //runs after fragment is created
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Finds and stores actionable pieces, such as the search button and the text search box
        TextView searchBar = view.findViewById(R.id.badgeSearchText);
        Button searchBut = view.findViewById(R.id.searchBadgesButton);
        Button submitBut = view.findViewById(R.id.searchSubmit);
        ProgressBar spinner = view.findViewById(R.id.searchProgress);
        TextView noSearchResults = view.findViewById(R.id.noSearchResults);


        //When merit badges are added to the search results update list
        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {
            //sets loading spinner to true
            toggleSpinner(spinner);

            //sets the returned badges to the livedata that was changed
            badges = badgesLiveData.getValue();
            if (badges == null) return;
            if (badges.size() == 0) {
                //displays "No Search Results"
                noSearchResults.setVisibility(View.VISIBLE);
            } else noSearchResults.setVisibility(View.GONE);


            //sorts badges in name order
            Collections.sort(badges, Comparator.comparing(meritBadge::getName));

            resetList(view);
        };

        searchBut.setOnClickListener(v ->
        {
            //toggles the spinner
            toggleSpinner(spinner);
            //gets the badge results and sets it as the returned badges
            setBadges(searchBar.getText().toString(), view);

        });

        badgesLiveData.observe(getViewLifecycleOwner(), badgeChanged);

        submitBut.setOnClickListener(v -> {
            ArrayList<Integer> addedBoxes = SSearchExpandListAdapter.getAddedBoxes();
            ArrayList<Integer> removedBoxes = SSearchExpandListAdapter.getRemovedBoxes();

            ExecutorService STE = Executors.newSingleThreadExecutor();
            CountDownLatch cdl = new CountDownLatch(1);
            STE.execute(() ->
            {
                sqlRunner.toggleAddToList(user, addedBoxes, removedBoxes);
                cdl.countDown();
            });

            try {
                cdl.await();
                Toast.makeText(getContext(), "My List updated!", Toast.LENGTH_LONG).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_LONG).show();
            }

            resetList(view);
        });

    }


    public void setBadges(String badgeName, View view) {
        //ASYNC thread
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> {
            //gets the searched merit badges
            setLiveBadges(sqlRunner.searchForBadges(badgeName));
        });

        resetList(view);
    }

    public void onPause() {
        super.onPause();
        SMyListFragment.getBadgesAdded();
        accordionList = null;
    }

    public void onResume() {
        super.onResume();

        if (getView() == null) return;
        accordionList = getView().findViewById(R.id.expandableListViewSearch);
        //Sets the badge titles for the accordion list
        badgeTitles = SSearchListTitles.getData(badges);
        //Creates an adapter to show the accordion titles
        expandableListAdapter = new SSearchExpandListAdapter(getContext(), badgeTitles, badges, user);
        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

    public void setLiveBadges(ArrayList<meritBadge> b) {
        badgesLiveData.postValue(b);
    }

    public void toggleSpinner(ProgressBar spinner) {
        //switches spinner
        if (spinner.getVisibility() == View.VISIBLE) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }


    public void resetList(View view) {
        //sets expandableList by view from XML
        accordionList = view.findViewById(R.id.expandableListViewSearch);
        //Sets the badge titles for the accordion list
        badgeTitles = SSearchListTitles.getData(badges);
        //Creates an adapter to show the accordion titles
        expandableListAdapter = new SSearchExpandListAdapter(getContext(), badgeTitles, badges, user);
        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

}