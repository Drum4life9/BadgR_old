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

    private static ArrayList<Integer> addedScouts = new ArrayList<>();
    private static ArrayList<scoutPerson> scouts;
    private final Activity context;
    private final String[] sNames;

    public SMSearchScoutListAdapter(Activity context, String[] sList, scoutMaster u) {
        super(context, R.layout.scoutmaster_add_to_scouts, sList);
        this.context = context;
        sNames = sList;
        scouts = u.getTroop();
    }

    public static ArrayList<Integer> getAddedScouts() {
        return addedScouts;
    }

    public static void clearScouts() {
        addedScouts = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if row doesnt exist, create new
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.scoutmaster_add_to_scouts, null, true);

        //set row elements
        TextView scoutName = row.findViewById(R.id.scoutName);
        CheckBox checkBox = row.findViewById(R.id.scoutBox);

        //set scout name
        scoutName.setText(sNames[position]);

        //when checkbox is clicked
        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();
            //update added scouts accordingly
            if (isChecked) addedScouts.add(scouts.get(position).getUserID());
            else addedScouts.remove((Integer) scouts.get(position).getUserID());
        });

        return row;
    }

}
