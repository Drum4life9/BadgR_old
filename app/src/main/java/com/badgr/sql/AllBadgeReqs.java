package com.badgr.sql;

import java.util.ArrayList;
import java.util.HashMap;

public class AllBadgeReqs {
    public static HashMap<Integer, HashMap<Integer, String>> badgeRequirements = new HashMap<>();

    public AllBadgeReqs() {
        badgeRequirements = sqlRunner.getReqs();
    }

    public static String getBadgeReq(int bID, int req) {
        HashMap<Integer, String> badge = badgeRequirements.get(bID);
        if (badge != null)
            return badge.get(req);
        return "No Req Found";
    }

    public static ArrayList<String> getBadgeReqs(int bID) {
        ArrayList<String> reqs = new ArrayList<>();
        HashMap<Integer, String> badge = badgeRequirements.get(bID);
        if (badge != null)
            for (int i = 0; i < badge.size(); i++)
                reqs.add(badge.get(i));


        return reqs;
    }
}
