package Fragments.ScoutFrags;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SCompletedListAdapter extends ArrayAdapter<String> {
    private static ArrayList<Integer> checkedBoxes;
    private static ArrayList<meritBadge> compBadges;
    private static scoutPerson user;
    private final String[] badgeNames;
    private final Activity context;
    private final CountDownLatch cdl = new CountDownLatch(1);

    public SCompletedListAdapter(Activity context, String[] bList, scoutPerson u) {
        super(context, R.layout.completed_check_titles, bList);
        //sets user, badge names, context, and resets checked boxes
        user = u;
        badgeNames = bList;
        this.context = context;
        checkedBoxes = new ArrayList<>();


        //gets completed badges from database
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() ->
        {
            try {
                compBadges = sqlRunner.getCompletedBadges(user);
            } catch (SQLException ignored) {
            }
            cdl.countDown();
        });

    }

    //return which boxes are checked
    public static ArrayList<Integer> getCheckedBoxes() {
        return checkedBoxes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if theres no row, create new row
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.completed_check_titles, null, true);

        //set row elements
        TextView badgeName = row.findViewById(R.id.CompletedLabel);
        CheckBox checkBox = row.findViewById(R.id.CompletedCheckBox);

        //set name
        badgeName.setText(badgeNames[position]);

        //if box is checked
        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();

            //if databse connection was not made yet, toast error message
            try {
                cdl.await();
            } catch (InterruptedException e) {
                Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }

            //add or remove item to checked list if box is checked
            if (isChecked) checkedBoxes.add(compBadges.get(position).getId());
            else checkedBoxes.remove((Integer) compBadges.get(position).getId());
        });

        return row;
    }

}
