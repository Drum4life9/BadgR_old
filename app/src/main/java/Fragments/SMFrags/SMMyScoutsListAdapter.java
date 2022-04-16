package Fragments.SMFrags;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.badgr.R;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;

import java.util.ArrayList;

public class SMMyScoutsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private static ArrayList<scoutPerson> scouts;

    public SMMyScoutsListAdapter(Activity context, scoutMaster u, String[] sList) {
        super(context, R.layout.sm_scout_list_item, sList);
        this.context = context;
        scouts = u.getTroop();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.sm_scout_list_item, null, true);
        TextView scoutName = row.findViewById(R.id.scoutName);
        Button profile = row.findViewById(R.id.profileButton);

        String name = scouts.get(position).getFName() + " " + scouts.get(position).getLName();
        scoutName.setText(name);


        profile.setOnClickListener(l ->
                changeFragmentFromAdapter(context, scouts.get(position)));


        return row;
    }

    public static void changeFragmentFromAdapter(Activity act, scoutPerson u)
    {

        SMScoutProfile.setU(u);
        Intent intent = new Intent(act, SMScoutProfile.class);
        act.startActivity(intent);
    }



}
