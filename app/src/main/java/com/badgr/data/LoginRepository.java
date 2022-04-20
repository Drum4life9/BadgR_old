package com.badgr.data;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.badgr.scoutClasses.scoutPerson;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LoginDataSource dataSource;

    private static scoutPerson user;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public static scoutPerson getUser() {
        return user;
    }

    private void setLoggedInUser(scoutPerson p) {
        user = p;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Result login(String username, String password) {
        //handles login
        Result result = dataSource.login(username, password);
        //creates result object

        //if user, login success
        if (result instanceof Result.Success)
            setLoggedInUser(((Result.Success<scoutPerson>) result).getData());
        return result;
    }

    public static void logout()
    {
        user = null;
    }
}

