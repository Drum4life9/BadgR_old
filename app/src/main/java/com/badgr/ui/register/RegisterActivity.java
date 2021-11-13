package com.badgr.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.badgr.R;
import com.badgr.ui.login.LoginActivity;

public class RegisterActivity extends Activity {

    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.layout_register);
    }


    public void openLoginApp(View view) {
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);
    }
}
