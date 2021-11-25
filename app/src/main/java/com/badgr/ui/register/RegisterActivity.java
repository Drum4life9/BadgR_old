package com.badgr.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    private TextView passLength;
    private TextView passCapital;
    private TextView passNumber;

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

        passLength = (TextView) findViewById(R.id.lengthCheck);
        passCapital = (TextView) findViewById(R.id.capitalCheck);
        passNumber = (TextView) findViewById(R.id.numberCheck);



        fNameEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        lNameEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        userEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        passEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                regButton.setEnabled(update());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegisterViewModel.passUpperValid(passEdit.getText().toString())) {
                    passCapital.setTextColor(Color.rgb(106, 196, 79));
                    passCapital.setVisibility(View.VISIBLE);
                } else {
                    passCapital.setTextColor(Color.rgb(255, 0, 0));
                    passCapital.setVisibility(View.VISIBLE);
                }
                if (RegisterViewModel.passNumberValid(passEdit.getText().toString())) {
                    passNumber.setTextColor(Color.rgb(106, 196, 79));
                    passNumber.setVisibility(View.VISIBLE);
                } else {
                    passNumber.setTextColor(Color.rgb(255, 0, 0));
                    passNumber.setVisibility(View.VISIBLE);
                }
                if (RegisterViewModel.passLengthValid(passEdit.getText().toString())) {
                    passLength.setTextColor(Color.rgb(106, 196, 79));
                    passLength.setVisibility(View.VISIBLE);
                } else {
                    passLength.setTextColor(Color.rgb(255, 0, 0));
                    passLength.setVisibility(View.VISIBLE);
                }

            }
        });
        ageEdit.setOnFocusChangeListener((v, hasFocus) -> regButton.setEnabled(update()));
        troopEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                regButton.setEnabled(update());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                regButton.setEnabled(update());
            }
        });



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
        p.setSM(p.getAge()>=18);
        p.setTroop(troopEdit.getText().toString());

        sqlRunner.addUser(p);
        //TODO LOGIN USER HERE
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
