package com.badgr.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.badgr.scoutClasses.scoutPerson;


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

    private void setLoggedInUser(scoutPerson p) { user = p; }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Result login(String username, String password) {
        //handles logout
        logout();
        //creates result object from datasource.login
        Result result = dataSource.login(username, password);


        //if user, login success and set this.user to result
        if (result instanceof Result.Success)
            setLoggedInUser(((Result.Success<scoutPerson>) result).getData());
        return result;
    }

    public static void logout() { user = null; }
}

