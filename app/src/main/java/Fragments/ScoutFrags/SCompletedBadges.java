package Fragments.ScoutFrags;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SCompletedBadges extends Fragment {

    private ListView list;
    private static String[] compTitles;
    private static final MutableLiveData<ArrayList<meritBadge>> completedBadgesLive = new MutableLiveData<>();
    private static ArrayList<meritBadge> completedBadges = new ArrayList<>();
    private static scoutPerson user;

    //creates tab, sets user
    public SCompletedBadges (scoutPerson p) {user = p;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //immediately pull finished badges
        getFinishedBadges(user);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_completed_badges, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set page elements
        list = view.findViewById(R.id.completedListView);
        Button edit = view.findViewById(R.id.CompletedEdit);
        Button remove = view.findViewById(R.id.CompletedRemoveButton);
        TextView none = view.findViewById(R.id.CompletedTextNone);
        ProgressBar spinner = view.findViewById(R.id.spinner);

        spinner.setVisibility(View.VISIBLE);
        list.setVisibility(View.INVISIBLE);
        //when the database connection and results come back
        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {

            //get value
            completedBadges = completedBadgesLive.getValue();

            //if value is null or size is 0 (no completed badges)
            if (completedBadges == null || completedBadges.size() == 0) {

                //disable list and enable "no completed" text, also kill method
                edit.setVisibility(View.GONE);
                none.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                return;
            } else {

                //enable list and disable any potential no completed text
                none.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
            }

            //sorts Merit Badges by title
            Collections.sort(completedBadges, Comparator.comparing(meritBadge::getName));
            //creates a new list for the badge titles
            compTitles = new String[completedBadges.size()];

            //resets list
            resetList(view);

        };

        //sets observer for when database results are returned
        completedBadgesLive.observe(getViewLifecycleOwner(), badgeChanged);

        //edit button on click
        edit.setOnClickListener(v -> {
            //if no badges are completed, Toast message and kill method
            if (completedBadgesLive.getValue() == null || completedBadgesLive.getValue().size() == 0) {
                Toast.makeText(getContext(), "No badges completed!", Toast.LENGTH_SHORT).show();
                return;
            }

            //makes a different list, that can edit the badges that are selected to be removed (if accidental completion)
            list = null;
            list = view.findViewById(R.id.completedListView);

            SCompletedListAdapter adapter = new SCompletedListAdapter(getActivity(), compTitles, user);
            list.setAdapter(adapter);

            remove.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
        });

        //remove button on click
        remove.setOnClickListener(v -> {

            //get which badges are selected
            ArrayList<Integer> badges = SCompletedListAdapter.getCheckedBoxes();

            //hides the remove button
            remove.setVisibility(View.GONE);

            //if list != null or size > 0
            if (badges != null && badges.size() != 0) {

                //connects to SQL and updates badges
                ExecutorService STE = Executors.newSingleThreadExecutor();
                STE.execute(() ->
                        sqlRunner.removeCompleted(badges, user));

                SMyListFragment.getDatabaseInfo(user);

                //Toast complementary message
                Toast.makeText(getContext(), "Badges Removed", Toast.LENGTH_LONG).show();
            }

            //if all badges removed
            if (completedBadges == null || completedBadges.size() == 0) {
                //disable list and enable "no completed" text
                list.setVisibility(View.GONE);
                none.setVisibility(View.VISIBLE);
            }

        });

    }


    public static void getFinishedBadges(scoutPerson user) {
        ExecutorService sTE = Executors.newSingleThreadExecutor();

        //gets which badges have been completed
        sTE.execute(() ->
        {
            try {
                completedBadgesLive.postValue(sqlRunner.getCompletedBadges(user));
            } catch (SQLException ignored) {}
        });
    }

    private void setTitles() {

        //sets titles for the list to be made
        for (int i = 0; i < completedBadges.size(); i++)
            compTitles[i] = completedBadges.get(i).getName();
    }

    private void resetList(View view) {
        //sets titles
        setTitles();

        //resets list
        list = null;
        list = view.findViewById(R.id.completedListView);

        //Sets the badge titles for the accordion list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.completed_titles, compTitles);

        //set adapter and list to visible
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        view.findViewById(R.id.spinner).setVisibility(View.GONE);
    }
}