package Fragments.SMFrags;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.badgr.R;
import com.badgr.scoutClasses.notification;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.sql.sqlRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMRecentAllNots extends Activity {

    private static ArrayList<notification> nots;
    private static scoutMaster user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoutmaster_all_notifications);

        //sets page elements
        ListView lv = findViewById(R.id.allNotList);
        Button back = findViewById(R.id.back);
        Button clear = findViewById(R.id.clear);

        //create new adapter and set list
        SMRecentAdapter adapter = new SMRecentAdapter(this, getStrings(), nots);
        lv.setAdapter(adapter);
        lv.setVisibility(View.VISIBLE);

        //back button on click, finish activity
        back.setOnClickListener(l -> finish());

        //clear button on click, try to clear nots. If error, toast message.
        clear.setOnClickListener(l -> {
            ExecutorService STE = Executors.newSingleThreadExecutor();
            STE.execute(() -> {
                try {
                    sqlRunner.deleteAllNots(user);

                    //finish activity (goes back to scoutmaster page)
                    finish();
                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                }
            });

        });
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

    public static void setUser(scoutMaster u) {user = u;}
}
