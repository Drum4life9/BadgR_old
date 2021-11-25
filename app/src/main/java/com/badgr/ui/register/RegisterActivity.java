package com.badgr.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;


import com.badgr.R;
import com.badgr.ui.login.LoginActivity;


public class RegisterActivity extends Activity {

    private EditText fNameEdit;
    private EditText lNameEdit;
    private EditText userEdit;
    private EditText passEdit;
    private EditText ageEdit;
    private EditText troopEdit;
    private Button regButton;

    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.layout_register);

        fNameEdit = (EditText) findViewById(R.id.registerFName);
        lNameEdit = (EditText) findViewById(R.id.registerLName);
        userEdit = (EditText) findViewById(R.id.registerUser);
        passEdit = (EditText) findViewById(R.id.registerPass);
        ageEdit = (EditText) findViewById(R.id.registerAge);
        troopEdit = (EditText) findViewById(R.id.registerTroop);
        regButton = (Button) findViewById(R.id.registerButton);



        fNameEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        lNameEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        userEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        passEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        ageEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        troopEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));



    }



    private boolean update() {
        return RegisterViewModel.registerDataChanged(
                fNameEdit.getText().toString(),
                lNameEdit.getText().toString(),
                userEdit.getText().toString(),
                passEdit.getText().toString(),
                ageEdit.getText().toString(),
                troopEdit.getText().toString());

    }

    public void attemptRegister(View v) {

        scoutPerson p = new scoutPerson();
        p.setFName(fNameEdit.getText().toString());
        p.setLName(lNameEdit.getText().toString());
        p.setUser(userEdit.getText().toString());
        p.setPass(passEdit.getText().toString());
        p.setAge(ageEdit.getText().toString());
        p.setTroop(troopEdit.getText().toString());

        sqlRunner.addUser(p); //TODO add user
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);

    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void openLoginApp(View view) {
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);
    }

}
