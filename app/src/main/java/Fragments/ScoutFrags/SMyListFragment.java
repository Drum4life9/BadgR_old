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
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RequiresApi(api = Build.VERSION_CODES.N)
public class SMyListFragment extends Fragment {

    private static final MutableLiveData<int[]> returns = new MutableLiveData<>();
    private static ArrayList<meritBadge> badgesAdded;
    private static scoutPerson user;
    private static HashMap<Integer, ArrayList<Integer>> reqs = new HashMap<>();
    private ExpandableListView accordionList;
    private ProgressBar spinner;
    final Observer<int[]> loadListCheck = booleans -> {
        int[] rets = returns.getValue();
        assert rets != null;

        //if either method returned an error, toast error
        if (rets[0] == 2 || rets[1] == 2) {
            Toast.makeText(getContext(), "An error occurred with database connection. Try again.", Toast.LENGTH_LONG).show();
            toggleSpinner(spinner, false);
            return;
        }

        //badges came back
        if (rets[0] == 1 && rets[1] == 0)
            getReqs(user);

            //both infos come back
        else if (rets[0] == 1 && rets[1] == 1) {
            Collections.sort(badgesAdded, Comparator.comparing(meritBadge::getName));
            resetList(requireView(), true);
        }
    };

    public SMyListFragment(scoutPerson p) {
        user = p;
    }

    public static void toggleSpinner(ProgressBar spinner, boolean set) {
        //switches spinner
        if (!set) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    public static void getDatabaseInfo(scoutPerson p) {
        //pulls added badges
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            try {
                badgesAdded = sqlRunner.getAddedBadgesMB(p);
                returns.postValue(new int[]{1, 0});
            } catch (SQLException e) {
                returns.postValue(new int[]{2, 0});
            }
        });
    }

    public static void getReqs(scoutPerson p) {
        int[] rets = returns.getValue();

        assert rets != null;

        //pulls added badges
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            try {
                reqs = sqlRunner.getCompletedReqs(p);
                rets[1] = 1;
                returns.postValue(rets);
            } catch (SQLException e) {
                rets[1] = 2;
                returns.postValue(rets);
            }
        });
    }

    private static ArrayList<String> getData(ArrayList<meritBadge> mbs) {
        ArrayList<String> titles = new ArrayList<>();

        if (mbs == null) return titles;
        for (meritBadge b : mbs) {
            titles.add(b.getName());
        }

        return titles;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_my_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //sets page elements
        spinner = view.findViewById(R.id.searchProgressMyList);
        AppCompatButton submit = view.findViewById(R.id.MyListSubmitButton);
        Button clear = view.findViewById(R.id.clearButton);
        Button collapse = view.findViewById(R.id.collapseButton);


        //set observer for when database connection is made
        returns.observe(getViewLifecycleOwner(), loadListCheck);

        //submit button on click
        submit.setOnClickListener(v -> {

            accordionList.setVisibility(View.INVISIBLE);
            toggleSpinner(spinner, true);


            new Handler().postDelayed(() -> {
                //try to update requirements, if error display message
                try {
                    SMyListExpandListAdapter.updateRequirements(spinner);
                } catch (InterruptedException | ConcurrentModificationException e) {
                    Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_LONG).show();
                    return;
                }

                //gets any completed badges
                ArrayList<Integer> completedBadges = SMyListExpandListAdapter.checkCompletedBadges();

                //if there are completed badges
                if (completedBadges.size() != 0) {

                    //toast message
                    Toast.makeText(getContext(), "Requirements Updated, and All Completed Badges Moved to \"Completed Badges\"", Toast.LENGTH_LONG).show();
                    SCompletedBadges.getFinishedBadges(user);
                } else {
                    Toast.makeText(getContext(), "Requirements Updated!", Toast.LENGTH_LONG).show();
                }

                //get badges added and update finished reqs
                getDatabaseInfo(user);

                //if no badges added, Toast and kill method
                if (badgesAdded == null) return;
                if (badgesAdded.size() == 0) {
                    Toast.makeText(getContext(), "No Badges Added! Go to \"Search Badges\"", Toast.LENGTH_LONG).show();
                }

                toggleSpinner(spinner, true);

            }, 100);
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

            //reset list
            resetList(view, true);

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
    }

    //when tab is resumed
    public void onResume() {
        super.onResume();

        //update added badges and pull finished reqs
        toggleSpinner(spinner, true);
        getDatabaseInfo(user);
    }

    //when tab paused
    @Override
    public void onPause() {
        super.onPause();

        //reset checked reqs
        SMyListExpandListAdapter.resetCheckedReqs();
        resetList(requireView(), false);
    }

    private void resetList(View view, boolean keep) {

        //gets no badge text view
        TextView noBadges = view.findViewById(R.id.noBadges);

        //resets the accordionList
        this.accordionList = null;
        accordionList = view.findViewById(R.id.expandableListViewMyList);
        noBadges.setVisibility(View.INVISIBLE);

        if (!keep) {
            accordionList.setVisibility(View.GONE);
            return;
        }

        //Sets the badge titles for the accordion list
        ArrayList<String> badgeTitles = getData(badgesAdded);

        if (badgeTitles.size() == 0) {
            noBadges.setVisibility(View.VISIBLE);
            accordionList.setVisibility(View.GONE);
            toggleSpinner(spinner, false);
            return;
        }

        //Creates an adapter to show the accordion titles
        ExpandableListAdapter expandableListAdapter = new SMyListExpandListAdapter(getContext(), badgeTitles, badgesAdded, user, reqs);

        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
        accordionList.setVisibility(View.VISIBLE);

        toggleSpinner(spinner, false);
    }
}