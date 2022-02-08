package Fragments.ScoutFrags;

import com.badgr.scoutClasses.meritBadge;

import java.util.ArrayList;

public class SSearchListTitles {
    public static ArrayList<String> getData(ArrayList<meritBadge> mbs) {
        ArrayList<String> titles = new ArrayList<>();

        if (mbs == null) return titles;
        for (meritBadge b : mbs) {
            titles.add(b.getName());
        }

        return titles;
    }
}
