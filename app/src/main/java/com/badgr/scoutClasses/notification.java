package com.badgr.scoutClasses;

public class notification {
    private final scoutPerson person;
    private final meritBadge mb;
    private final int id;

    public notification(scoutPerson p, meritBadge b, int i)
    {
        person = p;
        mb = b;
        id = i;
    }

    public notification(scoutPerson p, int i)
    {
        person = p;
        mb = null;
        id = i;
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
}
