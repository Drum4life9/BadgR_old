package Fragments.ScoutFrags;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SWelcomeFragment extends Fragment {

    private final scoutPerson user;

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
        new Handler().postDelayed(() -> reload(view, getContext()), 150);
    }


    private void reload(View view, Context context)
    {
        TextView progress = view.findViewById(R.id.progressGrid);
        TextView compText = view.findViewById(R.id.completedGrid);
        TextView eagleText = view.findViewById(R.id.eagleGrid);

        ExecutorService STE = Executors.newSingleThreadExecutor();
        Future<ArrayList<meritBadge>> add = STE.submit(() -> sqlRunner.getAddedBadgesMB(user));

        ExecutorService STE2 = Executors.newSingleThreadExecutor();
        Future<ArrayList<meritBadge>> comp = STE2.submit(() -> sqlRunner.getCompletedBadges(user));

        String progressText, compS, eagleS;

        try {
            ArrayList<meritBadge> added = add.get(), completed = comp.get(), eagle = new ArrayList<>();

            for (meritBadge mb : added)
            {
                if (mb.isEagle()) eagle.add(mb);
            }

            progressText = added.size() + " badge(s)";
            compS = completed.size() + " badge(s)";
            eagleS = eagle.size() + " badge(s)";

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context, "A database error has occurred", Toast.LENGTH_LONG).show();
            progressText = "Error";
            compS = "Error";
            eagleS = "Error";
        }



        progress.setText(progressText);
        compText.setText(compS);
        eagleText.setText(eagleS);
    }


    @Override
    public void onResume() {
        super.onResume();
        reload(requireView(), getContext());
    }
}