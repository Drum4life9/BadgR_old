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
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SMyListFragment extends Fragment {

    private ExpandableListView accordionList;
    private static final MutableLiveData<ArrayList<meritBadge>> badgesAddedLive = new MutableLiveData<>();
    private static ArrayList<meritBadge> badgesAdded;
    private static final scoutPerson user = LoginRepository.getUser();
    private ProgressBar spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //changes myList badges
        getBadgesAdded();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_my_list, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //toggles spinner
        spinner = view.findViewById(R.id.searchProgressMyList);
        AppCompatButton submit = view.findViewById(R.id.MyListSubmitButton);
        Button clear = view.findViewById(R.id.clearButton);
        Button collapse = view.findViewById(R.id.collapseButton);
        toggleSpinner(spinner, true);

        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {

            badgesAdded = badgesAddedLive.getValue();
            if (badgesAdded == null)
            {
                toggleSpinner(spinner, false);
                //TODO text thing here
                return;
            }
            Collections.sort(badgesAdded, Comparator.comparing(meritBadge::getName));

            resetList(view);
            toggleSpinner(spinner, false);
        };

        badgesAddedLive.observe(getViewLifecycleOwner(), badgeChanged);


        
        
        submit.setOnClickListener(v ->
        {
            SMyListExpandListAdapter.updateRequirements();
            SMyListExpandListAdapter.copyFinished();

            if (badgesAdded.size() == 0)
            {
                Toast.makeText(getContext(), "No Badges Added! Go to \"Search Badges\"", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<Integer> completedBadges = SMyListExpandListAdapter.checkCompletedBadges();
            if (completedBadges.size() != 0)
            {
                //resets the accordionList
                resetList(view);

                Toast.makeText(getContext(), "Requirements Updated, and All Completed Badges Moved to \"Completed Badges\"", Toast.LENGTH_LONG).show();
                SCompletedBadges.getFinishedBadges();
            }
            else {
                ArrayList<Integer> expanded = new ArrayList<>();
                for (int i = 0; i < Objects.requireNonNull(badgesAddedLive.getValue()).size(); i++)
                {
                    if (accordionList.isGroupExpanded(i)) expanded.add(i);
                }
                for (int i = 0; i < expanded.size(); i++)
                {
                    accordionList.expandGroup(expanded.get(i));
                }
                Toast.makeText(getContext(), "Requirements Updated!", Toast.LENGTH_LONG).show();


            }
            getBadgesAdded();
            SMyListExpandListAdapter.pullFinishedReqs(user);
        });

        clear.setOnClickListener(v ->
        {
            ArrayList<Integer> expanded = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(badgesAddedLive.getValue()).size(); i++)
            {
                if (accordionList.isGroupExpanded(i)) expanded.add(i);
            }

            //clears the previously checked boxes
            SMyListExpandListAdapter.resetCheckedReqs();

            //resets the accordionList
            badgesAdded = badgesAddedLive.getValue();

            resetList(view);

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

            Toast.makeText(getContext(), "All Boxes Collapsed!", Toast.LENGTH_LONG).show();
        });

        toggleSpinner(spinner, false);
    }

    public void onPause() {
        super.onPause();
        getBadgesAdded();
        SMyListExpandListAdapter.pullFinishedReqs(user);

    }

    public void onResume() {
        super.onResume();
        getBadgesAdded();
        SMyListExpandListAdapter.pullFinishedReqs(user);
    }


    public static void toggleSpinner(ProgressBar spinner, boolean set) {
        //switches spinner
        if (!set) spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
    }

    public static void getBadgesAdded()
    {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() ->
                {
                badgesAddedLive.postValue(sqlRunner.getListOfBadges(user));
                sqlRunner.getFinishedBadges(user);
                });
    }


    private void resetList(View view)
    {
        //resets the accordionList
        this.accordionList = null;
        accordionList = view.findViewById(R.id.expandableListViewMyList);
        //Sets the badge titles for the accordion list
        ArrayList<String> badgeTitles = SSearchListTitles.getData(badgesAdded);
        //Creates an adapter to show the accordion titles
        ExpandableListAdapter expandableListAdapter = new SMyListExpandListAdapter(getContext(), badgeTitles, badgesAdded, user);
        //sets adapter to the accordion list
        accordionList.setAdapter(expandableListAdapter);
    }

    public static void removeLiveAdded(int bID)
    {
        ArrayList<meritBadge> badges = badgesAddedLive.getValue();
        if (badges != null)
            for (Iterator<meritBadge> it = badges.iterator(); it.hasNext();) {
                meritBadge b = it.next();
                if (b.getId() == bID) it.remove();
            }

        badgesAddedLive.postValue(badges);
    }

}