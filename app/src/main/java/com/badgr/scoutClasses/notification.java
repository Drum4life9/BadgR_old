package com.badgr.scoutClasses;

public class notification {
    private final scoutPerson person;
    private final meritBadge mb;
    private final int id;
    private final boolean newNot;

    public notification(scoutPerson p, meritBadge b, int i, boolean n)
    {
        person = p;
        mb = b;
        id = i;
        newNot = n;
    }

    public notification(scoutPerson p, int i, boolean n)
    {
        this(p, null, i, n);
    }


    public meritBadge getMb() {
        return mb;
    }

    public scoutPerson getPerson() {
        return person;
    }

    public int getId() {
        return id;
    }

    public boolean isNew() {return newNot;}
}
