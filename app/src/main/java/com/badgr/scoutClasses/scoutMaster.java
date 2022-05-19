package com.badgr.scoutClasses;

import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class scoutMaster extends scoutPerson {
    //name, age, and troop methods provided

    private ArrayList<scoutPerson> troop;

    public scoutMaster() {
        this("defaultf", "defaultl", "user", "pass", 0, 0, 0);
    }

    public scoutMaster(String fn, String ln, String u, String p, int a, int t, int uID) {
        super(fn, ln, u, p, a, t, uID, true);
        getTroopMembers(1);
    }

    public scoutMaster(ArrayList<String> returned) {
        super(returned);
        troop = new ArrayList<>();
        getTroopMembers(1);
    }

    private void getTroopMembers(int count) {
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            try {
                troop = sqlRunner.getTroop(this);
            } catch (SQLException e) {
                if (count > 50)
                    troop = new ArrayList<>();
                else getTroopMembers(count + 1);
            }
        });
    }

    public ArrayList<scoutPerson> getTroop() {
        return troop;
    }

    public void setTroop(ArrayList<scoutPerson> trp) {
        troop = trp;
    }

}
