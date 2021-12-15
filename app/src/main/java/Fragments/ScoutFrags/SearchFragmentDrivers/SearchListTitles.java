package Fragments.ScoutFrags.SearchFragmentDrivers;

import java.util.ArrayList;

import com.badgr.scoutClasses.meritBadge;

public class SearchListTitles {
    public static ArrayList<String> getData(ArrayList<meritBadge> mbs) {
        ArrayList<String> titles = new ArrayList<>();

        if (mbs == null) return titles;
        for (meritBadge b : mbs)
        {
            titles.add(b.getName());
        }


        return titles;
    }
}
