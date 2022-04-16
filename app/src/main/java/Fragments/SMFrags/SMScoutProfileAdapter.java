package Fragments.SMFrags;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMScoutProfileAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] badgeNames;
    private final ArrayList<Integer> badgeIDs;
    private final int complIndex;
    private final HashMap<Integer, ArrayList<Integer>> compReqs;
    private final ArrayList<meritBadge> added;



    public SMScoutProfileAdapter(Activity context, String[] bList, int index, ArrayList<Integer> ids, HashMap<Integer, ArrayList<Integer>> reqs, ArrayList<meritBadge> add) {
        super(context, R.layout.list_group_titles_my_list, bList);
        this.context = context;
        badgeNames = bList;
        complIndex = index;
        badgeIDs = ids;
        compReqs = reqs;
        added = add;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.list_group_titles_my_list, null, true);

        TextView badgeName = row.findViewById(R.id.listTitle);
        TextView percent = row.findViewById(R.id.progressText);
        ProgressBar bar = row.findViewById(R.id.myProgressBar);

        badgeName.setText(badgeNames[position]);

        if (badgeIDs.size() == 0) return row;




        if (position >= complIndex)
        {
            String one = "100%";
            percent.setText(one);
            bar.setProgress(100);
        }
        else
        {
            int id = added.get(position).getId();
            ArrayList<Integer> reqs = compReqs.get(id);

            int size;
            if (reqs == null) size = 0;
            else size = reqs.size();

            int perc = (int) (size * 100.0 / added.get(position).getNumReqs());
            String percS = perc + "%";

            percent.setText(percS);
            bar.setProgress(perc);
        }






        return row;
    }
}
