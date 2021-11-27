package com.badgr.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

import com.badgr.scoutClasses.*;

public class sqlRunner {
    //divider strings are easier to call than type out
    private final static String strd = "', '";
    private final static String intd = ", ";

    //sql connection strings
    private final static String url = "jdbc:mysql://192.168.1.39:3306/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&allowMultiQueries=true";
    private final static String username = "AppRunner";
    private final static String password = "AppRunner1";

    //error variables
    private static boolean authSuccess = false, registerSuccess = false, userInDB = false;

    public static void addUser(scoutPerson p) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            try (Statement stmt = c.createStatement()) {
                String addStmt = "INSERT INTO `users`.`userpass` VALUES (userPassID, '" + p.getPass() + "'); " +
                        "INSERT INTO `users`.`users` VALUES (userID, '" + p.getFName() + strd + p.getLName() + strd + p.getUser() + "', last_insert_id(), " +
                        p.getAge() + intd + p.isSM() + intd + p.getTroop() + "); " +
                        "INSERT INTO `users`.`troop` VALUES (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
                stmt.executeUpdate(addStmt);
                sqlRunner.setRegisterSuccess(true);
            } catch (SQLException ignored) {
                sqlRunner.setRegisterSuccess(false);
            }
        } catch (SQLException ignored) {
            sqlRunner.setRegisterSuccess(false);
        }
    }

    //TODO login user and get all info for loginRepo class

    public static void authUser(String givenU, String givenP) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);

            //pauses just a second to let database do its thing
            TimeUnit.SECONDS.sleep(1);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);


            boolean isFirst;
            int uPID = -1;

            //sets cursor at beginning of resultSet
            isFirst = rs.first();
            if (isFirst) {
                //if cursor is at beginning, get the user's userPassID
                uPID = rs.getInt(1);
            }
            else {
                //if not (or there's no data), success set to false
                sqlRunner.setAuthSuccess(false);
                //kills method
                return;
            }


            //just in case userPassID didn't update, kills method
            if (uPID < 0) {
                sqlRunner.setAuthSuccess(false);
                return;
            }


            String pass = "";

            //selects password from userpass database with the userPassID found from the username
            rs = stmt.executeQuery("SELECT pass FROM userpass WHERE userPassID = " + uPID + ";");
            //sets cursor at beginning, if error then success = false and kill method
            if (!rs.first()) {
                sqlRunner.setAuthSuccess(false);
                return;
            }
            else {
                //sets the pass string to the database password
                pass = rs.getString(1);
            }

            //sets the successful login if the username in database = username given, and same for password
            sqlRunner.setAuthSuccess(pass.equals(givenP)); //TODO and username stuff here


            } catch (SQLException | InterruptedException e) {
                sqlRunner.setAuthSuccess(false);
            }
    }

    public static boolean isUserInDatabase(String givenU) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);

            //pauses just a second to let database do its thing
            TimeUnit.SECONDS.sleep(1);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            //moves the cursor to the first person in the database and sets global variable userInDB true if username exists
            //if the user does not exists, then rs.first() is false so userInDB will be set to false
            sqlRunner.setUserInDB(rs.first());

        } catch (SQLException | InterruptedException e) {
            sqlRunner.setUserInDB(false);
        }
        return getUserInDB();
    }

    public static boolean getAuthSuccess() {return authSuccess;}

    public static void setAuthSuccess(boolean b)
    {
        sqlRunner.authSuccess = b;
    }

    public static boolean getRegisterSuccess() {
        return registerSuccess;
    }

    public static void setRegisterSuccess(boolean b) {
        sqlRunner.registerSuccess = b;
    }

    public static boolean getUserInDB() {
        return userInDB;
    }

    public static void setUserInDB(boolean userInDB) {
        sqlRunner.userInDB = userInDB;
    }
}
