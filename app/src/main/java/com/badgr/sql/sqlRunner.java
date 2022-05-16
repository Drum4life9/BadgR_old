package com.badgr.sql;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.notification;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import Fragments.SMFrags.SMSearchBadgesFragment;
import Fragments.ScoutFrags.SCompletedBadges;
import Fragments.ScoutFrags.SMyListFragment;
import Fragments.ScoutFrags.SSearchExpandListAdapter;

public class sqlRunner {
    //divider strings are easier to call than type out
    private final static String strDiv = "', '";
    private final static String intDiv = ", ";

    //sql connection strings
    private final static String url = "jdbc:mysql://192.168.0.197/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&allowMultiQueries=true&connectRetryInterval=10&connectTimeout=10000";
    private final static String username = "AppRunner";
    private final static String password = "AppRunner1";


    public static boolean addUser(scoutPerson p) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();
            int isSM;
            if (p.getAge() >= 18) isSM = 1;
            else isSM = 0;


            String addStmt = "INSERT INTO `userpass`(`pass`) VALUES ('" + p.getPass() + "'); " +
                    "INSERT INTO `users`(`firstName`, `lastName`, `email`, `age`, `isScoutmaster`, `troop`) VALUES ('" + p.getFName() + strDiv + p.getLName() + strDiv + p.getEmail() + "', " +
                    p.getAge() + intDiv + isSM + intDiv + p.getTroopNum() + "); ";
            stmt.executeUpdate(addStmt);

            scoutPerson newP = new scoutPerson(Objects.requireNonNull(getUserInfo(p.getEmail())));
            addNewNot(newP, -1);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> getUserInfo(String givenU) {
        ArrayList<String> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds everything with the given username
            String ex = "SELECT * FROM users WHERE email = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            rs.first();

            int id = rs.getInt("userID");
            retList.add(String.valueOf(id));
            retList.add(rs.getString("firstName"));
            retList.add(rs.getString("lastName"));
            retList.add(rs.getString("email"));
            int age = rs.getInt("age");
            retList.add(String.valueOf(age));
            int isSM = rs.getInt("isScoutmaster");
            retList.add(String.valueOf(isSM));
            int troop = rs.getInt("troop");
            retList.add(String.valueOf(troop));


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static ArrayList<String> getUserInfo(int userId) {
        ArrayList<String> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds everything with the given userID
            String ex = "SELECT * FROM users WHERE userID = '" + userId + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            rs.first();

            int id = rs.getInt("userID");
            retList.add(String.valueOf(id));
            retList.add(rs.getString("firstName"));
            retList.add(rs.getString("lastName"));
            retList.add(rs.getString("email"));
            int age = rs.getInt("age");
            retList.add(String.valueOf(age));
            int isSM = rs.getInt("isScoutmaster");
            retList.add(String.valueOf(isSM));
            int troop = rs.getInt("troop");
            retList.add(String.valueOf(troop));


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static boolean authUser(String givenU, String givenP) throws SQLException {
        Connection c = DriverManager.getConnection(url, username, password);
        //Finds userPassID with the given username
        String ex = "SELECT userID FROM users WHERE email = '" + givenU + "'";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);


        boolean isFirst;
        int uPID;

        //sets cursor at beginning of resultSet
        isFirst = rs.first();
        if (isFirst) {
            //if cursor is at beginning, get the user's userPassID
            uPID = rs.getInt(1);
        } else {
            //if not (or there's no data), kills method
            return false;
        }


        //just in case userPassID didn't update, kills method
        if (uPID < 0) {
            return false;
        }

        String pass;

        //selects password from userpass database with the userPassID found from the username
        rs = stmt.executeQuery("SELECT pass FROM userpass WHERE userPassID = " + uPID + ";");
        //sets cursor at beginning, if error then kill method
        if (!rs.first()) {
            return false;
        } else {
            //sets the pass string to the database password
            pass = rs.getString(1);
        }

        //sets the successful login if the username in database = username given, and same for password
        return pass.equals(givenP);
    }

    public static boolean isEmailInDatabase(String givenU) {
        boolean userInDB;
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE email = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            Thread.sleep(1000);

            //moves the cursor to the first person in the database and sets global variable userInDB true if username exists
            //if the user does not exists, then rs.first() is false so userInDB will be set to false
            userInDB = rs.first();

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            userInDB = false;
        }
        return userInDB;
    }

    public static ArrayList<meritBadge> searchForBadges(String bName) {
        ArrayList<meritBadge> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM badgetable where badgeName LIKE '%" + bName + "%';";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            while (rs.next()) {
                meritBadge mb = new meritBadge();
                mb.setName(rs.getString("badgeName"));
                mb.setEagle(rs.getInt("isEagleReq") == 1);
                mb.setNumReqs(rs.getInt("numReqs"));
                mb.setId(rs.getInt("badgeTableID"));
                retList.add(mb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static meritBadge getBadge(int badgeID) {
        meritBadge mb = new meritBadge();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM badgetable where badgeTableID = " + badgeID + ";";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            while (rs.next()) {
                mb.setName(rs.getString("badgeName"));
                mb.setEagle(rs.getInt("isEagleReq") == 1);
                mb.setNumReqs(rs.getInt("numReqs"));
                mb.setId(rs.getInt("badgeTableID"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return mb;
    }

    public static void toggleAddToList(scoutPerson p, ArrayList<Integer> added, ArrayList<Integer> removed) throws SQLException {
        Connection c = DriverManager.getConnection(url, username, password);
        String addBadge, addReq, removeBadge;


        addBadge = "INSERT INTO userbadges VALUES (?, ?, 0)";
        PreparedStatement badgePS = c.prepareStatement(addBadge);

        addReq = "INSERT INTO userreq VALUES (?, ?, ?, 0);";
        PreparedStatement addPS = c.prepareStatement(addReq);

        removeBadge = "DELETE FROM userbadges WHERE badgeTableID = ?;";
        PreparedStatement remPS = c.prepareStatement(removeBadge);


        for (int i : added) {
            badgePS.setInt(1, i);
            badgePS.setInt(2, p.getUserID());
            badgePS.addBatch();

            for (int j = 1; j <= Objects.requireNonNull(getBadge(i)).getNumReqs(); j++) {
                addPS.setInt(1, p.getUserID());
                addPS.setInt(2, i);
                addPS.setInt(3, j);
                addPS.addBatch();
            }
        }

        for (int i : removed) {
            remPS.setInt(1, i);
            remPS.addBatch();
        }


        badgePS.executeBatch();
        addPS.executeBatch();
        remPS.executeBatch();


        //update checked boxes for when the children of the expandable list are created
        SSearchExpandListAdapter.pullAddedBadges(p);

    }

    public static void toggleAddToReqList(scoutPerson p, HashMap<Integer, ArrayList<Integer>> addReqsMap, HashMap<Integer, ArrayList<Integer>> delReqsMap) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //for each set in the map
            for (Map.Entry<Integer, ArrayList<Integer>> entry : addReqsMap.entrySet()) {

                //get badgeID because it is key
                int badgeID = entry.getKey();
                //gets changed requirements
                ArrayList<Integer> addedReqs = entry.getValue();


                String updateReq;
                Statement stmtAddOrRemove = c.createStatement();

                for (int i : addedReqs) {
                    //add to table
                    updateReq = "UPDATE userreq SET isCompleted = 1 WHERE userID = " + p.getUserID() + " AND badgeTableID = " + badgeID + " AND reqNum = " + i + ";";
                    stmtAddOrRemove.executeUpdate(updateReq);
                }
            }


            for (Map.Entry<Integer, ArrayList<Integer>> entryDel : delReqsMap.entrySet()) {
                int badgeIDDel = entryDel.getKey();
                ArrayList<Integer> requirementsDel = entryDel.getValue();

                String updateReq;
                Statement stmtAddOrRemove = c.createStatement();
                for (int reqNum : requirementsDel) {
                    //remove the badge req
                    updateReq = "UPDATE userreq SET isCompleted = 0 WHERE userID = " + p.getUserID() + " AND badgeTableID = " + badgeIDDel + " AND reqNum = " + reqNum + ";";
                    stmtAddOrRemove.executeUpdate(updateReq);

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> getAddedBadgesInt(scoutPerson p) {
        ArrayList<Integer> added = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = 0 ORDER BY badgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("badgeTableID");
                added.add(badgeIDAdded);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return added;
    }

    public static ArrayList<meritBadge> getAddedBadgesMB(scoutPerson p) throws SQLException {
        ArrayList<meritBadge> added = new ArrayList<>();

        Connection c = DriverManager.getConnection(url, username, password);
        //Finds badge names with the given name

        String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = 0 ORDER BY badgeTableID;";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);
        while (rs.next()) {
            int badgeIDAdded = rs.getInt("badgeTableID");
            added.add(getBadge(badgeIDAdded));
        }

        return added;
    }

    public static ArrayList<meritBadge> getAddedBadgesMB(int userID) throws SQLException {
        ArrayList<meritBadge> added = new ArrayList<>();

        Connection c = DriverManager.getConnection(url, username, password);
        //Finds badge names with the given name

        String ex = "SELECT * FROM userbadges WHERE userID = " + userID + " AND isCompleted = 0 ORDER BY badgeTableID;";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);
        while (rs.next()) {
            int badgeIDAdded = rs.getInt("badgeTableID");
            added.add(getBadge(badgeIDAdded));
        }


        return added;
    }

    public static void setBadgeCompleted(scoutPerson p, int i) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            String updateCompletion;
            Statement stmt = c.createStatement();

            updateCompletion = "UPDATE userbadges SET isCompleted = 1 WHERE userID = " + p.getUserID() + " AND badgeTableID = " + i + ";";
            stmt.executeUpdate(updateCompletion);

            updateCompletion = "DELETE FROM userreq where userID = " + p.getUserID() + " AND badgeTableID = " + i + ";";
            stmt.executeUpdate(updateCompletion);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> getCompletedBadgesInt(scoutPerson p) {
        ArrayList<Integer> completed = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY badgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("badgeTableID");
                completed.add(badgeIDAdded);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        SCompletedBadges.getFinishedBadges(p);

        return completed;
    }

    public static ArrayList<meritBadge> getCompletedBadges(scoutPerson p) throws SQLException {
        ArrayList<meritBadge> badges = new ArrayList<>();
        Connection c = DriverManager.getConnection(url, username, password);
        //Finds badge names with the given name and completed
        String ex = "SELECT badgeTableID FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY badgeTableID;";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);
        while (rs.next()) {
            int badgeIDAdded = rs.getInt("badgeTableID");
            meritBadge mb = getBadge(badgeIDAdded);
            badges.add(mb);
        }


        return badges;
    }

    public static ArrayList<meritBadge> getCompletedBadges(int id) throws SQLException {
        ArrayList<meritBadge> badges = new ArrayList<>();
        Connection c = DriverManager.getConnection(url, username, password);
        //Finds badge names with the given name and completed

        String ex = "SELECT badgeTableID FROM userbadges WHERE userID = " + id + " AND isCompleted = TRUE ORDER BY badgeTableID;";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);
        while (rs.next()) {
            int badgeIDAdded = rs.getInt("badgeTableID");
            meritBadge mb = getBadge(badgeIDAdded);
            badges.add(mb);
        }


        return badges;
    }

    public static HashMap<Integer, HashMap<Integer, String>> getReqs() throws SQLException {
        HashMap<Integer, HashMap<Integer, String>> retList = new HashMap<>();


        Connection c = DriverManager.getConnection(url, username, password);
        //Finds badge names with the given name

        String ex = "SELECT * FROM badgereqs;";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);
        while (rs.next()) {
            int badgeNum = rs.getInt("badgeTableID");
            int reqNum = rs.getInt("reqNum");
            String requirement = rs.getString("reqDesc");
            if (retList.get(badgeNum) == null)
                retList.put(badgeNum, new HashMap<Integer, String>() {{
                    put(reqNum, requirement);
                }});
            else {
                HashMap<Integer, String> badgeMap = retList.get(badgeNum);
                if (badgeMap == null) retList.put(badgeNum, new HashMap<Integer, String>() {{
                    put(reqNum, requirement);
                }});
                else badgeMap.put(reqNum, requirement);
            }
        }


        return retList;
    }

    public static HashMap<Integer, ArrayList<Integer>> getCompletedReqs(scoutPerson p) throws SQLException {
        HashMap<Integer, ArrayList<Integer>> compReqs = new HashMap<>();
        ArrayList<Integer> reqsPerBadge = new ArrayList<>();
        int badgeID = -1;
        int reqNum;
        int lastBadgeID = -1;
        int iteration = 0;


        Connection c = DriverManager.getConnection(url, username, password);
        //Finds badge names with the given name and completed

        String ex = "SELECT * FROM userReq WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY badgeTableID;";
        Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

        //gets results from database
        ResultSet rs = stmt.executeQuery(ex);
        while (rs.next()) {
            badgeID = rs.getInt("badgeTableID");
            reqNum = rs.getInt("reqNum");

            if (badgeID != lastBadgeID && iteration != 0) {

                compReqs.put(lastBadgeID, new ArrayList<>(reqsPerBadge));
                reqsPerBadge.clear();
                lastBadgeID = badgeID;
                reqsPerBadge.add(reqNum);
            } else {
                reqsPerBadge.add(reqNum);
                lastBadgeID = badgeID;
                iteration++;
            }
        }

        if (badgeID != -1) {
            compReqs.put(badgeID, new ArrayList<>(reqsPerBadge));
        }


        return compReqs;
    }

    public static HashMap<Integer, ArrayList<Integer>> getAddedAndFinishedReqs(scoutPerson p) {
        HashMap<Integer, ArrayList<Integer>> compReqs = new HashMap<>();
        ArrayList<Integer> reqsPerBadge = new ArrayList<>();
        int badgeID = -1;
        int reqNum;
        int lastBadgeID = -1;
        int iteration = 0;


        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userReq WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY badgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                badgeID = rs.getInt("badgeTableID");
                reqNum = rs.getInt("reqNum");

                if (badgeID != lastBadgeID && iteration != 0) {

                    compReqs.put(lastBadgeID, new ArrayList<>(reqsPerBadge));
                    reqsPerBadge.clear();

                    lastBadgeID = badgeID;
                } else {
                    reqsPerBadge.add(reqNum);
                    lastBadgeID = badgeID;
                    iteration++;
                }
            }

            if (badgeID != -1) {
                compReqs.put(badgeID, new ArrayList<>(reqsPerBadge));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return compReqs;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void removeCompleted(ArrayList<Integer> badgeList, scoutPerson p) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {

            for (int i : badgeList) {
                String remove = "UPDATE userbadges SET isCompleted = 0 WHERE userID = " + p.getUserID() + " AND badgeTableID = " + i + ";";
                Statement stmt = c.createStatement();

                stmt.executeUpdate(remove);

                meritBadge b = getBadge(i);
                if (b == null) continue;
                for (int bi = 1; bi <= b.getNumReqs(); bi++) {
                    String addReq = "INSERT INTO userreq VALUES (" + p.getUserID() + intDiv + i + intDiv + bi + intDiv + "1);";
                    stmt.executeUpdate(addReq);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        SMyListFragment.getDatabaseInfo(p);
        SCompletedBadges.getFinishedBadges(p);
        SMyListFragment.getDatabaseInfo(p);
    }

    public static ArrayList<scoutPerson> getTroop(scoutMaster p) {
        ArrayList<scoutPerson> retList = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            String getT = "SELECT userID FROM users WHERE troop = " + p.getTroopNum() + " AND isScoutmaster = false;";
            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery(getT);

            while (rs.next()) {
                scoutPerson scout;
                int id = rs.getInt("userID");
                ArrayList<String> info = getUserInfo(id);
                if (info != null) {
                    scout = new scoutPerson(info);
                    retList.add(scout);
                } else return retList;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return retList;
    }

    public static void smSetBadges(Hashtable<Integer, ArrayList<Integer>> table) throws SQLException {
        Connection c = DriverManager.getConnection(url, username, password);

        for (Map.Entry<Integer, ArrayList<Integer>> scoutList : table.entrySet()) {
            int key = scoutList.getKey();

            ArrayList<meritBadge> badgesPerScout = getAddedBadgesMB(key);
            ArrayList<meritBadge> compBadges = getCompletedBadges(key);
            ArrayList<Integer> badgeIDs = new ArrayList<>();
            for (meritBadge mb : badgesPerScout) badgeIDs.add(mb.getId());
            for (meritBadge mb : compBadges) badgeIDs.add(mb.getId());

            String badges = "INSERT INTO userbadges VALUES (?, ?, 0);";
            PreparedStatement userStmt = c.prepareStatement(badges);

            String reqs = "INSERT INTO userreq VALUES (?, ?, ?, 0);";
            PreparedStatement reqStmt = c.prepareStatement(reqs);


            for (int i : scoutList.getValue()) {
                if (!badgeIDs.contains(i)) {
                    userStmt.setInt(1, i);
                    userStmt.setInt(2, key);
                    userStmt.addBatch();

                    meritBadge badge = getBadge(i);

                    for (int badgeReq = 1; badgeReq <= Objects.requireNonNull(badge).getNumReqs(); badgeReq++) {
                        reqStmt.setInt(1, key);
                        reqStmt.setInt(2, i);
                        reqStmt.setInt(3, badgeReq);

                        reqStmt.addBatch();
                    }
                }
            }

            userStmt.executeBatch();
            reqStmt.executeBatch();
        }


        table.clear();
        SMSearchBadgesFragment.resetChecked();
    }

    public static void addNewNot(scoutPerson p, int b) throws SQLException {
        Connection c = DriverManager.getConnection(url, username, password);
        int pID = p.getUserID();

        String ins = "INSERT INTO recentactivity VALUES (notifID, 1, ?, ?, ?, ?);";
        PreparedStatement stmt = c.prepareStatement(ins);

        if (b == -1) {
            stmt.setString(1, "n");
            stmt.setInt(4, -1);
        } else {
            stmt.setString(1, "b");
            stmt.setInt(4, b);
        }
        stmt.setInt(2, pID);
        stmt.setInt(3, p.getTroopNum());

        stmt.execute();
    }

    public static ArrayList<notification> getNotifications(scoutMaster u) {
        ArrayList<notification> nots = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();

            String getNots = "SELECT * FROM recentactivity WHERE troop = " + u.getTroopNum() + ";";

            ResultSet rs = stmt.executeQuery(getNots);

            if (!rs.first()) return nots;


            do {

                scoutPerson p = new scoutPerson(Objects.requireNonNull(getUserInfo(rs.getInt("userID"))));
                int notifID = rs.getInt("notifID");
                int bID = rs.getInt("badgeTableID");
                boolean newNot = rs.getInt("newNotif") == 1;

                notification n;
                if (bID == -1) {
                    n = new notification(p, notifID, newNot);
                } else {
                    n = new notification(p, getBadge(bID), notifID, newNot);
                }

                nots.add(n);

            } while (rs.next());

        } catch (SQLException ignored) {
        }

        return nots;
    }

    public static void clearNots(ArrayList<notification> nots) throws SQLException {
        Connection c = DriverManager.getConnection(url, username, password);
        String st = "UPDATE recentactivity SET newNotif = false WHERE notifID = ?";
        PreparedStatement stmt = c.prepareStatement(st);

        for (notification n : nots) {
            stmt.setInt(1, n.getId());
            stmt.addBatch();
        }

        stmt.executeBatch();
    }

    public static void deleteAllNots(scoutMaster p) throws SQLException {
        Connection c = DriverManager.getConnection(url, username, password);
        String del = "DELETE FROM recentactivity WHERE troop = ?;";
        PreparedStatement s = c.prepareStatement(del);

        s.setInt(1, p.getTroopNum());

        s.executeUpdate();
    }

    public static int[] getTotalAdded(scoutMaster p) throws SQLException {
        int[] ret = new int[3];
        int added = 0, comp = 0, eagle = 0;

        Connection c = DriverManager.getConnection(url, username, password);
        Statement stmt = c.createStatement();

        String query = "SELECT * FROM userbadges WHERE userID IN (SELECT userID FROM users WHERE troop = " + p.getTroopNum() + ");";
        ResultSet rs = stmt.executeQuery(query);

        if (!rs.first()) return ret;
        else {
            do {
                if (rs.getInt("isCompleted") == 1) comp++;
                else added++;

                int id = rs.getInt("badgeTableID");

                meritBadge mb = getBadge(id);

                if (mb == null) continue;
                if (mb.isEagle()) eagle++;
            } while (rs.next());
        }

        ret[0] = added;
        ret[1] = comp;
        ret[2] = eagle;
        return ret;
    }
}
