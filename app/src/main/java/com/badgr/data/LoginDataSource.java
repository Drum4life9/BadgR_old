package com.badgr.data;


import java.util.ArrayList;

import java.util.concurrent.CountDownLatch;

import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private ArrayList<String> returned;

    public Object login(String username, String password) {

        try {

            //---------------------------------------------------------Check username-------------------------------------------------//

            //creates a countDownLatch so that this thread is completed before anything else happens
            CountDownLatch userInDBCDL = new CountDownLatch(1);

            //thread to check username in database
            Thread userInDB = new Thread(() -> {
                sqlRunner.isUserInDatabase(username);
                userInDBCDL.countDown();

            });

            userInDB.start();

            //waits until thread has completed to move on
            try {
                userInDBCDL.await();
            } catch (InterruptedException e) {
                return new Result.Error("Error logging in");
            }

            //if the user is not in the database, return error message
            if (!sqlRunner.getUserInDB())
            {
                return new Result.Error("User does not exist. Please Register a new user");
            }

            //---------------------------------------------------------Check authentication-------------------------------------------------//

            //creates a countDownLatch so that this thread is completed before anything else happens
            CountDownLatch authCDL = new CountDownLatch(1);

            //thread to authenticate the user
            Thread authUser = new Thread(() -> {
                sqlRunner.authUser(username, password);
                authCDL.countDown();

            });

            authUser.start();

            //waits until thread has completed to move on
            try {
                authCDL.await();
            } catch (InterruptedException e) {
                return new Result.Error("Error logging in");
            }


            //if authentication was not successful, return error
            if (!sqlRunner.getAuthSuccess())
            {
                return new Result.Error("Password Incorrect. Please try again.");
            }

            //---------------------------------------------------------Pulls user info from database-------------------------------------------------//
            CountDownLatch loginCDL = new CountDownLatch(1);

            //thread to pull user info
            Thread loginT = new Thread(() -> {
                returned = sqlRunner.getUserInfo(username);
                loginCDL.countDown();

            });

            loginT.start();

            //waits until thread has completed to move on
            try {
                loginCDL.await();
            } catch (InterruptedException e) {
                return new Result.Error("Error logging in");
            }



            if (returned == null || returned.size() == 0)
            {
                return new Result.Error("Error logging in");
            }

            scoutPerson user = new scoutPerson(returned);


            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error("Error logging in");
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

}