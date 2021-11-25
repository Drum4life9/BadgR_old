package com.badgr.scoutClasses;

public class scoutPerson {

    private String fName, lName;
    private String user, pass;
    private int age, troop;
    private boolean isSM;



    public scoutPerson(String fn, String ln, String u, String p, int a, int t, boolean iSM) {
        fName = fn;
        lName = ln;
        age = a;
        troop = t;
        user = u;
        pass = p;
        isSM = iSM;

    }

    public scoutPerson() {
        this("defaultf", "defaultl", "user", "pass", 0, 0, false);
    }

    public int getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = Integer.parseInt(age);
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

    public void setTroop(String troop) { this.troop = Integer.parseInt(troop);}

    public void setUser(String user) {this.user = user;}

    public String getUser() {
        return user;
    }

    public void setPass(String pass) {this.pass = pass;}

    public String getPass() {
        return pass;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public boolean isSM() {
        return isSM;
    }

    public void setSM(boolean SM) {
        isSM = SM;
    }
}
