package com.badgr.scoutClasses;

public class scoutPerson {

    private String fName, lName;
    private final String user, pass;
    private int age, troop;



    public scoutPerson(String fn, String ln, int a, int t, String u, String p) {
        fName = fn;
        lName = ln;
        age = a;
        troop = t;
        user = u;
        pass = p;

    }

    public scoutPerson() {
        this("defaultf", "defaultl", 0, 0, "user", "pass");
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String name) {
        this.fName = name;
    }

    public int getTroop() {
        return troop;
    }

    public void setTroop(int troop) {
        this.troop = troop;
    }


    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }
}
