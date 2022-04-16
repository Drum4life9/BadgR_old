package Fragments.SMFrags;

import android.os.Bundle;
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
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.notification;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.Objects;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMRecentFragment extends Fragment {

    private static final MutableLiveData<ArrayList<notification>> nots = new MutableLiveData<>();
    private static final scoutMaster u = (scoutMaster) LoginRepository.getUser();
    private ListView lv;
    private ProgressBar bar;
    private TextView noNot;
    private Button clear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scoutmaster_fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.notList);
        bar = view.findViewById(R.id.notProgress);
        noNot = view.findViewById(R.id.noNots);
        clear = view.findViewById(R.id.clear);

        getNots();
        nots.observe(getViewLifecycleOwner(), notGot);

        clear.setOnClickListener(l ->
        {
            clearList(lv, noNot);
            Toast.makeText(getContext(), "Notifications cleared!", Toast.LENGTH_LONG).show();
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        lv.setVisibility(View.GONE);
        noNot.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNots();
    }

    private final Observer<ArrayList<notification>> notGot = notifications -> {
        ArrayList<notification> notList = nots.getValue();
        bar.setVisibility(View.GONE);
        if (notList == null) return;

        if (notList.size() == 0)
        {
            noNot.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            clear.setVisibility(View.INVISIBLE);
        }
        else
        {
            noNot.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

            SMRecentAdapter adpt = new SMRecentAdapter(getActivity(), getStrings(), notList);
            lv.setAdapter(adpt);
        }
    };


    private static void getNots()
    {
        ExecutorService ste = Executors.newSingleThreadExecutor();
        ste.execute(() -> nots.postValue(sqlRunner.getNotifications(u)));
    }

    private static String[] getStrings()
    {
        String[] strs = new String[Objects.requireNonNull(nots.getValue()).size()];
        int i = 0;

        for (notification n : nots.getValue())
        {
            if (n.getMb() == null)
            {
                strs[i] = n.getPerson().getFullName() + " has been added to your troop!";
            }
            else
            {
                strs[i] = n.getPerson().getFullName() + " has completed the " + n.getMb().getName() + " merit badge!";
            }
            i++;
        }

        return strs;
    }

    private static void clearList(ListView lv, TextView noNot)
    {
        ExecutorService ste = Executors.newSingleThreadExecutor();
        ste.execute(() -> sqlRunner.clearNots(Objects.requireNonNull(nots.getValue())));

        lv.setVisibility(View.GONE);
        noNot.setVisibility(View.VISIBLE);
    }

}
