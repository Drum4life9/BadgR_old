package Fragments.SMFrags;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.badgr.R;
import com.badgr.scoutClasses.notification;

import java.util.ArrayList;
import java.util.Objects;

public class SMRecentAllNots extends AppCompatActivity {

    private static ArrayList<notification> nots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoutmaster_all_notifications);

        ListView lv = findViewById(R.id.allNotList);
        Button back = findViewById(R.id.back);

        SMRecentAdapter adapter = new SMRecentAdapter(this, getStrings(), nots);
        lv.setAdapter(adapter);

        lv.setVisibility(View.VISIBLE);

        back.setOnClickListener(l -> finish());
    }

    public static void setNots(ArrayList<notification> n)
    {
        nots = n;
    }

    private static String[] getStrings()
    {
        String[] strs = new String[nots.size()];
        int i = 0;

        for (notification n : nots)
        {
            if (n.getMb() == null)
            {
                strs[i] = n.getPerson().getFullName() + " has been added to your troop!";
            }
            else
            {
                strs[i] = n.getPerson().getFullName() + " has completed the " + n.getMb().getName() + " merit badge!";
            }
            i++;
        }

        return strs;
    }
}
