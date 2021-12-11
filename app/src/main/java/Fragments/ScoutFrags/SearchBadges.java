package Fragments.ScoutFrags;

import android.opengl.Visibility;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutPagesAndClasses.CustomizedExpandableListAdapter;
import com.badgr.scoutPagesAndClasses.ExpandableListDataItems;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO badges

public class SearchBadges extends Fragment {

    ExpandableListView expandableListViewExample;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableTitleList;
    HashMap<String, List<String>> expandableDetailList;
    ArrayList<meritBadge> badges = null;
    MutableLiveData<ArrayList<meritBadge>> badgesLiveData = new MutableLiveData<>();

    //runs first
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_badges, container, false);

    }

    //runs after fragment is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView searchBar = view.findViewById(R.id.badgeSearchText);
        Button searchBut = view.findViewById(R.id.searchBadgesButton);
        ProgressBar spinner = view.findViewById(R.id.searchProgress);



        //When merit badges are added to the search results update list
        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {

            //TODO add merit badges to the expand list here
            expandableListViewExample = view.findViewById(R.id.expandableListViewSample);
            expandableDetailList = ExpandableListDataItems.getData(badgesLiveData.getValue());
            expandableTitleList = new ArrayList<>(expandableDetailList.keySet());
            expandableListAdapter = new CustomizedExpandableListAdapter(getContext(), expandableTitleList, expandableDetailList);
            expandableListViewExample.setAdapter(expandableListAdapter);


            //When child item from list is clicked
            expandableListViewExample.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                //TODO when click on merit badge, get id and add to list
                //expandableDetailList.get(expandableTitleList.get(groupPosition)).get(childPosition);
                return false;
            });

            for (meritBadge b : Objects.requireNonNull(badgesLiveData.getValue()))
                System.out.println(b);

            toggleSpinner(spinner);
        };

        searchBut.setOnClickListener(v ->
        {

            toggleSpinner(spinner);
            setBadges(searchBar.getText().toString());
        });

        badgesLiveData.observe(getViewLifecycleOwner(), badgeChanged);
    }

    public void setBadges(String badgeName) {

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> {
            ArrayList<meritBadge> retList = sqlRunner.getListOfBadges(badgeName);
            setLiveBadges(retList);
        });
    }


    public void setLiveBadges(ArrayList<meritBadge> b) { badgesLiveData.postValue(b); }

    public void toggleSpinner(ProgressBar spinner)
    {
        if (spinner.getVisibility() == View.VISIBLE) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }
}