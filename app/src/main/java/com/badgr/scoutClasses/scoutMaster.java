package com.badgr.scoutClasses;

import java.util.ArrayList;

public class scoutMaster extends scoutPerson {
    //name, age, and troop methods provided

    private ArrayList<scout> scouts;

    public scoutMaster(){
        this("defaultf", "defaultl", 0, 0, "user", "pass");
    }

    public scoutMaster(String fn, String ln, int a, int t, String u, String p) {
        super(fn, ln, a, t, u, p);
        scouts = new ArrayList<scout>();
    }

    public void addScout(scout s) {scouts.add(s);}

    public void removeScout(scout s) {scouts.remove(s);}

    public scout getScoutByName(String fn, String ln) {
        for (scout s : scouts)
            if (s.getFName().equalsIgnoreCase(fn) && s.getLName().equalsIgnoreCase(ln)) return s;
        return null;
    }

}
