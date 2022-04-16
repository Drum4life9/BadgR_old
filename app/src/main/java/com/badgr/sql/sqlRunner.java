package com.badgr.sql;

import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.notification;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import Fragments.SMFrags.SMSearchBadgesFragment;
import Fragments.ScoutFrags.SCompletedBadges;
import Fragments.ScoutFrags.SMyListExpandListAdapter;
import Fragments.ScoutFrags.SMyListFragment;
import Fragments.ScoutFrags.SSearchExpandListAdapter;

public class sqlRunner {
    //divider strings are easier to call than type out
    private final static String strDiv = "', '";
    private final static String intDiv = ", ";

    //sql connection strings
    private final static String url = "jdbc:mysql://192.168.1.22:3306/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&allowMultiQueries=true&connectRetryInterval=1";
    private final static String username = "AppRunner";
    private final static String password = "AppRunner1";

    //error variables
    private static boolean authSuccess = false;


    public static boolean addUser(scoutPerson p) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();
            int isSM;
            if (p.getAge() >= 18) isSM = 1;
            else isSM = 0;
            String addStmt = "INSERT INTO userpass VALUES (userPassID, '" + p.getPass() + "'); " +
                    "INSERT INTO users VALUES (userID, '" + p.getFName() + strDiv + p.getLName() + strDiv + p.getUser() + "', last_insert_id(), " +
                    p.getAge() + intDiv + isSM + intDiv + p.getTroopNum() + "); " +
                    "INSERT INTO troop VALUES (" + p.getTroopNum() + ", last_insert_id(), " + p.isSM() + ");";
            stmt.executeUpdate(addStmt);

            scoutPerson newP = new scoutPerson(Objects.requireNonNull(getUserInfo(p.getUser())));
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
            //Finds userPassID with the given username
            String ex = "SELECT * FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


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


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static ArrayList<String> getUserInfo(int userId) {
        ArrayList<String> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT * FROM users WHERE userID = '" + userId + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


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


        } catch (SQLException e) {
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
            } else {
                //sets the pass string to the database password
                pass = rs.getString(1);
            }

            //sets the successful login if the username in database = username given, and same for password
            authSuccess = pass.equals(givenP); //TODO and username stuff here


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUserInDatabase(String givenU) {
        boolean userInDB = false;
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE username = '" + givenU + "'";
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

    public static ArrayList<meritBadge> getListOfBadges(String bName) {
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
                mb.setId(rs.getInt("BadgeTableID"));
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

            String ex = "SELECT * FROM badgetable where BadgeTableID = " + badgeID + ";";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            while (rs.next()) {
                mb.setName(rs.getString("badgeName"));
                mb.setEagle(rs.getInt("isEagleReq") == 1);
                mb.setNumReqs(rs.getInt("numReqs"));
                mb.setId(rs.getInt("BadgeTableID"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return mb;
    }

    public static ArrayList<meritBadge> getListOfBadges(scoutPerson p) {
        ArrayList<meritBadge> retList = new ArrayList<>();
        ArrayList<Integer> addedBadges = addedBadges(p);
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            for (int i : addedBadges) {
                String ex = "SELECT * FROM badgetable where BadgeTableID = " + i + ";";
                Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


                //gets results from database
                ResultSet rs = stmt.executeQuery(ex);

                while (rs.next()) {
                    meritBadge mb = new meritBadge();
                    mb.setName(rs.getString("badgeName"));
                    mb.setEagle(rs.getInt("isEagleReq") == 1);
                    mb.setNumReqs(rs.getInt("numReqs"));
                    mb.setId(rs.getInt("BadgeTableID"));
                    retList.add(mb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static void toggleAddToList(scoutPerson p, ArrayList<Integer> added, ArrayList<Integer> removed) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            String addOrRemoveBadge;
            Statement stmtAddOrRemove = c.createStatement();

            for (int i : added) {

                addOrRemoveBadge = "INSERT INTO userbadges VALUES (" + i + ", " + p.getUserID() + ", 0);";
                stmtAddOrRemove.executeUpdate(addOrRemoveBadge);

                //add requirements to list
                for (int j = 1; j <= Objects.requireNonNull(getBadge(i)).getNumReqs(); j++) {
                    String addReq = "INSERT INTO userreq VALUES (" + p.getUserID() + ", " + i + ", " + j + ", 0);";
                    stmtAddOrRemove.executeUpdate(addReq);
                }
            }

            for (int i : removed) {
                //remove the merit badge
                addOrRemoveBadge = "DELETE FROM userbadges WHERE BadgeTableID = " + i + ";";
                stmtAddOrRemove.executeUpdate(addOrRemoveBadge);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //update checked boxes for when the children of the expandable list are created
        SSearchExpandListAdapter.pullAddedBadges(p);
    }

    public static void toggleAddToReqList(scoutPerson p, HashMap<Integer, ArrayList<Integer>> addReqsMap, HashMap<Integer, ArrayList<Integer>> delReqsMap) {
        ArrayList<Integer> addedReqList = new ArrayList<>();
        ArrayList<Integer> delReqList = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : addReqsMap.entrySet()) {
                //Finds badge names with the given ID

                int badgeID = entry.getKey();
                ArrayList<Integer> requirements = entry.getValue();
                for (int reqNum : requirements) {
                    String updateReq;
                    Statement stmtAddOrRemove = c.createStatement();
                    String ex = "SELECT * FROM userreq WHERE BadgeTableID = " + badgeID + ";";
                    Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

                    //gets results from database
                    ResultSet rs = stmt.executeQuery(ex);
                    while (rs.next()) {
                        int badgeReqNum = rs.getInt("reqNum");
                        int isCompleted = rs.getInt("isCompleted");
                        if (isCompleted == 1) addedReqList.add(badgeReqNum);
                    }


                    //if the requirement we are adding isn't already in the database
                    if (!addedReqList.contains(reqNum)) {
                        //add to table
                        updateReq = "UPDATE userreq SET isCompleted = 1 WHERE userID = " + p.getUserID() + " AND BadgeTableID = " + badgeID + " AND reqNum = " + reqNum + ";";
                        stmtAddOrRemove.executeUpdate(updateReq);
                    }
                    addedReqList.clear();
                }
            }


            for (Map.Entry<Integer, ArrayList<Integer>> entryDel : delReqsMap.entrySet()) {
                //Finds badge names with the given ID

                int badgeIDDel = entryDel.getKey();
                ArrayList<Integer> requirementsDel = entryDel.getValue();
                for (int reqNum : requirementsDel) {
                    String updateReq;
                    Statement stmtAddOrRemove = c.createStatement();
                    String ex = "SELECT * FROM userreq WHERE BadgeTableID = " + badgeIDDel + ";";
                    Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

                    //gets results from database
                    ResultSet rs = stmt.executeQuery(ex);
                    while (rs.next()) {
                        int badgeReqNum = rs.getInt("reqNum");
                        int isCompleted = rs.getInt("isCompleted");
                        if (isCompleted == 1) delReqList.add(badgeReqNum);
                    }


                    if (delReqList.contains(reqNum)) {
                        //remove the merit badge
                        updateReq = "UPDATE userreq SET isCompleted = 0 WHERE userID = " + p.getUserID() + " AND BadgeTableID = " + badgeIDDel + " AND reqNum = " + reqNum + ";";
                        stmtAddOrRemove.executeUpdate(updateReq);
                    }
                }

                delReqList.clear();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Integer> addedBadges(scoutPerson p) {
        ArrayList<Integer> added = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = 0 ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                added.add(badgeIDAdded);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return added;
    }

    public static ArrayList<meritBadge> getAddedBadges(scoutPerson p) {
        ArrayList<meritBadge> added = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = 0 ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                added.add(getBadge(badgeIDAdded));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return added;
    }

    public static ArrayList<meritBadge> getAddedBadges(int userID) {
        ArrayList<meritBadge> added = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM userbadges WHERE userID = " + userID + " AND isCompleted = 0 ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                added.add(getBadge(badgeIDAdded));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return added;
    }

    public static void setBadgeCompleted(scoutPerson p, int i) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            String updateCompletion;
            Statement stmt = c.createStatement();

            updateCompletion = "UPDATE userbadges SET isCompleted = 1 WHERE userID = " + p.getUserID() + " AND BadgeTableID = " + i + ";";
            stmt.executeUpdate(updateCompletion);

            updateCompletion = "DELETE FROM userreq where userID = " + p.getUserID() + " AND BadgeTableID = " + i + ";";
            stmt.executeUpdate(updateCompletion);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> getFinishedBadgesInt(scoutPerson p) {
        ArrayList<Integer> completed = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                completed.add(badgeIDAdded);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        SCompletedBadges.getFinishedBadges();

        return completed;
    }

    public static ArrayList<meritBadge> getFinishedBadges(scoutPerson p) {
        ArrayList<meritBadge> badges = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT BadgeTableID FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                meritBadge mb = getBadge(badgeIDAdded);
                badges.add(mb);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return badges;
    }

    public static HashMap<Integer, HashMap<Integer, String>> getReqs() {
        HashMap<Integer, HashMap<Integer, String>> retList = new HashMap<>();


        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM badgereqs;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                int badgeNum = rs.getInt("BadgeTableID");
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


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retList;
    }

    public static HashMap<Integer, ArrayList<Integer>> getFinishedReqs(scoutPerson p) {
        HashMap<Integer, ArrayList<Integer>> compReqs = new HashMap<>();
        ArrayList<Integer> reqsPerBadge = new ArrayList<>();
        int badgeID = -1;
        int reqNum;
        int lastBadgeID = -1;
        int iteration = 0;


        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userReq WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                badgeID = rs.getInt("BadgeTableID");
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

    public static HashMap<Integer, ArrayList<Integer>> getAddedAndFinishedReqs(scoutPerson p) {
        HashMap<Integer, ArrayList<Integer>> compReqs = new HashMap<>();
        ArrayList<Integer> reqsPerBadge = new ArrayList<>();
        int badgeID = -1;
        int reqNum;
        int lastBadgeID = -1;
        int iteration = 0;


        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userReq WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next()) {
                badgeID = rs.getInt("BadgeTableID");
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

    public static void removeCompleted(ArrayList<Integer> badgeList, scoutPerson p) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {

            for (int i : badgeList) {
                String remove = "UPDATE userbadges SET isCompleted = false WHERE userID = " + p.getUserID() + " AND BadgeTableID = " + i + ";";
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

        SMyListFragment.getBadgesAdded();
        SCompletedBadges.getFinishedBadges();
        SMyListExpandListAdapter.pullFinishedReqs(p);
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

    public static void setBadges(Hashtable<Integer, ArrayList<Integer>> table) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {

            for (Map.Entry<Integer, ArrayList<Integer>> scoutList : table.entrySet()) {
                int key = scoutList.getKey();

                ArrayList<meritBadge> badgesPerScout = getAddedBadges(key);
                ArrayList<Integer> badgeIDs = new ArrayList<>();
                for (meritBadge mb : badgesPerScout) badgeIDs.add(mb.getId());

                Statement stmt = c.createStatement();
                for (int i : scoutList.getValue()) {
                    if (!badgeIDs.contains(i)) {
                        String update = "INSERT INTO userbadges VALUES (" + i + ", " + key + ", 0);";
                        stmt.executeUpdate(update);

                        for (int badgeReq = 1; badgeReq <= Objects.requireNonNull(getBadge(i)).getNumReqs(); badgeReq++) {
                            String addReq = "INSERT INTO userreq VALUES (" + key + ", " + i + ", " + badgeReq + ", 0);";
                            stmt.executeUpdate(addReq);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.clear();
        SMSearchBadgesFragment.resetChecked();
    }

    public static void addNewNot(scoutPerson p, int b)
    {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();
            String upd;
            int pID = p.getUserID();
            if (b == -1) upd = "INSERT INTO recentactivity VALUES (1, \"newUser\", " + pID + intDiv + p.getTroopNum() + intDiv + "-1,  notifID);";
            else upd = "INSERT INTO recentactivity VALUES (1, \"badgeComp\", " + pID + intDiv + p.getTroopNum() + intDiv + b + ", notifID);";

            stmt.execute(upd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ArrayList<notification> getNotifications(scoutMaster u) {
        ArrayList<notification> nots = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();

            String getNots = "SELECT * FROM recentactivity WHERE troop = " + u.getTroopNum() + " AND newNotif = 1;";

            ResultSet rs = stmt.executeQuery(getNots);

            if (!rs.first()) return nots;

            int count = 0;
            while (rs.next())
            {
                if (count == 0)
                {
                    count++;
                    rs.first();
                }
                scoutPerson p = new scoutPerson(Objects.requireNonNull(getUserInfo(rs.getInt("userID"))));
                int notifID = rs.getInt("notifID");
                int bID = rs.getInt("badgeTableID");

                notification n;
                if (bID == -1)
                {
                    n = new notification(p, notifID);
                }
                else {
                    n = new notification(p, getBadge(bID), notifID);
                }

                nots.add(n);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return nots;
    }

    public static void clearNots(ArrayList<notification> nots) {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();

            String clear;

            for (notification n : nots)
            {
                clear = "UPDATE recentactivity SET newNotif = false WHERE notifID = " + n.getId();
                stmt.executeUpdate(clear);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean getAuthSuccess() {
        return authSuccess;
    }

}
