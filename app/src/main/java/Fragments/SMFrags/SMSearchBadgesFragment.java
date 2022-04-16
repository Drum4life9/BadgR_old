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
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Fragments.ScoutFrags.SSearchListTitles;

public class SMSearchBadgesFragment extends Fragment {


    private ArrayList<meritBadge> badges;
    private final MutableLiveData<ArrayList<meritBadge>> badgesLiveData = new MutableLiveData<>();
    private final scoutMaster user = (scoutMaster) LoginRepository.getUser();
    private ListView list;
    private String[] scoutList;
    private ArrayList<Integer> addedScouts, checked;
    private FrameLayout frameLayout;
    private RelativeLayout rl;

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
        //Finds and stores actionable pieces, such as the search button and the text search box
        TextView searchBar = view.findViewById(R.id.SMbadgeSearchText);
        Button searchBut = view.findViewById(R.id.SMsearchBadgesButton);
        Button addBut = view.findViewById(R.id.SMAddButton);
        ProgressBar spinner = view.findViewById(R.id.SMsearchProgress);
        TextView noSearchResults = view.findViewById(R.id.SMnoSearchResults);
        frameLayout = view.findViewById(R.id.scoutPanel);
        rl = view.findViewById(R.id.relLayout);
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabSM);



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

        searchBut.setOnClickListener(v ->
        {
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

            rl.setVisibility(View.INVISIBLE);

            frameLayout.setVisibility(View.VISIBLE);
            list = view.findViewById(R.id.scoutList);



            //get scout names and add to String[]
            getScoutNames();
            SMSearchScoutListAdapter listAdapter = new SMSearchScoutListAdapter(getActivity(), scoutList, user);

            //set list adapter to customized list adapter (see SMScoutListAdapter)
            list.setAdapter(listAdapter);



        });

        fab.setOnClickListener(v -> {
            addedScouts = SMSearchScoutListAdapter.getAddedScouts();
            checked = SMSearchExpandListAdapter.getChecked();

            addBadgesToScouts(addedScouts, checked);

            Toast.makeText(getContext(), "All Scouts' badges updated", Toast.LENGTH_LONG).show();

            frameLayout.setVisibility(View.INVISIBLE);
            rl.setVisibility(View.VISIBLE);

            resetList();


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


        frameLayout.setVisibility(View.INVISIBLE);
        rl.setVisibility(View.VISIBLE);
        resetList();
        resetChecked();

    }

    public void onResume() {
        super.onResume();

        frameLayout.setVisibility(View.INVISIBLE);
        rl.setVisibility(View.VISIBLE);
        resetList();
        resetChecked();
    }



    private void getScoutNames()
    {
        ArrayList<scoutPerson> troop = user.getTroop();
        scoutList = new String[troop.size()];

        for (int i = 0; i < troop.size(); i++) scoutList[i] = troop.get(i).getFName() + " " + troop.get(i).getLName();
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
        ArrayList<String> badgeTitles = SSearchListTitles.getData(badges);
        //Creates an adapter to show the accordion titles
        ExpandableListAdapter expandableListAdapter = new SMSearchExpandListAdapter(getContext(), badgeTitles, badges);
        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

    private void addBadgesToScouts(ArrayList<Integer> scoutsList, ArrayList<Integer> checked)
    {
        Hashtable<Integer, ArrayList<Integer>> table = new Hashtable<>();
        for (int scoutID : scoutsList)
        {
            table.put(scoutID, checked);
        }


        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> sqlRunner.setBadges(table));
    }

    public static void resetChecked()
    {
        SMSearchScoutListAdapter.clearScouts();
        SMSearchExpandListAdapter.clear();
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
