package Fragments.SMFrags;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.badgr.R;
import com.badgr.scoutClasses.notification;

import java.util.ArrayList;

public class SMRecentAllNots extends Activity {

    private static ArrayList<notification> nots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoutmaster_all_notifications);

        //sets page elements
        ListView lv = findViewById(R.id.allNotList);
        Button back = findViewById(R.id.back);

        //create new adapter and set list
        SMRecentAdapter adapter = new SMRecentAdapter(this, getStrings(), nots);
        lv.setAdapter(adapter);
        lv.setVisibility(View.VISIBLE);

        //back button on click, finish activity
        back.setOnClickListener(l -> finish());
    }

    public static void setNots(ArrayList<notification> n)
    {
        nots = n;
    }

    //gets strings
    private static String[] getStrings()
    {
        String[] strs = new String[nots.size()];
        int i = 0;

        for (notification n : nots)
        {
            //set respective string
            if (n.getMb() == null) strs[i] = n.getPerson().getFullName() + " has been added to your troop!";
            else strs[i] = n.getPerson().getFullName() + " has completed the " + n.getMb().getName() + " merit badge!";
            i++;
        }

        return strs;
    }
}
