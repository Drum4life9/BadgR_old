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

        Thread passwordAdd = new Thread(() -> {
            try (Connection c = DriverManager.getConnection(url, username, password)) {
                try (Statement stmt = c.createStatement()) {
                    String addPass = "INSERT INTO `users`.`userpass` VALUES (userPassID, '" + p.getPass() + "'); " +
                            "INSERT INTO `users`.`users` VALUES (userID, '" + p.getFName() + strd + p.getLName() + strd + p.getUser() + "', last_insert_id(), " +
                            p.getAge() + intd + p.isSM() + intd +  p.getTroop() + "); " +
                            "INSERT INTO `users`.`troop` VALUES (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
                    System.out.println(addPass);
                    stmt.executeUpdate(addPass);
                    success.set(true);
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                success.set(false);

            }
        });

        passwordAdd.start();
        return success;
/**
  "INSERT INTO userpass VALUES (userPassID, '" + p.getPass() + "');" + "\n" +
                      "INSERT INTO users (`userID`, `firstName`, `lastName`, `username`, `age`, `isScoutmaster`, `UserPassID`, `troop`) " +
                              "VALUES (userID, '" + p.getFName() + strd + p.getLName() + strd + p.getUser() + "', " +
                              p.getAge() + intd + p.isSM() + intd + "last_insert_id(), " + p.getTroop() + ");" + "\n" +
                              "INSERT INTO troop VALUES (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
 **/
        /**
        Thread userAdd = new Thread(() -> {
            try (Connection c = DriverManager.getConnection(url, username, password)) {
                try (Statement stmt = c.createStatement()) {
                    System.out.println("attempt add user");
                    String addUser = "INSERT INTO user VALUES (userID, '" + p.getFName() + strd + p.getLName() + strd + p.getUser() + "', userPassID, " +
                            p.getAge() + intd + p.isSM() + intd + "last_insert_id(), " + p.getTroop() + ");";
                    stmt.executeUpdate(addUser);
                } catch (SQLException ignored) {
                    System.out.println("User error");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        userAdd.start();
        userAdd.interrupt();
        userAdd = null;

        Thread troopAdd = new Thread(() -> {
            try (Connection c = DriverManager.getConnection(url, username, password)) {
                try (Statement stmt = c.createStatement()) {
                    System.out.println("attempt add troop");
                    String addTroop = "INSERT INTO troop values (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
                    stmt.executeUpdate(addTroop);
                } catch (SQLException ignored) {
                    System.out.println("troop error");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        troopAdd.start();
        troopAdd.interrupt();
        troopAdd = null;
        **/
    }
}
