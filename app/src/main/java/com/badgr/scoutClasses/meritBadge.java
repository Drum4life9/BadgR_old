package com.badgr.scoutClasses;

import androidx.annotation.NonNull;

import java.util.Locale;

public class meritBadge implements Comparable<meritBadge> {

    private String name;
    private boolean isEagle;
    private int numReqs, id;


    public meritBadge() {
        this("defaultName", false, 0, 0);
    }

    public meritBadge(String n, boolean iE, int nR, int i) {
        name = n;
        isEagle = iE;
        numReqs = nR;
        id = i;
    }


    @NonNull
    public String toString() {
        return name + ": Is an eagle required? " + isEagle + ", and has " + numReqs + " requirements. It's ID is: " + id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrippedName() {
        return name.toLowerCase(Locale.ROOT).replaceAll(" ", "");
    }

    public boolean isEagle() {
        return isEagle;
    }

    public void setEagle(boolean eagle) {
        isEagle = eagle;
    }

    public int getNumReqs() {
        return numReqs;
    }

    public void setNumReqs(int numReq) {
        this.numReqs = numReq;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    @Override
    public int compareTo(meritBadge o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
