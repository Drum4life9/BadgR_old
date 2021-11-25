package com.badgr.data;

import java.io.IOException;

import com.badgr.scoutClasses.scoutPerson;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Object login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication, SEARCH SQL DATABASE HERE
                scoutPerson masTest = new scoutPerson("Bill", "Henning", username, password,  45, 39, true);
            return new Result.Success<>(masTest);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}