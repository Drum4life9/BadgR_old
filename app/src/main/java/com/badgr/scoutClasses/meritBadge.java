package com.badgr.scoutClasses;

public class meritBadge {

    private String name;
    private boolean isEagle, hasEarned;
    private int numReq;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEagle() {
        return isEagle;
    }

    public void setEagle(boolean eagle) {
        isEagle = eagle;
    }

    public boolean isHasEarned() {
        return hasEarned;
    }

    public void setHasEarned(boolean hasEarned) {
        this.hasEarned = hasEarned;
    }

    public int getNumReq() {
        return numReq;
    }

    public void setNumReq(int numReq) {
        this.numReq = numReq;
    }
}
