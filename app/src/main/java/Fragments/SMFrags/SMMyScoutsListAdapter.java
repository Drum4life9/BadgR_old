package Fragments.SMFrags;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badgr.R;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;

import java.util.ArrayList;

public class SMMyScoutsListAdapter extends ArrayAdapter<String> {

    private final Activity act;
    private static ArrayList<scoutPerson> scouts;
    private final ProgressBar spinner;

    public SMMyScoutsListAdapter(Activity context, scoutMaster u, String[] sList, ProgressBar spin) {
        super(context, R.layout.sm_scout_list_item, sList);
        this.act = context;
        scouts = u.getTroop();
        spinner = spin;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if row doesn't exist, create new
        View row = convertView;
        LayoutInflater inflater = act.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.sm_scout_list_item, null, true);

        //sets page elements
        TextView scoutName = row.findViewById(R.id.scoutName);
        Button profile = row.findViewById(R.id.profileButton);

        //gets name and set string to name
        String name = scouts.get(position).getFName() + " " + scouts.get(position).getLName();
        scoutName.setText(name);

        //profile button on click
        profile.setOnClickListener(l ->
                {
                    spinner.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> changeFragmentFromAdapter(act, scouts.get(position)), 100);
                });



        return row;
    }

    //opens profile
    public static void changeFragmentFromAdapter(Activity act, scoutPerson u)
    {
        SMScoutProfile.setU(u);

        Intent intent = new Intent(act, SMScoutProfile.class);
        act.startActivity(intent);


    }



}
