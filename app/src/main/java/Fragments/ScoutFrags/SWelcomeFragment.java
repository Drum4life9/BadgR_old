package Fragments.ScoutFrags;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SWelcomeFragment extends Fragment {

    private final scoutPerson user;
    private LinearLayout infoGrid;
    private ProgressBar spinner;

    //sets user when fragment initialized
    public SWelcomeFragment(scoutPerson p) { user = p; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //sets page elements
        infoGrid = view.findViewById(R.id.infoGrid);
        spinner = view.findViewById(R.id.spinner);

        //sets visibilities
        infoGrid.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);

        //delays 150ms, then reloads the list so spinner activates
        new Handler().postDelayed(() -> reload(view, getContext()), 150);
    }

    //reloads list
    private void reload(View view, Context context)
    {
        //sets page elements
        TextView progress = view.findViewById(R.id.progressGrid);
        TextView compText = view.findViewById(R.id.completedGrid);
        TextView eagleText = view.findViewById(R.id.eagleGrid);

        //lists for when database connection comes back
        MutableLiveData<ArrayList<meritBadge>> add = new MutableLiveData<>(), comp = new MutableLiveData<>();
        MutableLiveData<int[]> success = new MutableLiveData<>();

        //gets added merit badges. If success, set first of success list to 1, else 2
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            int[] bool = success.getValue();
            if (bool == null) bool = new int[2];

            try {
                add.postValue(sqlRunner.getAddedBadgesMB(user));
                bool[0] = 1;
            } catch (SQLException e) {
                bool[0] = 2;
            }
            success.postValue(bool);
        });

        final Observer<ArrayList<meritBadge>> addedObs = meritBadges -> {
            //gets completed merit badges. If success, set second of success list to 1, else 2
            ExecutorService STE2 = Executors.newSingleThreadExecutor();
            STE2.execute(() -> {
                int[] bool = success.getValue();
                if (bool == null) bool = new int[2];

                try {
                    comp.postValue(sqlRunner.getCompletedBadges(user));
                    bool[1] = 1;
                } catch (SQLException e) {
                    bool[1] = 2;
                }
                success.postValue(bool);
            });
        };

        //sets display strings
        final String[] progressText = new String[1];
        final String[] compS = new String[1];
        final String[] eagleS = new String[1];

        //when success list is changed
        final Observer<int[]> successObs = ints -> {
            int[] booleans = success.getValue();
            assert booleans != null;

            //if one of the sets of data hasn't come back yet, return
            if (booleans[0] == 0 || booleans[1] == 0) return;

            //if either produced an error
            if (booleans[0] == 2 || booleans[1] == 2)
            {
                Toast.makeText(context, "A database error has occurred", Toast.LENGTH_LONG).show();
                progressText[0] = "Error";
                compS[0] = "Error";
                eagleS[0] = "Error";

                spinner.setVisibility(View.GONE);
                infoGrid.setVisibility(View.VISIBLE);
                return;
            }

            //gets values of returned results
            ArrayList<meritBadge> added = add.getValue(), completed = comp.getValue(), eagle = new ArrayList<>();

            //if either list is null, create new
            if (added == null) added = new ArrayList<>();
            if (completed == null) completed = new ArrayList<>();

            //add any merit badges in added that are eagle required
            for (meritBadge mb : added)
                if (mb.isEagle()) eagle.add(mb);

            //sets strings
            progressText[0] = added.size() + " badge(s)";
            compS[0] = completed.size() + " badge(s)";
            eagleS[0] = eagle.size() + " badge(s)";

            //sets table with strings
            progress.setText(progressText[0]);
            compText.setText(compS[0]);
            eagleText.setText(eagleS[0]);

            spinner.setVisibility(View.GONE);
            infoGrid.setVisibility(View.VISIBLE);
        };


        //sets observer for success list
        success.observe(getViewLifecycleOwner(), successObs);
        add.observe(getViewLifecycleOwner(), addedObs);
    }


    @Override
    public void onResume() {
        super.onResume();

        spinner.setVisibility(View.VISIBLE);
        infoGrid.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(() -> reload(requireView(), getContext()), 100);
    }
}