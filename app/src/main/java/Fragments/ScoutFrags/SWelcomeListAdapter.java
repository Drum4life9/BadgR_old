package Fragments.ScoutFrags;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SWelcomeListAdapter extends ArrayAdapter {

    private final Activity context;
    private final scoutPerson user = LoginRepository.getUser();
    private HashMap<Integer, ArrayList<Integer>> finishedReq;

        public SWelcomeListAdapter(@NonNull Activity context, int resource, String[] bList) {
            super(context, resource, bList);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group_titles_my_list, null);
            }

            TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
            ProgressBar progress = convertView.findViewById(R.id.myProgressBar);
            TextView progressText = convertView.findViewById(R.id.progressText);

            final meritBadge[] badge = new meritBadge[1];
            CountDownLatch cdl = new CountDownLatch(1);

            ExecutorService STE = Executors.newSingleThreadExecutor();
            CountDownLatch finalCdl1 = cdl;
            STE.execute(() ->
            {
                badge[0] = sqlRunner.getBadge(position);
                finalCdl1.countDown();
            });

            try { finalCdl1.await(); }
            catch (InterruptedException e) { e.printStackTrace(); }


            cdl = new CountDownLatch(1);
            ExecutorService sTE = Executors.newSingleThreadExecutor();
            CountDownLatch finalCdl = cdl;
            sTE.execute(() ->
            {
                finishedReq = sqlRunner.getFinishedReqs(user);
                finalCdl.countDown();
            });

            try { finalCdl.await(); }
            catch (InterruptedException e) { e.printStackTrace(); }



            int numReqs = badge[0].getNumReqs();
            int compReqs = Objects.requireNonNull(finishedReq.get(position + 1)).size();
            double percent = compReqs * 1.0 / numReqs * 100;
            String percentText = ((int) (percent * 10) / 10) + "%";

            progress.setProgress((int) percent);
            listTitleTextView.setText(badge[0].getName());
            progressText.setText(percentText);

            return convertView;
        }


}
