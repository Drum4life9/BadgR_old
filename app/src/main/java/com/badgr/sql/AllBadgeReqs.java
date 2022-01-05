package com.badgr.sql;

import java.util.HashMap;

public class AllBadgeReqs {
    public static HashMap<Integer, HashMap<Integer, String>> badgeRequirements = new HashMap<>();

    public AllBadgeReqs()
    {
        badgeRequirements = sqlRunner.getReqs();
    }

    public static String getBadgeReq(int bID, int req)
    {
        HashMap<Integer, String> badge = badgeRequirements.get(bID);
        if (badge != null)
            return badge.get(req);
        return "No Req Found";
    }
}
