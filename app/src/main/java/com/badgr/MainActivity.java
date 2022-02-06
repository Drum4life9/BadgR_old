package com.badgr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import com.badgr.sql.AllBadgeReqs;
import com.badgr.ui.login.LoginActivity;
import com.badgr.ui.register.RegisterActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.home_page);

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(AllBadgeReqs::new);
    }



    public void loginClick(View view) {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }


    public void registerClick(View view) {
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }


}

