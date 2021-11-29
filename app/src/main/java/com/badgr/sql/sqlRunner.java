package com.badgr.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.badgr.scoutClasses.*;

public class sqlRunner {
    //divider strings are easier to call than type out
    private final static String strDivider = "', '";
    private final static String intDivider = ", ";

    //sql connection strings
    private final static String url = "jdbc:mysql://192.168.1.39:3306/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&allowMultiQueries=true&connectRetryInterval=1";
    private final static String username = "AppRunner";
    private final static String password = "AppRunner1";

    //error variables
    private static boolean authSuccess = false, registerSuccess = false, userInDB = false;

    public static void addUser(scoutPerson p) {
        registerSuccess = false;

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();
            String addStmt = "INSERT INTO userpass VALUES (userPassID, '" + p.getPass() + "'); " +
                    "INSERT INTO users VALUES (userID, '" + p.getFName() + strDivider + p.getLName() + strDivider + p.getUser() + "', last_insert_id(), " +
                    p.getAge() + intDivider + p.isSM() + intDivider + p.getTroop() + "); " +
                    "INSERT INTO troop VALUES (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
            stmt.executeUpdate(addStmt);
            registerSuccess = true;
        } catch (SQLException e) {
            e.printStackTrace();
            registerSuccess = false;
        }

    }

    //TODO login user and get all info for loginRepo class

    public static ArrayList<String> getUserInfo(String givenU)
    {
        ArrayList<String> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT * FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);

            //pauses just a second to let database do its thing
            TimeUnit.SECONDS.sleep(1);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            rs.first();

            int id = rs.getInt("userID");
            retList.add(String.valueOf(id));
            retList.add(rs.getString("firstName"));
            retList.add(rs.getString("lastName"));
            retList.add(rs.getString("username"));
            int uPID = rs.getInt("userPassID");
            retList.add(String.valueOf(uPID));
            int age = rs.getInt("age");
            retList.add(String.valueOf(age));
            int isSM = rs.getInt("isScoutmaster");
            retList.add(String.valueOf(isSM));
            int troop = rs.getInt("troop");
            retList.add(String.valueOf(troop));


        } catch (SQLException | InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }

        return retList;
    }



    public static void authUser(String givenU, String givenP) {
        authSuccess = false;
        
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);

            //pauses just a second to let database do its thing
            TimeUnit.SECONDS.sleep(1);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);


            boolean isFirst;
            int uPID;

            //sets cursor at beginning of resultSet
            isFirst = rs.first();
            if (isFirst) {
                //if cursor is at beginning, get the user's userPassID
                uPID = rs.getInt(1);
            }
            else {
                //if not (or there's no data), success set to false
                //kills method
                return;
            }


            //just in case userPassID didn't update, kills method
            if (uPID < 0) {
                return;
            }


            String pass;

            //selects password from userpass database with the userPassID found from the username
            rs = stmt.executeQuery("SELECT pass FROM userpass WHERE userPassID = " + uPID + ";");
            //sets cursor at beginning, if error then success = false and kill method
            if (!rs.first()) {
                return;
            }
            else {
                //sets the pass string to the database password
                pass = rs.getString(1);
            }

            //sets the successful login if the username in database = username given, and same for password
            authSuccess = pass.equals(givenP); //TODO and username stuff here


            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static boolean isUserInDatabase(String givenU) {
        userInDB = false;
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
            userInDB = rs.first();

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            userInDB = false;
        }
        return userInDB;
    }



    public static boolean getAuthSuccess() {return authSuccess;}

    public static boolean getRegisterSuccess() { return registerSuccess; }

    public static boolean getUserInDB() { return userInDB; }

}
