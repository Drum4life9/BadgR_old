package Fragments.SMFrags;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.badgr.R;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;


import java.util.ArrayList;

public class SMSearchScoutListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] sNames;
    private static ArrayList<Integer> addedScouts = new ArrayList<>();
    private static ArrayList<scoutPerson> scouts;

    public SMSearchScoutListAdapter(Activity context, String[] sList, scoutMaster u) {
        super(context, R.layout.scoutmaster_add_to_scouts, sList);
        this.context = context;
        sNames = sList;
        scouts = u.getTroop();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.scoutmaster_add_to_scouts, null, true);
        TextView scoutName = row.findViewById(R.id.scoutName);
        CheckBox checkBox = row.findViewById(R.id.scoutBox);

        scoutName.setText(sNames[position]);

        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();
            if (isChecked) addedScouts.add(scouts.get(position).getUserID());
            else addedScouts.remove((Integer) scouts.get(position).getUserID());
        });

        return row;
    }

    public static ArrayList<Integer> getAddedScouts() {return addedScouts;}

    public static void clearScouts()
    {
        addedScouts = new ArrayList<>();
    }

}
