package com.badgr.scoutPagesAndClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.badgr.scoutClasses.meritBadge;

public class ExpandableListDataItems {
    public static HashMap<String, List<String>> getData(ArrayList<meritBadge> mbs) {
        HashMap<String, List<String>> expandableDetailList = new HashMap<>();

        for (meritBadge b : mbs)
        {
            ArrayList<String> info = new ArrayList<>();
            String name = b.getName();
            String isEagle;
            if (b.isEagle()) isEagle = "true";
            else isEagle = "false";
            String reqs = Integer.toString(b.getNumReqs());
            String id = Integer.toString(b.getId());

            info.add(name);
            info.add(isEagle);
            info.add(reqs);
            info.add(id);
            expandableDetailList.put(name, info);
        }


        return expandableDetailList;
    }
}
