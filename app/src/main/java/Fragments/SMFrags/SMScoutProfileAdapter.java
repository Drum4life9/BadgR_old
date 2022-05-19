package Fragments.SMFrags;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;

import java.util.ArrayList;
import java.util.HashMap;

public class SMScoutProfileAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] badgeNames;
    private final int complIndex;
    private final HashMap<Integer, ArrayList<Integer>> compReqs;
    private final ArrayList<meritBadge> added;

    //constructor, sets class fields
    public SMScoutProfileAdapter(Activity context, String[] bList, int index, HashMap<Integer, ArrayList<Integer>> reqs, ArrayList<meritBadge> add) {
        super(context, R.layout.list_group_titles_my_list, bList);
        this.context = context;
        badgeNames = bList;
        complIndex = index;
        compReqs = reqs;
        added = add;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //if row doesn't exist, create new
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.list_group_titles_my_list, null, true);

        //sets row elements
        TextView badgeName = row.findViewById(R.id.listTitle);
        TextView percent = row.findViewById(R.id.progressText);
        ProgressBar bar = row.findViewById(R.id.myProgressBar);

        //set badge name from list
        badgeName.setText(badgeNames[position]);

        //if the position of badge is > the number of added badges (meaning its completed)
        if (position >= complIndex) {

            //set completed info
            String one = "Completed!!";
            percent.setText(one);
            bar.setVisibility(View.INVISIBLE);
        } else {

            //get badge
            int id = added.get(position).getId();
            ArrayList<Integer> reqs = compReqs.get(id);

            //set number of reqs completed
            int size;
            if (reqs == null) size = 0;
            else size = reqs.size();

            //create percent int and string
            int perc = (int) (size * 100.0 / added.get(position).getNumReqs());
            String percS = perc + "%";

            //set visibilities and properties
            bar.setVisibility(View.VISIBLE);
            percent.setText(percS);
            bar.setProgress(perc);

            //change the bar color to blue
            int blue = R.color.primary_blue;
            bar.setProgressTintList(ColorStateList.valueOf(getContext().getColor(blue)));
        }


        return row;
    }
}
