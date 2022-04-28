package Fragments.ScoutFrags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.AllBadgeReqs;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SMyListExpandListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableTitleList;
    private static ArrayList<meritBadge> badges;
    private static HashMap<Integer, ArrayList<Integer>> finishedReq, changedReqs, deletedReqs;
    private static scoutPerson user;
    private static CountDownLatch cdl = new CountDownLatch(1);


    //Constructor
    public SMyListExpandListAdapter(Context context, List<String> expandableListTitle,
                                    ArrayList<meritBadge> b, scoutPerson u) throws ExecutionException, InterruptedException {
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        user = u;
        badges = b;

        ExecutorService STE = Executors.newSingleThreadExecutor();
        Future<HashMap<Integer, ArrayList<Integer>>> finReq = STE.submit(() -> sqlRunner.getCompletedReqs(user));

        finishedReq = finReq.get();
        changedReqs = copyFinished();
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
        } else {
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
        if (desc != null)
            desc = desc.replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")));
        description.setText(desc);


        CheckBox finalCheckBox = checkBox;
        checkBox.setOnClickListener(v -> {
            boolean isChecked = finalCheckBox.isChecked();
            ArrayList<Integer> checkedList = changedReqs.get(badge.getId());
            ArrayList<Integer> deletedList = deletedReqs.get(badge.getId());
            if (deletedList == null) {
                deletedList = new ArrayList<>();
                deletedReqs.put(badge.getId(), deletedList);
            }
            if (checkedList == null) {
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
        meritBadge badge = badges.get(listPosition);
        ArrayList<Integer> pulledReqs = finishedReq.get(badge.getId());
        if (pulledReqs == null) pulledReqs = new ArrayList<>();
        String listTitle = (String) getGroup(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_titles_my_list, null);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        ProgressBar progress = convertView.findViewById(R.id.myProgressBar);
        TextView progressText = convertView.findViewById(R.id.progressText);

        int numReqs = badge.getNumReqs();
        int compReqs = pulledReqs.size();
        int percent = (int) (compReqs * 1.0 / numReqs * 100);
        String percentText = percent + "%";

        progress.setProgress(percent);
        progressText.setText(percentText);

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
        //gets which requirements have been completed
        ExecutorService STE = Executors.newSingleThreadExecutor();
        Future<HashMap<Integer, ArrayList<Integer>>> finReq = STE.submit(() -> sqlRunner.getCompletedReqs(p));

        try {
            finishedReq = finReq.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void updateRequirements() throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        sTE.execute(() ->
                {
                    try {
                        sqlRunner.toggleAddToReqList(user, changedReqs, deletedReqs);
                        cdl.countDown();
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }
                });
        cdl.await();
    }

    //clears all of the checked reqs since submit button was clicked
    public static void resetCheckedReqs() {
        if (changedReqs == null || deletedReqs == null) return;
        changedReqs.clear();
        deletedReqs.clear();
        changedReqs = copyFinished();

    }


    public static HashMap<Integer, ArrayList<Integer>> copyFinished() {
        HashMap<Integer, ArrayList<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : finishedReq.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }


    public static ArrayList<Integer> checkCompletedBadges() {
        //sets new arrayList for completed integer ids of badges
        ArrayList<Integer> completedBadges = new ArrayList<>();
        Set<Integer> set = changedReqs.keySet();
        //for int in changedReqs
        for (int i : set) {

            boolean comp = true;
            ArrayList<Integer> completedReqs = changedReqs.get(i);

            cdl = new CountDownLatch(1);

            int reqNumber = 0;
            for (meritBadge mb : badges)
            {
                if (mb.getId() == i)
                    reqNumber = mb.getNumReqs();
            }

            if (completedReqs == null || completedReqs.size() != reqNumber) continue;

            //if any requirements are not completed, skip and move to next merit badge
            for (int req = 0; req < completedReqs.size(); req++) {
                if (completedReqs.get(req) == 0)
                {
                    comp = false;
                    break;
                }
            }

            //if comp remains true, add it to completed badges
            if (comp) {
                completedBadges.add(i);
            }
        }



        //removes any completed badges from list
        for (int i : completedBadges) {
            ExecutorService STE = Executors.newSingleThreadExecutor();
            cdl = new CountDownLatch(1);
            STE.execute(() ->
            {
                sqlRunner.setBadgeCompleted(user, i);
                cdl.countDown();
                sqlRunner.addNewNot(user, i);
            });

            finishedReq.remove(i);
            changedReqs.remove(i);

            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ExecutorService STE = Executors.newSingleThreadExecutor();
        Future<HashMap<Integer, ArrayList<Integer>>> finReq = STE.submit(() -> sqlRunner.getCompletedReqs(user));

        try {
            finishedReq = finReq.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        copyFinished();
        finishedReq.clear();
        changedReqs.clear();


        return completedBadges;
    }

}

