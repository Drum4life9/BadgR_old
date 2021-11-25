package com.badgr.ui.register;


import androidx.lifecycle.ViewModel;

import android.util.Patterns;


public class RegisterViewModel extends ViewModel {


    public static boolean registerDataChanged(String fName, String lName, String user, String pass, String age, String troop) {
        return isNameValid(fName, lName) && isUserNameValid(user) && isPasswordValid(pass) && isAgeValid(Integer.parseInt(age)) && isTroopValid(Integer.parseInt(troop));
    }

    private static boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches() && username.contains("@");

    }

    private static boolean isPasswordValid(String password) {
        String specialChar = "@#$%&?";
        boolean validSC = false, validUP = false;
        for (int i = 0; i < password.length(); i++) {
            String c = password.substring(i, i + 1);
            if (specialChar.contains(c)) {validSC = true;}
            if (Character.isUpperCase(c.charAt(0))) {validUP = true;}
        }
        return password.length() > 8 && validSC && validUP;
    }

    private static boolean isAgeValid(int a) {
        return a > 0 && a < 120;
    }

    private static boolean isTroopValid(int t) {
        return t > 0 && t <= 9999;
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