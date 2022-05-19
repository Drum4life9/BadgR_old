package Fragments.ScoutFrags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.badgr.R;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSearchExpandListAdapter extends BaseExpandableListAdapter {

    private static ArrayList<Integer> addedBadges, finishedBadges, addedBoxes, removedBoxes;
    private final Context context;
    private final List<String> expandableTitleList;
    private final ArrayList<meritBadge> badges;


    //Constructor
    public SSearchExpandListAdapter(Context context, List<String> expandableListTitle,
                                    ArrayList<meritBadge> b, scoutPerson u) {

        //sets class fields
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        badges = b;
        addedBoxes = new ArrayList<>();
        removedBoxes = new ArrayList<>();

        //pulls the list of merit badges already added
        pullAddedBadges(u);
        pullFinishedBadges(u);
    }

    public static void pullAddedBadges(scoutPerson p) {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been already added to their list
        sTE.execute(() ->
                addedBadges = sqlRunner.getAddedBadgesInt(p));

    }

    public static void pullFinishedBadges(scoutPerson p) {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been completed
        sTE.execute(() ->
                finishedBadges = sqlRunner.getCompletedBadgesInt(p));
    }

    public static ArrayList<Integer> getAddedBoxes() {
        return addedBoxes;
    }

    public static ArrayList<Integer> getRemovedBoxes() {
        return removedBoxes;
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

        //if row == null create new one
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.mini_search_badge_view, null);

        }

        //gets row elements
        TextView name = convertView.findViewById(R.id.badgeNameReplace);
        TextView eagleReq = convertView.findViewById(R.id.badgeIsEagleReplace);
        TextView addToList = convertView.findViewById(R.id.addToListText);
        CheckBox checkBox = convertView.findViewById(R.id.badgeChecked);
        ImageView image = convertView.findViewById(R.id.imageReplace);

        //sets badge id and image name
        int id = badge.getId();
        String badgeImageName = "merit_badge_" + badge.getStrippedName();


        //if the badge was already added before, check the box and change text, else set it to be available to be checked

        if (finishedBadges.contains(id)) {
            checkBox.setChecked(true);
            addToList.setText(R.string.completedBadge);
            checkBox.setEnabled(false);
        } else if (addedBadges.contains(id)) {
            checkBox.setChecked(true);
            addToList.setText(R.string.added);
            checkBox.setEnabled(true);
        } else {
            checkBox.setChecked(false);
            addToList.setText(R.string.toAdd);
            checkBox.setEnabled(true);
        }

        //if the badge is already added, set checked to true
        if (addedBoxes.contains(id)) {
            checkBox.setChecked(true);
            checkBox.setEnabled(true);
        }
        if (removedBoxes.contains(id)) {
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
        }

        //check box on click
        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();

            //update appropriate list
            if (addedBadges.contains(id) && !isChecked)
                removedBoxes.add(id);
            else if (!addedBadges.contains(id) && isChecked)
                addedBoxes.add(id);
            else if (addedBoxes.contains(id) && !isChecked)
                addedBoxes.remove((Integer) id);
            else
                removedBoxes.remove((Integer) id);
        });

        //set image to respective badge
        Context context = convertView.getContext();
        int imageId = context.getResources().getIdentifier(badgeImageName, "drawable", context.getPackageName());
        image.setImageResource(imageId);

        //sets mini_badge_view to the badge name
        name.setText(badge.getName());

        //sets the eagle text to if the badge is eagle required or not.
        if (badge.isEagle()) eagleReq.setText(R.string.eagleReq);
        else eagleReq.setText(R.string.NEagleReq);

        return convertView;
    }

    @Override
    // Gets the number of children in a specified group (always 1, the mini_badge_view)
    public int getChildrenCount(int listPosition) {
        return 1;
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

}
