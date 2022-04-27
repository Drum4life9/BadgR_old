package com.badgr.data;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Result login(String username, String password) {
        //---------------------------------------------------------Check authentication-------------------------------------------------//

        //thread to authenticate the user
        ExecutorService STE = Executors.newSingleThreadExecutor();
        Future<Boolean> auth = STE.submit(() -> sqlRunner.authUser(username, password));

        ExecutorService STE2 = Executors.newSingleThreadExecutor();
        Future<ArrayList<String>> info = STE2.submit(() -> sqlRunner.getUserInfo(username));

        //---------------------------------------------------------Pulls user info from database-------------------------------------------------//

        //thread to pull user info


        try {
            boolean b = auth.get();

            //if authentication was not successful, return error
            if (!b) {
                return new Result.Error("Email or Password Incorrect. Please try again.");
            } else {
                ArrayList<String> userInfo = info.get();

                if (userInfo != null) {
                    scoutPerson user = new scoutPerson(userInfo);
                    if (user.isSM()) {
                        user = new scoutMaster(userInfo);
                    }

                    return new Result.Success<>(user);
                }
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Result.Error("A connection or request error occurred. Please try again");
    }

}