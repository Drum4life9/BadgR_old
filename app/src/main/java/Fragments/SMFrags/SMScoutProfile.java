package Fragments.SMFrags;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SMScoutProfile extends AppCompatActivity {

    private static scoutPerson u;
    private static ArrayList<meritBadge> added, compl;
    private static HashMap<Integer, ArrayList<Integer>> reqs;
    private ListView mb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoutmaster_scout_profile_fragment);

        TextView nameRep = findViewById(R.id.nameReplace);
        TextView mbRep = findViewById(R.id.mbReplaceText);
        TextView noBadges = findViewById(R.id.noBadges);
        Button back = findViewById(R.id.backButton);
        mb = findViewById(R.id.mbList);
        ProgressBar pb = findViewById(R.id.loadingBar);


        String name = u.getFName() + " " + u.getLName();
        String title = name + "'s profile";
        String mbTitle = name + "'s merit badges";

        nameRep.setText(title);
        mbRep.setText(mbTitle);

        try {
            setReqs();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "An error occurred with database connection. Please exit and try again",Toast.LENGTH_LONG).show();
        }


        if (reqs == null) {
            noBadges.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            return;
        }

        SMScoutProfileAdapter adapter = new SMScoutProfileAdapter(this, getStrings(noBadges), added.size(), reqs, added);
        mb.setAdapter(adapter);

        pb.setVisibility(View.GONE);
        mb.setVisibility(View.VISIBLE);


        back.setOnClickListener(l -> finish());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mb = null;
    }

    private static void setReqs() throws ExecutionException, InterruptedException {
        ExecutorService ste = Executors.newSingleThreadExecutor();

        Future<ArrayList<meritBadge>> add = ste.submit(() -> sqlRunner.getAddedBadgesMB(u));
        Future<ArrayList<meritBadge>> comp = ste.submit(() -> sqlRunner.getFinishedBadges(u));
        Future<HashMap<Integer, ArrayList<Integer>>> req = ste.submit(() -> sqlRunner.getAddedAndFinishedReqs(u));

        added = add.get();
        compl = comp.get();
        reqs = req.get();

    }


    private static String[] getStrings(TextView noBadges)
    {

        String[] ret = new String[added.size() + compl.size()];

        if (ret.length == 0) noBadges.setVisibility(View.VISIBLE);
        else noBadges.setVisibility(View.GONE);

        int count = 0;

        for (meritBadge mb : added)
        {
            ret[count] = mb.getName();
            count++;
        }

        count = 0;

        for (int i = added.size(); i < added.size() + compl.size(); i++)
        {
            ret[i] = compl.get(count).getName();
            count++;
        }


        return ret;
    }

    public static void setU(scoutPerson user)
    {
        u = user;
    }



}
