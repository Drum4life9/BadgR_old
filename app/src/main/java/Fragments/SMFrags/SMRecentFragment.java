package Fragments.SMFrags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.notification;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMRecentFragment extends Fragment {

    private static final MutableLiveData<ArrayList<notification>> nots = new MutableLiveData<>();
    private static scoutMaster user;
    private ListView lv;
    private ProgressBar bar;
    private TextView noNot;
    private Button clear;

    //constructor, sets user
    public SMRecentFragment(scoutMaster u) { user = u;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scoutmaster_fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //sets page elements
        lv = view.findViewById(R.id.notList);
        bar = view.findViewById(R.id.notProgress);
        noNot = view.findViewById(R.id.noNots);
        clear = view.findViewById(R.id.clear);
        Button allNotBut = view.findViewById(R.id.allNotButton);

        //get notifications
        getNots();

        //set observer for database results
        nots.observe(getViewLifecycleOwner(), notGot);

        //clear button on click
        clear.setOnClickListener(l -> {

            //clear list and toast message
            clearList(lv, noNot, requireContext());
            Toast.makeText(getContext(), "Notifications cleared!", Toast.LENGTH_LONG).show();
        });

        //all notifications button on click
        allNotBut.setOnClickListener(l -> {

            //toggle visibilities
            lv.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);

            //lets spinner load, gets notifications
            new Handler().postDelayed(() -> {
                ArrayList<notification> notList = nots.getValue();
                if (notList == null) {
                    Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_LONG).show();
                    return;
                }

                //create new activity with all notifications
                SMRecentAllNots.setUser(user);
                SMRecentAllNots.setNots(notList);
                changeFragmentFromAdapter(getActivity());
            }, 100);
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        //toggle visibilities
        lv.setVisibility(View.GONE);
        noNot.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        //toggle visibilities
        lv.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        noNot.setVisibility(View.GONE);

        //retrieve notifications
        getNots();
    }

    private final Observer<ArrayList<notification>> notGot = notifications -> {

        //gets notification list
        ArrayList<notification> notList = nots.getValue();
        ArrayList<notification> newNots = new ArrayList<>();

        //remove spinner
        bar.setVisibility(View.GONE);

        //if notifications == null (should never happen)
        if (notList == null) return;

        //if no notifications
        if (notList.size() == 0) {
            //set no not visibilty true
            noNot.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            clear.setVisibility(View.INVISIBLE);
        }
        else {

            //if notification is new, add to new list
            for (notification n : notList)
                if (n.isNew()) newNots.add(n);

            if (newNots.size() == 0){
                noNot.setVisibility(View.VISIBLE);
                lv.setVisibility(View.INVISIBLE);
                clear.setVisibility(View.INVISIBLE);
                return;
            }
            //toggle elements
            noNot.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);

            //create notification list
            SMRecentAdapter adpt = new SMRecentAdapter(getActivity(), getStrings(), newNots);
            lv.setAdapter(adpt);
        }
    };


    private static void getNots()
    {
        //database connection to get notifications
        ExecutorService ste = Executors.newSingleThreadExecutor();
        ste.execute(() -> nots.postValue(sqlRunner.getNotifications(user)));
    }


    private static String[] getStrings()
    {
        String[] strs = new String[Objects.requireNonNull(nots.getValue()).size()];
        int i = 0;

        //each notification
        for (notification n : nots.getValue())
        {
            //assign respective string if notification has a merit badge attached to it
            if (n.getMb() == null) strs[i] = n.getPerson().getFullName() + " has been added to your troop!";
            else strs[i] = n.getPerson().getFullName() + " has completed the " + n.getMb().getName() + " merit badge!";

            i++;
        }

        return strs;
    }

    private static void clearList(ListView lv, TextView noNot, Context c)
    {
        //tries to run SQl clear
        ExecutorService ste = Executors.newSingleThreadExecutor();
        ste.execute(() -> {
            try {
                sqlRunner.clearNots(Objects.requireNonNull(nots.getValue()));
            } catch (SQLException e) {
                Toast.makeText(c, "An error occurred, please try again.",Toast.LENGTH_LONG).show();
            }
        });

        //toggle page elements
        lv.setVisibility(View.GONE);
        noNot.setVisibility(View.VISIBLE);
    }

    public static void changeFragmentFromAdapter(Activity act)
    {
        //open new activity
        Intent intent = new Intent(act, SMRecentAllNots.class);
        act.startActivity(intent);
    }
}
