package Fragments.SMFrags;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMMyScoutsFragment extends Fragment {
    private final scoutMaster user;
    private final MutableLiveData<ArrayList<scoutPerson>> troop = new MutableLiveData<>();
    private ListView scoutsList;
    private ProgressBar pb;

    //sets user when fragment initialized
    public SMMyScoutsFragment(scoutMaster u) {
        user = u;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scoutmaster_fragment_my_scouts, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //gets page elements
        TextView noScouts = view.findViewById(R.id.noScouts);
        scoutsList = view.findViewById(R.id.scoutsList);
        pb = view.findViewById(R.id.progressBar);


        final Observer<ArrayList<scoutPerson>> troopObs = scoutPeople -> {

            noScouts.setVisibility(View.INVISIBLE);

            //delays 50ms to allow spinner to activate, then sets list
            new Handler().postDelayed(() -> {

                //sets names
                String[] names = setsNames();

                //if no scouts in troop, display text
                if (names.length < 1)
                    noScouts.setVisibility(View.VISIBLE);
                else {
                    //creates list and toggles visibilities
                    SMMyScoutsListAdapter adapter = new SMMyScoutsListAdapter(getActivity(), user, names, pb);
                    scoutsList.setAdapter(adapter);
                    scoutsList.setVisibility(View.VISIBLE);
                }
                pb.setVisibility(View.GONE);

            }, 100);
        };


        troop.observe(getViewLifecycleOwner(), troopObs);
    }

    @Override
    public void onResume() {
        super.onResume();
        pb.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            ExecutorService STE = Executors.newSingleThreadExecutor();
            STE.execute(() -> {
                try {
                    ArrayList<scoutPerson> people;
                    people = sqlRunner.getTroop(user);
                    troop.postValue(people);
                    user.setTroop(people);
                } catch (SQLException ignored) {
                }
            });
        }, 100);


    }

    private String[] setsNames() {
        //creates list of names
        String[] sNames = new String[Objects.requireNonNull(troop.getValue()).size()];
        int c = 0;
        for (scoutPerson p : user.getTroop()) {
            String name = p.getFullName();
            sNames[c] = name;
            c++;
        }

        return sNames;
    }

}
