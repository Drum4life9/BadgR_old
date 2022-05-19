package Fragments.SMFrags;

import android.content.Context;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMWelcomeFragment extends Fragment {

    private static final MutableLiveData<int[]> add = new MutableLiveData<>(), comp = new MutableLiveData<>();
    private static scoutMaster user;

    public SMWelcomeFragment(scoutMaster u) {
        user = u;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scoutmaster_fragment_welcome, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload(view, getContext());
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void reload(View view, Context context) {

        //get page elements
        TextView added = view.findViewById(R.id.progressGrid);
        TextView compText = view.findViewById(R.id.completedGrid);
        TextView eagleText = view.findViewById(R.id.eagleGrid);
        LinearLayout infoGrid = view.findViewById(R.id.infoGrid);
        ProgressBar pb = view.findViewById(R.id.spinner);

        //creates strings for grid use
        final String[] addedS = new String[1];
        final String[] compS = new String[1];
        final String[] eagleS = new String[1];

        //reset screen element visibilities
        infoGrid.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);

        //delay 200ms to let the spinner load
        new Handler().postDelayed(() -> {

            //get database connection and get added badges
            ExecutorService STE = Executors.newSingleThreadExecutor();
            STE.execute(() -> {
                try {
                    add.postValue(sqlRunner.getTotalAdded(user));
                } catch (SQLException ignored) {
                    Toast.makeText(context, "A database error has occurred", Toast.LENGTH_LONG).show();

                    addedS[0] = "Error";
                    compS[0] = "Error";
                    eagleS[0] = "Error";
                }
            });

        }, 200);

        //once database results come back
        final Observer<int[]> addedObs = ints -> {
            int[] vals = add.getValue();
            if (vals == null) vals = new int[3];

            //set strings accordingly
            addedS[0] = vals[0] + " badge(s)";
            compS[0] = vals[1] + " badge(s)";
            eagleS[0] = vals[2] + "/" + addedS[0];

            //set elements accordingly
            added.setText(addedS[0]);
            compText.setText(compS[0]);
            eagleText.setText(eagleS[0]);

            //reset screen elements
            infoGrid.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        };

        //when database results are returned, run addedObs
        add.observe(getViewLifecycleOwner(), addedObs);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onResume() {
        super.onResume();
        reload(requireView(), getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        requireView().findViewById(R.id.infoGrid).setVisibility(View.INVISIBLE);
        requireView().findViewById(R.id.spinner).setVisibility(View.VISIBLE);
    }
}
