package Fragments.SMFrags;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badgr.R;
import com.badgr.scoutClasses.notification;

import java.util.ArrayList;

public class SMRecentAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] strings;
    private final ArrayList<notification> nots;

    public SMRecentAdapter(Activity context, String[] sList, ArrayList<notification> n) {
        super(context, R.layout.notification_item, sList);
        this.context = context;
        strings = sList;
        nots = n;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.notification_item, null, true);

        ImageView img = row.findViewById(R.id.image);
        TextView text = row.findViewById(R.id.text);

        notification n = nots.get(position);
        if (n.getMb() != null)
        {
            String badgeImageName = "merit_badge_" + n.getMb().getStrippedName();
            int imgID = context.getResources().getIdentifier(badgeImageName, "drawable", context.getPackageName());
            img.setImageResource(imgID);
        }
        else
        {

            int imgID = context.getResources().getIdentifier("ic_launcher_foreground", "drawable", context.getPackageName());
            img.setImageResource(imgID);
        }

        text.setText(strings[position]);


        return row;
    }
}
