package Fragments.ScoutFrags;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.lang.reflect.Array;
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
    private static final scoutPerson user = LoginRepository.getUser();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getFinishedBadges();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_completed_badges, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(R.id.completedListView);
        Button edit = view.findViewById(R.id.CompletedEdit);
        Button remove = view.findViewById(R.id.CompletedRemoveButton);
        TextView none = view.findViewById(R.id.CompletedTextNone);

        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {
            completedBadges = completedBadgesLive.getValue();
            if (completedBadges == null || completedBadges.size() == 0)
            {
                edit.setVisibility(View.GONE);
                none.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
                return;
            }
            else
            {
                none.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
            }

            //sorts Merit Badges by title
            Collections.sort(completedBadges, Comparator.comparing(meritBadge::getName));
            //creates a new list for the badge titles
            compTitles = new String[completedBadges.size()];

            resetList(view);
        };

        completedBadgesLive.observe(getViewLifecycleOwner(), badgeChanged);

        edit.setOnClickListener(v -> {
            if (completedBadgesLive.getValue() == null || completedBadgesLive.getValue().size() == 0)
            {
                Toast.makeText(getContext(), "No badges completed!", Toast.LENGTH_SHORT).show();
                return;
            }

            list = null;

            list = view.findViewById(R.id.completedListView);

            SCompletedListAdapter adapter = new SCompletedListAdapter(getActivity(), compTitles, user);
            list.setAdapter(adapter);

            remove.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
        });

        remove.setOnClickListener(v -> {
            ArrayList<Integer> badges = SCompletedListAdapter.getCheckedBoxes();
            //hides the remove button
            remove.setVisibility(View.GONE);

            if (badges != null && badges.size() != 0){
                //connects to SQL and updates badges
                ExecutorService STE = Executors.newSingleThreadExecutor();
                STE.execute(() ->
                        sqlRunner.removeCompleted(badges, user));
                SMyListExpandListAdapter.pullFinishedReqs(user);
                Toast.makeText(getContext(), "Badges Removed", Toast.LENGTH_LONG).show();
            }
            if (completedBadges == null || completedBadges.size() == 0)
            {
                list.setVisibility(View.GONE);
            }
            resetList(view);
        });

    }


    public static void getFinishedBadges()
    {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been completed
        sTE.execute(() ->
                completedBadgesLive.postValue(sqlRunner.getFinishedBadges(user)));
    }

    private void setTitles()
    {
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

        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
    }
}