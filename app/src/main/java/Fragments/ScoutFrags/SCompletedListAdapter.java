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

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SCompletedListAdapter extends ArrayAdapter<String> {
    private final String[] badgeNames;
    private final Activity context;
    private static ArrayList<Integer> checkedBoxes;
    private static ArrayList<meritBadge> compBadges;
    private static scoutPerson user;
    private final CountDownLatch cdl = new CountDownLatch(1);

    public SCompletedListAdapter(Activity context, String[] bList, scoutPerson u) {
        super(context, R.layout.completed_check_titles, bList);
        user = u;
        badgeNames = bList;
        this.context = context;
        checkedBoxes = new ArrayList<>();

        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() ->
        {
            compBadges = sqlRunner.getFinishedBadges(user);
            cdl.countDown();
        });

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.completed_check_titles, null, true);
        TextView badgeName = row.findViewById(R.id.CompletedLabel);
        CheckBox checkBox = row.findViewById(R.id.CompletedCheckBox);

        badgeName.setText(badgeNames[position]);

        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();
            try {
                cdl.await();
            } catch (InterruptedException e) {
                Toast.makeText(getContext(), "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isChecked) checkedBoxes.add(compBadges.get(position).getId());
            else checkedBoxes.remove((Integer) compBadges.get(position).getId());
        });

        return row;
    }


    public static ArrayList<Integer> getCheckedBoxes() {
        return checkedBoxes;
    }

}
