package com.badgr.data;

import java.io.IOException;

import com.badgr.scoutClasses.scoutPerson;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Object login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
                scoutPerson masTest = new scoutPerson("Bill", "Henning", 45, 39, username, password);
            return new Result.Success<>(masTest);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}