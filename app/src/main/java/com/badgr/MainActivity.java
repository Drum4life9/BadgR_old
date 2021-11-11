package com.badgr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import com.badgr.ui.login.LoginActivity;
import com.badgr.ui.register.RegisterActivity;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.home_page);
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

