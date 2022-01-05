package Fragments.ScoutFrags.MyListDrivers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.AllBadgeReqs;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MyListExpandListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableTitleList;
    private final ArrayList<meritBadge> badges;
    private static HashMap<Integer, ArrayList<Integer>> finishedReq;
    private final scoutPerson user = LoginRepository.getUser();


    //Constructor
    public MyListExpandListAdapter(Context context, List<String> expandableListTitle,
                                   ArrayList<meritBadge> b) {
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        badges = b;
        pullFinishedReqs(user);
    }


    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int lstPosn, int expanded_ListPosition) {
        pullFinishedReqs(user);
        return this.expandableTitleList.get(lstPosn);
    }

    @Override
    // Gets the ID for the given child within the given group.
    public long getChildId(int listPosition, int expanded_ListPosition) {
        pullFinishedReqs(user);
        return expanded_ListPosition;
    }

    //TODO return view here
    @SuppressLint("InflateParams")
    @Override
    // Gets a View that displays the data for the given child within the given group.
    public View getChildView(int lstPosn, final int expanded_ListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //gets the associated merit badge
        meritBadge badge = badges.get(lstPosn);
        ArrayList<Integer> pulledReqs = finishedReq.get(badge.getId());


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.requirement_template, null);

        }

        TextView description = convertView.findViewById(R.id.requirementReplace);
        CheckBox checkBox = convertView.findViewById(R.id.reqChecked);

        checkBox.setChecked(pulledReqs != null && pulledReqs.contains(expanded_ListPosition + 1));

        //pulls requirement number
        int reqNum = expanded_ListPosition + 1;
        //gets the requirement string
        String desc = AllBadgeReqs.getBadgeReq(badge.getId(), reqNum);
        //sets the \n's in the string to whatever is needed
        if (desc != null) desc = desc.replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")));
        description.setText(desc);



        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();
            //adds to list if checked, remove if unchecked
            ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
            singleThreadExecutor.execute(() -> sqlRunner.toggleAddToReqList(user, badge.getId(), reqNum, isChecked));

        });

        return convertView;
    }

    @Override
    // Gets the number of children in a specified group (number of reqs)
    public int getChildrenCount(int listPosition) {
        return badges.get(listPosition).getNumReqs();
    }

    @Override
    // Gets the data associated with the given group.
    public Object getGroup(int listPosition) {
        return this.expandableTitleList.get(listPosition);
    }

    @Override
    // Gets the number of groups
    public int getGroupCount() {
        return this.expandableTitleList.size();
    }

    @Override
    // Gets the ID for the group at the given position
    public long getGroupId(int listPosition) {
        return listPosition;
    }


    @SuppressLint("InflateParams")
    @Override
    // Gets a View that displays the given group.
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_search, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    // Indicates whether the child and group IDs are stable across changes to the underlying data.
    public boolean hasStableIds() {
        return false;
    }

    @Override
    // Whether the child at the specified position is selectable.
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public static void pullFinishedReqs(scoutPerson p) {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been completed
        sTE.execute(() ->
                finishedReq = sqlRunner.finishedReqs(p));
    }

}

