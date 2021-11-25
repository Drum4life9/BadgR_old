package com.badgr.ui.register;


import androidx.lifecycle.ViewModel;

import android.util.Patterns;


public class RegisterViewModel extends ViewModel {


    public static boolean registerDataChanged(String fName, String lName, String user, String pass, String age, String troop) {
        return isNameValid(fName, lName) && isUserNameValid(user) && isAgeValid(age) && isTroopValid(troop) &&
                passNumberValid(pass) && passLengthValid(pass) && passUpperValid(pass);
    }

    private static boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches() && username.contains("@");

    }

    public static boolean passUpperValid(String p) {
        for (int i = 0; i < p.length(); i++) {
            if (Character.isUpperCase(p.charAt(i))) {return true;}
        }
        return false;
    }

    public static boolean passNumberValid(String p) {
        for (int i = 0; i < p.length(); i++) {
            if (Character.isDigit(p.charAt(i))) {return true;}
        }
        return false;
    }

    public static boolean passLengthValid(String p) {
        return p.length() >= 8;
    }

    /**
    private static boolean isPasswordValid(String password) {
        //String specialChar = "@#$%&?";
        boolean validSC = false, validUP = false;
        for (int i = 0; i < password.length(); i++) {
            String c = password.substring(i, i + 1);
            //if (specialChar.contains(c)) {validSC = true;}
            if (Character.isUpperCase(c.charAt(0))) {validUP = true;}
        }

        return password.length() >= 8 && validUP; //validSC
    }
     **/

    private static boolean isAgeValid(String a) {
        int age;
        try {
            age = Integer.parseInt(a);
        }
        catch (NumberFormatException e)
        {
            age = -1;
        }
        return age > 0 && age <= 120;
    }

    private static boolean isTroopValid(String t) {
        int troop;
        try {
            troop = Integer.parseInt(t);
        }
        catch (NumberFormatException e)
        {
            troop = -1;
        }
        return troop > 0 && troop <= 9999;
    }

    private static boolean isNameValid(String fn, String ln) {
        String name = fn + " " + ln;
        for (int i = 0; i < name.length(); i++)
        {
            if (Character.isDigit(name.charAt(i))) {return false;}
        }
        return true;
    }
}