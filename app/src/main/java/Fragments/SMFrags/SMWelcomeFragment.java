package Fragments.SMFrags;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.meritBadge;
import com.badgr.scoutClasses.scoutMaster;
import com.badgr.sql.sqlRunner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SMWelcomeFragment extends Fragment {

    private static final scoutMaster user = (scoutMaster) LoginRepository.getUser();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scoutmaster_fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload(view, getContext());
    }


    private static void reload(View view, Context context)
    {
        TextView added = view.findViewById(R.id.progressGrid);
        TextView compText = view.findViewById(R.id.completedGrid);
        TextView eagleText = view.findViewById(R.id.eagleGrid);

        LinearLayout infoGrid = view.findViewById(R.id.infoGrid);
        ProgressBar pb = view.findViewById(R.id.spinner);

        infoGrid.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            ExecutorService STE = Executors.newSingleThreadExecutor();
            Future<int[]> add = STE.submit(() -> sqlRunner.getTotalAdded(user));


            String addedS, compS, eagleS;

            try {
                int[] nums = add.get();
                addedS = nums[0] + " badge(s)";
                compS = nums[1] + " badge(s)";
                eagleS = nums[2] + "/" + addedS;


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(context, "A database error has occurred", Toast.LENGTH_LONG).show();

                addedS = "Error";
                compS = "Error";
                eagleS = "Error";
            }



            added.setText(addedS);
            compText.setText(compS);
            eagleText.setText(eagleS);

            infoGrid.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }, 100);

    }


    @Override
    public void onResume() {
        super.onResume();
        reload(requireView(), getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        requireView().findViewById(R.id.infoGrid).setVisibility(View.INVISIBLE);
        requireView().findViewById(R.id.spinner).setVisibility(View.VISIBLE);
    }
}
