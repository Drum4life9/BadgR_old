package com.badgr.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.badgr.appPages.scoutmasterPage;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.ui.login.LoginViewModelFactory;
import com.badgr.ui.register.RegisterViewModelFactory;

import com.badgr.R;
import com.badgr.ui.login.LoginActivity;
import com.badgr.ui.login.LoginViewModel;

public class RegisterActivity extends Activity {

    private RegisterViewModel registerViewModel;
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

        registerViewModel = new RegisterViewModel();

        fNameEdit = (EditText) findViewById(R.id.registerFName);
        lNameEdit = (EditText) findViewById(R.id.registerLName);
        userEdit = (EditText) findViewById(R.id.registerUser);
        passEdit = (EditText) findViewById(R.id.registerPass);
        ageEdit = (EditText) findViewById(R.id.registerAge);
        troopEdit = (EditText) findViewById(R.id.registerTroop);
        regButton = (Button) findViewById(R.id.registerButton);



        userEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (update())
                {
                    regButton.setEnabled(true);
                }
                else {regButton.setEnabled(false);}
            }
        });
        passEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (update())
                {
                    regButton.setEnabled(true);
                }
                else {regButton.setEnabled(false);}
            }
        });



    }



    private boolean update() {
        boolean success = registerViewModel.registerDataChanged(
                userEdit.getText().toString(),
                passEdit.getText().toString(),
                fNameEdit.getText().toString(),
                lNameEdit.getText().toString(),
                ageEdit.getText().toString(),
                troopEdit.getText().toString());
        return success;
    }

    public void successRegister(View v) {


        //sqlRunner.addUser(new scoutPerson()); //TODO add user
        Intent oLogin = new Intent(this, scoutmasterPage.class);
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
