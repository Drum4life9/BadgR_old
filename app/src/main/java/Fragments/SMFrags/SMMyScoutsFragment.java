package Fragments.SMFrags;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.scoutClasses.scoutPerson;

public class SMMyScoutsFragment extends Fragment {
    private final scoutMaster user;
    private ListView scoutsList;
    private ProgressBar pb;

    //sets user when fragment initialized
    public SMMyScoutsFragment(scoutMaster u) {user = u;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scoutmaster_fragment_my_scouts, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //gets page elements
        scoutsList = view.findViewById(R.id.scoutsList);
        pb = view.findViewById(R.id.progressBar);

        //delays 50ms, then sets list to allow spinner to activate
        new Handler().postDelayed(() -> {

            //sets names
            setsNames();

            //creates list and toggles visibilities
            SMMyScoutsListAdapter adapter = new SMMyScoutsListAdapter(getActivity(), user, setsNames());
            scoutsList.setAdapter(adapter);
            scoutsList.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

        }, 50);
    }


    private String[] setsNames()
    {
        //creates list of names
        String[] sNames = new String[user.getTroop().size()];
        int c = 0;
        for (scoutPerson p : user.getTroop()) {
            String name = p.getFullName();
            sNames[c] = name;
            c++;
        }

        return sNames;
    }


}
