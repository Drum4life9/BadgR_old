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


public class LoginDataSource {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Result login(String username, String password) {

        //gets authentication success or failure
        ExecutorService STE = Executors.newSingleThreadExecutor();
        Future<Boolean> auth = STE.submit(() -> sqlRunner.authUser(username, password));

        //gets user info
        ExecutorService STE2 = Executors.newSingleThreadExecutor();
        Future<ArrayList<String>> info = STE2.submit(() -> sqlRunner.getUserInfo(username));


        try {
            //gets authentication result
            boolean b = auth.get();

            //if authentication was not successful, return error
            if (!b) {
                //return new error, either email or password incorrect
                return new Result.Error("Email or Password Incorrect. Please try again.");
            } else {
                //gets the user info
                ArrayList<String> userInfo = info.get();

                //if userInfo is not null (should never be)
                if (userInfo != null) {
                    //create new user
                    scoutPerson user = new scoutPerson(userInfo);
                    //if user is scoutmaster
                    if (user.isSM()) {
                        //create new scoutmaster
                        user = new scoutMaster(userInfo);
                    }

                    //return user as success
                    return new Result.Success<>(user);
                }
            }
            //if any errors occurred with future class
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //if connection error, return new Error
        return new Result.Error("A connection or request error occurred. Please try again");
    }

}