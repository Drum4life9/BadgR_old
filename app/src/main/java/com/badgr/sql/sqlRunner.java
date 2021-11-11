package com.badgr.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

import com.badgr.scoutClasses.*;

public class sqlRunner {

    public static void addUser(scoutPerson p){

        String url = "jdbc:mysql://10.60.10.149:3306/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false";
        String username = "ScoutMAccount";
        String password = "ScoutMAccount1";

        Thread t = new Thread(() -> {
            try (Connection c = DriverManager.getConnection(url, username, password)) {
                try (Statement stmt = c.createStatement()){
                    String sd = "', '";                                                              //divider string is easier to call than type out
                    String id = ", ";
                    String query = "INSERT INTO scout VALUES (id, '" + p.getFName() + sd + p.getLName() + "', " + p.getAge() + id + p.getTroop() + id + "0);";
                    stmt.executeUpdate(query);
                }
                catch( SQLException ignored){}
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                e.printStackTrace();
            }
        });

        t.start();
    }
}
