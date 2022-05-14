package Fragments.SMFrags;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMSearchBadgesFragment extends Fragment {


    private ArrayList<meritBadge> badges;
    private final MutableLiveData<ArrayList<meritBadge>> badgesLiveData = new MutableLiveData<>();
    private final scoutMaster user;
    private ListView list;
    private String[] scoutList;
    private ArrayList<Integer> addedScouts, checked;
    private static Button addBut;
    private FrameLayout frameLayout;
    private RelativeLayout rl;

    public SMSearchBadgesFragment(scoutMaster u) {user = u;}

    //runs first
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scoutmaster_fragment_search_badges, container, false);

    }

    //runs after fragment is created
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //sets page elements
        TextView searchBar = view.findViewById(R.id.SMbadgeSearchText);
        Button searchBut = view.findViewById(R.id.SMsearchBadgesButton);
        Button cancel = view.findViewById(R.id.cancel);
        addBut = view.findViewById(R.id.SMAddButton);
        ProgressBar spinner = view.findViewById(R.id.SMsearchProgress);
        TextView noSearchResults = view.findViewById(R.id.SMnoSearchResults);
        frameLayout = view.findViewById(R.id.scoutPanel);
        rl = view.findViewById(R.id.relLayout);
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabSM);

        //set button enabled to false
        addBut.setEnabled(false);

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

            resetList();

        };

        searchBut.setOnClickListener(v -> {

            //toggles the spinner
            toggleSpinner(spinner);
            //gets the badge results and sets it as the returned badges
            setBadges(searchBar.getText().toString());

        });


        //when the add button is clicked
        addBut.setOnClickListener(v -> {
            //bring up scoutList screen
            if (getActivity() != null)
                hideKeyboard(getActivity());

            //get scout names and add to String[]
            getScoutNames();

            //toggle visibilities
            rl.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.VISIBLE);

            //set lists
            list = view.findViewById(R.id.scoutList);
            RelativeLayout miniList = view.findViewById(R.id.miniList);

            //heights used for margin calculation
            int totalSpace = 70 * (scoutList.length + 4) + 150;
            int height = rl.getHeight();

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );

            //if total space needed is bigger than margins that are needed, set margins and let the list scroll
            if (totalSpace > height - 200) params.setMargins(100, 100, 100, 100);
            else params.setMargins(100, (height - totalSpace) / 2, 100, (height - totalSpace) / 2);
            miniList.setLayoutParams(params);

            //create and set list adapter
            SMSearchScoutListAdapter listAdapter = new SMSearchScoutListAdapter(getActivity(), scoutList, user);
            list.setAdapter(listAdapter);

        });

        //floating action button inside scout list on click
        fab.setOnClickListener(v -> {

            //gets the added scouts and added badges
            addedScouts = SMSearchScoutListAdapter.getAddedScouts();
            checked = SMSearchExpandListAdapter.getChecked();

            //database connection to add badges to scouts
            addBadgesToScouts(addedScouts, checked);

            //toast completion message
            Toast.makeText(getContext(), "All Scouts' badges updated", Toast.LENGTH_LONG).show();

            //reset list and screen
            frameLayout.setVisibility(View.INVISIBLE);
            rl.setVisibility(View.VISIBLE);

            resetList();

            addBut.setEnabled(false);
        });

        //cancel button inside scout list on click
        cancel.setOnClickListener(v -> {
            //reset screen
            frameLayout.setVisibility(View.INVISIBLE);
            rl.setVisibility(View.VISIBLE);

            //toast message
            Toast.makeText(getContext(), "No badges changed", Toast.LENGTH_LONG).show();

            //clear checked boxes
            SMSearchExpandListAdapter.clear();

            addBut.setEnabled(false);
            resetList();
        });

        //waits until database results return
        badgesLiveData.observe(getViewLifecycleOwner(), badgeChanged);
    }

    public static void toggleBut(boolean bool) { addBut.setEnabled(bool); }

    public void setBadges(String badgeName) {
        //gets badges that user searches for
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> setLiveBadges(sqlRunner.searchForBadges(badgeName)));
    }

    public void onPause() {
        super.onPause();

        //reset screen elements
        frameLayout.setVisibility(View.INVISIBLE);
        rl.setVisibility(View.VISIBLE);
        resetList();
        resetChecked();

    }

    public void onResume() {
        super.onResume();

        //reset screen elements
        frameLayout.setVisibility(View.INVISIBLE);
        rl.setVisibility(View.VISIBLE);
        resetList();
        resetChecked();
    }



    private void getScoutNames() {

        //creates a String list with all scout names
        ArrayList<scoutPerson> troop = user.getTroop();
        scoutList = new String[troop.size()];

        for (int i = 0; i < troop.size(); i++) scoutList[i] = troop.get(i).getFullName();
    }


    public void setLiveBadges(ArrayList<meritBadge> b) {
        badgesLiveData.postValue(b);
    }

    public void toggleSpinner(ProgressBar spinner) {
        //switches spinner
        if (spinner.getVisibility() == View.VISIBLE) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    private void resetList() {
        if (getView() == null) return;
        ExpandableListView accordionList = getView().findViewById(R.id.SMexpandableListViewSearch);
        //Sets the badge titles for the accordion list
        ArrayList<String> badgeTitles = getData(badges);
        //Creates an adapter to show the accordion titles
        ExpandableListAdapter expandableListAdapter = new SMSearchExpandListAdapter(getContext(), badgeTitles, badges);
        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

    private void addBadgesToScouts(ArrayList<Integer> scoutsList, ArrayList<Integer> checked) {

        //creates table with scout IDs and badges to assign to each scout
        Hashtable<Integer, ArrayList<Integer>> table = new Hashtable<>();
        for (int scoutID : scoutsList)
            table.put(scoutID, checked);

        //make database connection to add badges
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            try {
                sqlRunner.smSetBadges(table);
            } catch (SQLException e) {
                Toast.makeText(getContext(), "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void resetChecked()
    {
        SMSearchScoutListAdapter.clearScouts();
        SMSearchExpandListAdapter.clear();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        //check if no view has focus; hide keyboard
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static ArrayList<String> getData(ArrayList<meritBadge> mbs) {
        ArrayList<String> titles = new ArrayList<>();

        //add badge name to String list of badge names for the list
        if (mbs == null) return titles;
        for (meritBadge b : mbs) {
            titles.add(b.getName());
        }

        return titles;
    }

}
