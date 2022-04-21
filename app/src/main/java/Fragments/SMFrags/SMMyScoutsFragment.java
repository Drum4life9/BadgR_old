package Fragments.SMFrags;


import android.os.Bundle;
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


    private final scoutMaster u = (scoutMaster) LoginRepository.getUser();
    private String[] sNames;
    private ListView scoutsList;
    private ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scoutmaster_fragment_my_scouts, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scoutsList = view.findViewById(R.id.scoutsList);
        pb = view.findViewById(R.id.progressBar);

        setsNames();

        SMMyScoutsListAdapter adapter = new SMMyScoutsListAdapter(getActivity(), u, sNames);
        scoutsList.setAdapter(adapter);

    }


    private void setsNames()
    {
        sNames = new String[u.getTroop().size()];
        int c = 0;
        for (scoutPerson p : u.getTroop())
        {
            String name = p.getFullName();
            sNames[c] = name;
            c++;
        }

        scoutsList.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }


}
