package com.badgr.scoutClasses;

import java.util.ArrayList;

public class scoutMaster extends scoutPerson {
    //name, age, and troop methods provided

    private ArrayList<scout> scouts;

    public scoutMaster(){
        this("defaultf", "defaultl","user", "pass", 0, 0, 0);
    }

    public scoutMaster(String fn, String ln, String u, String p, int a, int t, int uID) {
        super(fn, ln, u, p,  a, t, uID, true);
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
