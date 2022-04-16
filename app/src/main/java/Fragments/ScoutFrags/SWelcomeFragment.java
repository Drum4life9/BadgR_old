package Fragments.ScoutFrags;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SWelcomeFragment extends Fragment {

    private static final scoutPerson user = LoginRepository.getUser();
    private static ArrayList<meritBadge> added = new ArrayList<>();
    private static final MutableLiveData<ArrayList<meritBadge>> addedBadgesLive = new MutableLiveData<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getFinishedBadges();
        getAddedBadges();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scout_fragment_welcome, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTextTitles(view);

        final Observer<ArrayList<meritBadge>> badgeChanged = meritBadges -> {
            setTextTitles(view);
        };

        addedBadgesLive.observe(getViewLifecycleOwner(), badgeChanged);
    }



    private void setTextTitles(View view)
    {
        SSearchExpandListAdapter.pullAddedBadges(user);
        SSearchExpandListAdapter.pullFinishedBadges(user);
        ArrayList<Integer> completed = SSearchExpandListAdapter.getFinishedBadges();

        TextView progress = view.findViewById(R.id.progressGrid);
        TextView comp = view.findViewById(R.id.completedGrid);
        TextView eagle = view.findViewById(R.id.eagleGrid);

        String progressText = added.size() + " badge(s)";
        String compText = completed.size() + " badge(s)";
        final int[] eagleBadges = {0};

        for (int i : completed)
        {
            CountDownLatch cdl = new CountDownLatch(1);
            ExecutorService STE = Executors.newSingleThreadExecutor();
            STE.execute(() -> {
                if (Objects.requireNonNull(sqlRunner.getBadge(i)).isEagle()) eagleBadges[0]++;
                cdl.countDown();
            });
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String eagleText = eagleBadges[0] + " badge(s)";

        progress.setText(progressText);
        comp.setText(compText);
        eagle.setText(eagleText);
    }


    public static void getFinishedBadges() {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been completed
        sTE.execute(() ->
                addedBadgesLive.postValue(sqlRunner.getAddedBadges(user)));
    }

    public static void getAddedBadges() {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been completed
        sTE.execute(() ->
                added = sqlRunner.getAddedBadges(user));
    }

}