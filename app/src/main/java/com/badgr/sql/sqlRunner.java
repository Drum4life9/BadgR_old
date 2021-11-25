package com.badgr.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import com.badgr.scoutClasses.*;

public class sqlRunner {
    private final static String strd = "', '";                                                              //divider string is easier to call than type out
    private final static String intd = ", ";

    public static AtomicBoolean addUser(scoutPerson p) {

        AtomicBoolean success = new AtomicBoolean(false);
        String url = "jdbc:mysql://192.168.1.39:3306/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&allowMultiQueries=true";
        String username = "AppRunner";
        String password = "AppRunner1";

        Thread add = new Thread(() -> {
            try (Connection c = DriverManager.getConnection(url, username, password)) {
                try (Statement stmt = c.createStatement()) {
                    String addStmt = "INSERT INTO `users`.`userpass` VALUES (userPassID, '" + p.getPass() + "'); " +
                            "INSERT INTO `users`.`users` VALUES (userID, '" + p.getFName() + strd + p.getLName() + strd + p.getUser() + "', last_insert_id(), " +
                            p.getAge() + intd + p.isSM() + intd +  p.getTroop() + "); " +
                            "INSERT INTO `users`.`troop` VALUES (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
                    stmt.executeUpdate(addStmt);
                    success.set(true);
                } catch (SQLException ignored) {
                }
            } catch (SQLException e) {
                success.set(false);

            }
        });

        add.start();
        return success;
    }
}
