package com.badgr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(AllBadgeReqs::new);

        Button login = findViewById(R.id.btnSignIn);
        Button register = findViewById(R.id.btnNewUser);


        login.setOnClickListener(v -> loginClick());
        register.setOnClickListener(v -> registerClick());
    }


    public void loginClick() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }


    public void registerClick() {
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }


}

