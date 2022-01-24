package Fragments.ScoutFrags.SearchFragmentDrivers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

public class SearchExpandListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableTitleList;
    private final ArrayList<meritBadge> badges;
    private static ArrayList<Integer> addedBadges;
    private static ArrayList<Integer> finishedBadges;
    private final scoutPerson user = LoginRepository.getUser();


    //Constructor
    public SearchExpandListAdapter(Context context, List<String> expandableListTitle,
                                   ArrayList<meritBadge> b) {
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        badges = b;
        //pulls the list of merit badges already added
        pullAddedBadges(user);
        pullFinishedBadges(user);
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
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.mini_search_badge_view, null);

        }

        //changes the mini_badge_view fields to match the respective merit badge
        TextView name = convertView.findViewById(R.id.badgeNameReplace);
        TextView eagleReq = convertView.findViewById(R.id.badgeIsEagleReplace);
        TextView addToList = convertView.findViewById(R.id.addToListText);
        CheckBox checkBox = convertView.findViewById(R.id.badgeChecked);

        //if the badge was already added before, check the box and change text, else set it to be available to be checked

        if (finishedBadges.contains(badge.getId()))
        {
            checkBox.setChecked(true);
            addToList.setText(R.string.completedBadge);
            checkBox.setEnabled(false);

        }
        else if (addedBadges.contains(badge.getId()))
        {
            checkBox.setChecked(true);
            addToList.setText(R.string.added);
            checkBox.setEnabled(true);
        }
        else
        {
            checkBox.setChecked(false);
            addToList.setText(R.string.toAdd);
            checkBox.setEnabled(true);
        }

        //sets a checkbox listener and updates the table if box is checked
        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();
            if (isChecked)
            {
                addToList.setText(R.string.added);
            }
            else
            {
                addToList.setText(R.string.toAdd);
            }
            //adds to list if checked, remove if unchecked
            SearchBadges.toggleAddToList(user, badge.getId(), isChecked);
        });



        //TODO image stuff
        ImageView image = convertView.findViewById(R.id.imageReplace);

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

    public static void pullAddedBadges(scoutPerson p)
    {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been already added to their list
        sTE.execute(() ->
                addedBadges = sqlRunner.addedBadges(p));

    }

    public static void pullFinishedBadges(scoutPerson p) {
        ExecutorService sTE = Executors.newSingleThreadExecutor();
        //gets which badges have been completed
        sTE.execute(() ->
                finishedBadges = sqlRunner.finishedBadgesInt(p));
    }

}
