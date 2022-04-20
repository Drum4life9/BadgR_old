package Fragments.SMFrags;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMScoutProfile extends AppCompatActivity {

    private static scoutPerson u;
    private static final MutableLiveData<ArrayList<meritBadge>> added = new MutableLiveData<>();
    private static final MutableLiveData<ArrayList<meritBadge>> compl = new MutableLiveData<>();
    private static final MutableLiveData<ArrayList<Boolean>> trues = new MutableLiveData<>();
    private static final MutableLiveData<HashMap<Integer, ArrayList<Integer>>> reqs = new MutableLiveData<>();
    private static int count;
    private ListView mb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoutmaster_scout_profile_fragment);

        setReqs();


        TextView nameRep = findViewById(R.id.nameReplace);
        TextView mbRep = findViewById(R.id.mbReplaceText);
        Button back = findViewById(R.id.backButton);
        mb = findViewById(R.id.mbList);
        ProgressBar pb = findViewById(R.id.loadingBar);


        String name = u.getFName() + " " + u.getLName();
        String title = name + "'s profile";
        String mbTitle = name + "'s merit badges";

        nameRep.setText(title);
        mbRep.setText(mbTitle);


        final Observer<ArrayList<meritBadge>> gotBadges = meritBadges -> {
            ArrayList<Boolean> tru = trues.getValue();
            if (tru == null) tru = new ArrayList<>();
            tru.add(true);
            trues.postValue(tru);
            count++;
        };

        final Observer<ArrayList<meritBadge>> gotComp = meritBadges -> {
            ArrayList<Boolean> tru = trues.getValue();
            if (tru == null) tru = new ArrayList<>();
            tru.add(true);
            trues.postValue(tru);
            count++;
        };

        final Observer<HashMap<Integer, ArrayList<Integer>>> gotReqs = meritBadges -> {
            ArrayList<Boolean> tru = trues.getValue();
            if (tru == null) tru = new ArrayList<>();
            tru.add(true);
            trues.postValue(tru);
            count++;
        };

        final Observer<ArrayList<Boolean>> isTrue = booleans -> {
            if (count >= 3)
            {
                if (reqs.getValue() == null) return;
                ArrayList<Integer> ids = new ArrayList<>();
                if (added.getValue() == null) return;

                for (meritBadge badge : added.getValue())
                {
                    ids.add(badge.getId());
                }

                SMScoutProfileAdapter adapter = new SMScoutProfileAdapter(this, getStrings(), added.getValue().size(), ids, reqs.getValue(), added.getValue());
                mb.setAdapter(adapter);

                pb.setVisibility(View.GONE);
                mb.setVisibility(View.VISIBLE);
            }
        };


        added.observe(this, gotBadges);
        compl.observe(this, gotComp);
        reqs.observe(this, gotReqs);

        trues.observe(this, isTrue);



        back.setOnClickListener(l -> finish());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        trues.postValue(new ArrayList<>());
        count = 0;
        mb = null;
    }

    private static void setReqs()
    {
        ExecutorService ste = Executors.newSingleThreadExecutor();
        ste.execute(() ->
                {
                    compl.postValue(sqlRunner.getFinishedBadges(u));
                    added.postValue(sqlRunner.getAddedBadges(u));
                    reqs.postValue(sqlRunner.getAddedAndFinishedReqs(u));
                });
    }


    private static String[] getStrings()
    {
        ArrayList<meritBadge> completed = compl.getValue();
        ArrayList<meritBadge> inProgress = added.getValue();


        String[] ret = new String[inProgress.size() + completed.size()];
        int count = 0;

        for (meritBadge mb : inProgress)
        {
            ret[count] = mb.getName();
            count++;
        }

        count = 0;

        for (int i = inProgress.size(); i < inProgress.size() + completed.size(); i++)
        {
            ret[i] = completed.get(count).getName();
            count++;
        }



        return ret;
    }

    public static void setU(scoutPerson user)
    {
        u = user;
    }



}
