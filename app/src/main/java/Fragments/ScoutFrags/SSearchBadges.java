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
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SSearchBadges extends Fragment {

    private ExpandableListView accordionList;
    private ArrayList<meritBadge> badges;
    private final MutableLiveData<ArrayList<meritBadge>> badgesLiveData = new MutableLiveData<>();
    private final scoutPerson user;

    public SSearchBadges(scoutPerson p) {user = p;}

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

        //Sets page elements
        TextView searchBar = view.findViewById(R.id.badgeSearchText);
        Button searchBut = view.findViewById(R.id.searchBadgesButton);
        Button submitBut = view.findViewById(R.id.searchSubmit);
        ProgressBar spinner = view.findViewById(R.id.searchProgress);
        TextView noSearchResults = view.findViewById(R.id.noSearchResults);


        //Observer for when database results return
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

            //reset list
            resetList(view);
        };

        //search button on click
        searchBut.setOnClickListener(v -> {

            //toggles the spinner
            toggleSpinner(spinner);

            //gets the badge results and sets it as the returned badges
            setBadges(searchBar.getText().toString(), view);
        });

        //sets observer for when database results come back
        badgesLiveData.observe(getViewLifecycleOwner(), badgeChanged);

        //submit button on click
        submitBut.setOnClickListener(v -> {

            //gets added and removed badge lists
            ArrayList<Integer> addedBoxes = SSearchExpandListAdapter.getAddedBoxes();
            ArrayList<Integer> removedBoxes = SSearchExpandListAdapter.getRemovedBoxes();

            //runs SQL toggle add to list
            ExecutorService STE = Executors.newSingleThreadExecutor();
            CountDownLatch cdl = new CountDownLatch(1);
            final boolean[] success = new boolean[1];
            STE.execute(() ->
            {
                try {
                    sqlRunner.toggleAddToList(user, addedBoxes, removedBoxes);
                    success[0] = true;
                } catch (SQLException e) {
                    success[0] = false;
                }
                cdl.countDown();
            });

            //if an error occurred Toast message
            try {
                cdl.await();
                if (!success[0]) throw new InterruptedException();
                Toast.makeText(getContext(), "My List updated!", Toast.LENGTH_LONG).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_LONG).show();
            }

            //reset list
            resetList(view);
        });

    }


    public void setBadges(String badgeName, View view) {

        //makes database connection to get searched badge names
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> setLiveBadges(sqlRunner.searchForBadges(badgeName)));

        //resets list
        resetList(view);
    }

    //when tab paused
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onPause() {
        super.onPause();

        //set list to null and get added badges
        SMyListFragment.getDatabaseInfo(user);
        accordionList = null;
    }

    public void onResume() {
        super.onResume();

        //reset list
        resetList(requireView());
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
        ArrayList<String> badgeTitles = getData(badges);

        //Creates an adapter to show the accordion titles
        ExpandableListAdapter expandableListAdapter = new SSearchExpandListAdapter(getContext(), badgeTitles, badges, user);

        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

    private static ArrayList<String> getData(ArrayList<meritBadge> mbs) {
        ArrayList<String> titles = new ArrayList<>();

        if (mbs == null) return titles;
        for (meritBadge b : mbs) {
            titles.add(b.getName());
        }

        return titles;
    }

}