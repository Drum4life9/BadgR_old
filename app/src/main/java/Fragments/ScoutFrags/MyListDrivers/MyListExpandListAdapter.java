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

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MyListExpandListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableTitleList;
    private final ArrayList<meritBadge> badges;
    private static HashMap<Integer, ArrayList<Integer>> finishedReq, changedReqs, deletedReqs;
    private static final scoutPerson user = LoginRepository.getUser();
    private static final CountDownLatch cdl = new CountDownLatch(1);


    //Constructor
    @SuppressWarnings("unchecked")
    public MyListExpandListAdapter(Context context, List<String> expandableListTitle,
                                   ArrayList<meritBadge> b) {
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        badges = b;
        pullFinishedReqs(user);
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        changedReqs = (HashMap<Integer, ArrayList<Integer>>) finishedReq.clone();
        deletedReqs = new HashMap<>();
    }


    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int lstPosn, int expanded_ListPosition) {
        return this.expandableTitleList.get(lstPosn);
    }

    @Override
    // Gets the ID for the given child within the given group.
    public long getChildId(int listPosition, int expanded_ListPosition) {
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
        ArrayList<Integer> pulledReqs = changedReqs.get(badge.getId());
        ArrayList<Integer> origReqs = finishedReq.get(badge.getId());
        CheckBox checkBox;


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.requirement_template, null);
            checkBox = convertView.findViewById(R.id.reqChecked);
            checkBox.setChecked(origReqs != null && origReqs.contains(expanded_ListPosition + 1));
        }
        else
        {
            checkBox = convertView.findViewById(R.id.reqChecked);
            checkBox.setChecked(pulledReqs != null && pulledReqs.contains(expanded_ListPosition + 1));
        }

        TextView description = convertView.findViewById(R.id.requirementReplace);
        checkBox = convertView.findViewById(R.id.reqChecked);



        //pulls requirement number
        int reqNum = expanded_ListPosition + 1;
        //gets the requirement string
        String desc = AllBadgeReqs.getBadgeReq(badge.getId(), reqNum);
        //sets the \n's in the string to whatever is needed to format description correctly
        if (desc != null) desc = desc.replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")));
        description.setText(desc);


        CheckBox finalCheckBox = checkBox;
        checkBox.setOnClickListener(v -> {
            boolean isChecked = finalCheckBox.isChecked();
            ArrayList<Integer> checkedList = changedReqs.get(badge.getId());
            ArrayList<Integer> deletedList = deletedReqs.get(badge.getId());
            if (deletedList == null)
            {
                deletedList = new ArrayList<>();
                deletedReqs.put(badge.getId(), deletedList);
            }
            if (checkedList == null)
            {
                checkedList = new ArrayList<>();
                changedReqs.put(badge.getId(), checkedList);
            }


            //if the box is checked
            if (isChecked) {
                //add to check list
                checkedList.add(reqNum);
                //remove from delete list
                deletedList.remove((Integer) reqNum);
            }
            //if the box is unchecked
            //------------------------------------------------------------------

            else {
                //add to delete list
                deletedList.add(reqNum);
                //remove from add list
                checkedList.remove((Integer) reqNum);
            }
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
        Thread t = new Thread(() ->
        {
            finishedReq = sqlRunner.finishedReqs(p);
            cdl.countDown();
        });
        t.start();
    }

    public static void updateRequirements()
    {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        sTE.execute(() -> {
            sqlRunner.toggleAddToReqList(user, changedReqs, deletedReqs);
            cdl.countDown();
        });
    }

    public static void resetCheckedReqs()
    {
        changedReqs.clear();
        deletedReqs.clear();
        changedReqs = copyFinished();

    }


    public static HashMap<Integer, ArrayList<Integer>> copyFinished()
    {
        HashMap<Integer, ArrayList<Integer>> copy = new HashMap<>();
            for (Map.Entry<Integer, ArrayList<Integer>> entry : finishedReq.entrySet()) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        return copy;
    }
}

