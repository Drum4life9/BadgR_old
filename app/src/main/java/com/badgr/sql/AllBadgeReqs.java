package com.badgr.sql;

import android.os.Handler;

import java.sql.SQLException;
import java.util.HashMap;

public class AllBadgeReqs {
    public static HashMap<Integer, HashMap<Integer, String>> badgeRequirements = new HashMap<>();

    public AllBadgeReqs() {
        try {
            badgeRequirements = sqlRunner.getReqs();
        } catch (SQLException e) {
            new Handler().postDelayed(AllBadgeReqs::new, 1000);
        }
    }

    public static String getBadgeReq(int bID, int req) {
        HashMap<Integer, String> badge = badgeRequirements.get(bID);
        if (badge != null)
            return badge.get(req);
        return "No Req Found";
    }

}
