package com.badgr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

        //initial database connection to get all badges and requirements
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(AllBadgeReqs::new);

        //sets buttons
        Button login = findViewById(R.id.btnSignIn);
        Button register = findViewById(R.id.btnNewUser);

        //when button is clicked
        login.setOnClickListener(v -> loginClick());
        register.setOnClickListener(v -> registerClick());
    }


    //open login
    public void loginClick() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    //open register
    public void registerClick() {
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }


}

