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
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SMyListFragment extends Fragment {

    private ExpandableListView accordionList;
    private static ArrayList<meritBadge> badgesAdded;
    private static final scoutPerson user = LoginRepository.getUser();
    private static final MutableLiveData<ArrayList<meritBadge>> badgesAddedLive = new MutableLiveData<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_my_list, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //toggles spinner
        ProgressBar spinner = view.findViewById(R.id.searchProgressMyList);
        AppCompatButton submit = view.findViewById(R.id.MyListSubmitButton);
        Button clear = view.findViewById(R.id.clearButton);
        Button collapse = view.findViewById(R.id.collapseButton);

        new Handler().postDelayed(SMyListFragment::getBadgesAdded, 200);


        final Observer<ArrayList<meritBadge>> obs = meritBadges -> {
            badgesAdded = badgesAddedLive.getValue();
            if (badgesAdded == null) {
                toggleSpinner(spinner, false);
                return;
            }
            Collections.sort(badgesAdded, Comparator.comparing(meritBadge::getName));

            resetList(view);
            toggleSpinner(spinner, false);

        };


        badgesAddedLive.observe(getViewLifecycleOwner(), obs);



        submit.setOnClickListener(v ->
        {
            SMyListExpandListAdapter.updateRequirements();
            SMyListExpandListAdapter.copyFinished();

            ArrayList<Integer> completedBadges = SMyListExpandListAdapter.checkCompletedBadges();
            if (completedBadges.size() != 0) {
                Toast.makeText(getContext(), "Requirements Updated, and All Completed Badges Moved to \"Completed Badges\"", Toast.LENGTH_LONG).show();
                SCompletedBadges.getFinishedBadges();
            } else {
                Toast.makeText(getContext(), "Requirements Updated!", Toast.LENGTH_LONG).show();
            }
            getBadgesAdded();
            SMyListExpandListAdapter.pullFinishedReqs(user);

            if (badgesAdded == null) return;
            if (badgesAdded.size() == 0) {
                Toast.makeText(getContext(), "No Badges Added! Go to \"Search Badges\"", Toast.LENGTH_LONG).show();
                return;
            }

            resetList(view);
        });




        clear.setOnClickListener(v ->
        {
            ArrayList<Integer> expanded = new ArrayList<>();
            if (badgesAdded == null || accordionList == null) return;
            for (int i = 0; i < badgesAdded.size(); i++) {
                if (accordionList.isGroupExpanded(i)) expanded.add(i);
            }

            //clears the previously checked boxes
            SMyListExpandListAdapter.resetCheckedReqs();

            resetList(view);

            for (int i = 0; i < expanded.size(); i++) {
                accordionList.expandGroup(expanded.get(i));
            }

            Toast.makeText(getContext(), "All Changed Boxes Reset!", Toast.LENGTH_LONG).show();
        });




        collapse.setOnClickListener(v -> {
            if (accordionList == null) return;
            for (int i = 0; i < badgesAdded.size(); i++) {
                accordionList.collapseGroup(i);
            }

            Toast.makeText(getContext(), "All Boxes Collapsed!", Toast.LENGTH_LONG).show();
        });

        toggleSpinner(spinner, false);
    }

    public void onResume() {
        super.onResume();
        getBadgesAdded();
        SMyListExpandListAdapter.pullFinishedReqs(user);
    }

    @Override
    public void onPause() {
        super.onPause();
        SMyListExpandListAdapter.resetCheckedReqs();
    }

    public static void toggleSpinner(ProgressBar spinner, boolean set) {
        //switches spinner
        if (!set) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    public static void getBadgesAdded() {
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> badgesAddedLive.postValue(sqlRunner.getAddedBadgesMB(user)));
    }


    private void resetList(View view) {
        //resets the accordionList
        this.accordionList = null;
        accordionList = view.findViewById(R.id.expandableListViewMyList);
        //Sets the badge titles for the accordion list
        ArrayList<String> badgeTitles = SSearchListTitles.getData(badgesAdded);

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



}