package Fragments.ScoutFrags.MyListDrivers;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Fragments.ScoutFrags.SearchFragmentDrivers.SearchListTitles;


public class MyListFragment extends Fragment {

    ExpandableListView accordionList;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<String> badgeTitles;
    static MutableLiveData<ArrayList<meritBadge>> badgesAddedLive = new MutableLiveData<>();
    static ArrayList<meritBadge> badgesAdded;
    static scoutPerson user = LoginRepository.getUser();
    ProgressBar spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //changes myList badges
        getBadgesAdded();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_list, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //toggles spinner
        spinner = view.findViewById(R.id.searchProgressMyList);
        AppCompatButton submit = view.findViewById(R.id.MyListSubmitButton);
        Button clear = view.findViewById(R.id.clearButton);
        Button collapse = view.findViewById(R.id.collapseButton);
        
        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {

            badgesAdded = badgesAddedLive.getValue();
            if (badgesAdded == null)
            {
                //TODO text thing here
                return;
            }
            Collections.sort(badgesAdded, Comparator.comparing(meritBadge::getName));

            accordionList = view.findViewById(R.id.expandableListViewMyList);
            //Sets the badge titles for the accordion list
            badgeTitles = SearchListTitles.getData(badgesAdded);
            //Creates an adapter to show the accordion titles
            expandableListAdapter = new MyListExpandListAdapter(getContext(), badgeTitles, badgesAdded);
            //sets adapter to the accordion list
            accordionList.setAdapter(expandableListAdapter);


        };

        badgesAddedLive.observe(getViewLifecycleOwner(), badgeChanged);


        
        
        submit.setOnClickListener(v ->
        {
            MyListExpandListAdapter.updateRequirements();
            MyListExpandListAdapter.copyFinished();
            Toast.makeText(getContext(), "Requirements Updated!", Toast.LENGTH_LONG).show();
        });

        clear.setOnClickListener(v ->
        {
            ArrayList<Integer> expanded = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(badgesAddedLive.getValue()).size(); i++)
            {
                if (accordionList.isGroupExpanded(i)) expanded.add(i);
            }

            //clears the previously checked boxes
            MyListExpandListAdapter.resetCheckedReqs();

            //resets the accordionList
            accordionList = null;
            accordionList = view.findViewById(R.id.expandableListViewMyList);
            //Sets the badge titles for the accordion list
            badgeTitles = SearchListTitles.getData(badgesAdded);
            //Creates an adapter to show the accordion titles
            expandableListAdapter = new MyListExpandListAdapter(getContext(), badgeTitles, badgesAdded);
            //sets adapter to the accordion list
            accordionList.setAdapter(expandableListAdapter);

            for (int i = 0; i < expanded.size(); i++)
            {
                accordionList.expandGroup(expanded.get(i));
            }

            Toast.makeText(getContext(), "All Changed Boxes Reset!", Toast.LENGTH_LONG).show();
        });

        collapse.setOnClickListener(v -> {
            if (accordionList == null) return;
            for (int i = 0; i < Objects.requireNonNull(badgesAddedLive.getValue()).size(); i++)
            {
                accordionList.collapseGroup(i);
            }
        });
    }

    public void onPause() {
        super.onPause();
        getBadgesAdded();
        MyListExpandListAdapter.pullFinishedReqs(user);

    }

    public void onResume() {
        super.onResume();
        getBadgesAdded();
        MyListExpandListAdapter.pullFinishedReqs(user);
    }

    public static void toggleSpinner(ProgressBar spinner) {
        //switches spinner
        if (spinner.getVisibility() == View.VISIBLE) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    public static void getBadgesAdded()
    {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() ->
                badgesAddedLive.postValue(sqlRunner.getListOfBadges(user)));
    }

}