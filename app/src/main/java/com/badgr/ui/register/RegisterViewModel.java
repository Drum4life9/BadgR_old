package com.badgr.ui.register;


import androidx.lifecycle.ViewModel;

import android.util.Patterns;


public class RegisterViewModel extends ViewModel {


    public static boolean registerDataChanged(String fName, String lName, String user, String pass, String age, String troop) {
        if (!isUserNameValid(user)) {
            return false;
        } else if (!isPasswordValid(pass)) {
            return false;
        }
        return true;
    }

    // A placeholder username validation check
    //TODO username stuff
    private static boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    //TODO password stuff
    private static boolean isPasswordValid(String password) {
        return password.equalsIgnoreCase("test");
    }

    //TODO Lots more checks
}