package Fragments.ScoutFrags;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
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
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SMyListFragment extends Fragment {

    private ExpandableListView accordionList;
    private static ArrayList<meritBadge> badgesAdded;
    private static scoutPerson user;
    private static final MutableLiveData<ArrayList<meritBadge>> badgesAddedLive = new MutableLiveData<>();

    public SMyListFragment(scoutPerson p) {user = p;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_my_list, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //sets page elementsa
        ProgressBar spinner = view.findViewById(R.id.searchProgressMyList);
        AppCompatButton submit = view.findViewById(R.id.MyListSubmitButton);
        Button clear = view.findViewById(R.id.clearButton);
        Button collapse = view.findViewById(R.id.collapseButton);

        //lets spinner activate by delaying thread call
        new Handler().postDelayed(() -> getBadgesAdded(user), 150);

        //when in progress badges are returned
        final Observer<ArrayList<meritBadge>> obs = meritBadges -> {

            //get value
            badgesAdded = badgesAddedLive.getValue();

            //if value == null kill method
            if (badgesAdded == null) {
                return;
            }

            //sort merit badges by name order
            Collections.sort(badgesAdded, Comparator.comparing(meritBadge::getName));

            //resets list and toggles spinner to false
            resetList(view);
            toggleSpinner(spinner, false);

        };

        //set observer for when database connection is made
        badgesAddedLive.observe(getViewLifecycleOwner(), obs);

        //submit button on click
        submit.setOnClickListener(v -> {

            //try to update requirements, if error display message
            try {
                SMyListExpandListAdapter.updateRequirements();
            } catch (InterruptedException | ConcurrentModificationException e) {
                Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_LONG).show();
                return;
            }

            //gets any completed badges
            ArrayList<Integer> completedBadges = SMyListExpandListAdapter.checkCompletedBadges(getContext());

            //if there are completed badges
            if (completedBadges.size() != 0) {

                //toast message
                Toast.makeText(getContext(), "Requirements Updated, and All Completed Badges Moved to \"Completed Badges\"", Toast.LENGTH_LONG).show();
                SCompletedBadges.getFinishedBadges(user);
            } else {
                Toast.makeText(getContext(), "Requirements Updated!", Toast.LENGTH_LONG).show();
            }

            //get badges added and update finished reqs
            getBadgesAdded(user);
            SMyListExpandListAdapter.pullFinishedReqs(user);

            //if no badges added, Toast and kill method
            if (badgesAdded == null) return;
            if (badgesAdded.size() == 0) {
                Toast.makeText(getContext(), "No Badges Added! Go to \"Search Badges\"", Toast.LENGTH_LONG).show();
                return;
            }

            //reset list
            resetList(view);
        });

        //clear button on click
        clear.setOnClickListener(v -> {

            //gets expanded groups
            ArrayList<Integer> expanded = new ArrayList<>();
            if (badgesAdded == null || accordionList == null) return;
            for (int i = 0; i < badgesAdded.size(); i++) {
                if (accordionList.isGroupExpanded(i)) expanded.add(i);
            }

            //clears the previously checked boxes
            SMyListExpandListAdapter.resetCheckedReqs();

            //reset lise
            resetList(view);

            //re-expand groups
            for (int i = 0; i < expanded.size(); i++) {
                accordionList.expandGroup(expanded.get(i));
            }

            //Toast
            Toast.makeText(getContext(), "All Changed Boxes Reset!", Toast.LENGTH_LONG).show();
        });

        //collapse on click
        collapse.setOnClickListener(v -> {

            //collapse all groups
            if (accordionList == null) return;
            for (int i = 0; i < badgesAdded.size(); i++) {
                accordionList.collapseGroup(i);
            }

            //Toast
            Toast.makeText(getContext(), "All Boxes Collapsed!", Toast.LENGTH_LONG).show();
        });

        //Set spinner to false
        toggleSpinner(spinner, false);
    }

    //when tab is resumed
    public void onResume() {
        super.onResume();

        //update added badges and pull finished reqs
        getBadgesAdded(user);
        SMyListExpandListAdapter.pullFinishedReqs(user);

        //reset list
        resetList(requireView());
    }

    //when tab paused
    @Override
    public void onPause() {
        super.onPause();

        //reset checked reqs
        SMyListExpandListAdapter.resetCheckedReqs();
    }

    public static void toggleSpinner(ProgressBar spinner, boolean set) {
        //switches spinner
        if (!set) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    public static void getBadgesAdded(scoutPerson p) {

        //pulls added badges
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            try {
                badgesAddedLive.postValue(sqlRunner.getAddedBadgesMB(p));
            } catch (SQLException ignored) {}
        });
    }


    private void resetList(View view) {

        //resets the accordionList
        this.accordionList = null;
        accordionList = view.findViewById(R.id.expandableListViewMyList);

        //Sets the badge titles for the accordion list
        ArrayList<String> badgeTitles = getData(badgesAdded);

        try {
            //Creates an adapter to show the accordion titles
            ExpandableListAdapter expandableListAdapter = new SMyListExpandListAdapter(getContext(), badgeTitles, badgesAdded, user);

            //sets adapter to the accordion list
            accordionList.setAdapter(expandableListAdapter);
            accordionList.setVisibility(View.VISIBLE);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred with loading the list. Please try again.", Toast.LENGTH_LONG).show();
        }
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