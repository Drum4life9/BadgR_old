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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.*;

import androidx.annotation.NonNull;

import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;


import com.badgr.R;
import com.badgr.ui.login.LoginActivity;


public class RegisterActivity extends Activity {

    //---------------------------------------------Class Fields-------------------------------------//

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
    private FrameLayout loading;

    private boolean usernameCheckSuccess = false;
    private boolean userInDB = false;


    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.layout_register);

        //sets texts and buttons from layout
        {
            fNameEdit = findViewById(R.id.registerFName);
            lNameEdit = findViewById(R.id.registerLName);
            userEdit = findViewById(R.id.registerUser);
            passEdit = findViewById(R.id.registerPass);
            ageEdit = findViewById(R.id.registerAge);
            troopEdit = findViewById(R.id.registerTroop);
            regButton = findViewById(R.id.registerButton);

            passLength = findViewById(R.id.lengthCheck);
            passCapital = findViewById(R.id.capitalCheck);
            passNumber = findViewById(R.id.numberCheck);
            loading = findViewById(R.id.loadingScreen);
        }

        //adds textChangeListeners to items, tries to update button whenever text is changed
        {
            fNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isFNameValid(fNameEdit.getText().toString()))
                        fNameEdit.setError("Invalid First Name");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isFNameValid(fNameEdit.getText().toString()))
                        fNameEdit.setError("Invalid First Name");
                    regButton.setEnabled(update());
                }
            });
            lNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isFNameValid(lNameEdit.getText().toString()))
                        lNameEdit.setError("Invalid Last Name");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isFNameValid(lNameEdit.getText().toString()))
                        lNameEdit.setError("Invalid Last Name");
                    regButton.setEnabled(update());
                }
            });
            userEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isUserNameValid(userEdit.getText().toString()))
                        userEdit.setError("Invalid username");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isUserNameValid(userEdit.getText().toString()))
                        userEdit.setError("Invalid username");
                    regButton.setEnabled(update());
                }
            });
            passEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (RegisterViewModel.passUpperValid(passEdit.getText().toString())) {
                        passCapital.setTextColor(Color.rgb(106, 196, 79));
                    } else {
                        passCapital.setTextColor(Color.rgb(255, 0, 0));
                    }
                    if (RegisterViewModel.passNumberValid(passEdit.getText().toString())) {
                        passNumber.setTextColor(Color.rgb(106, 196, 79));
                    } else {
                        passNumber.setTextColor(Color.rgb(255, 0, 0));
                    }
                    if (RegisterViewModel.passLengthValid(passEdit.getText().toString())) {
                        passLength.setTextColor(Color.rgb(106, 196, 79));
                    } else {
                        passLength.setTextColor(Color.rgb(255, 0, 0));
                    }

                }
            });
            ageEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isAgeValid(ageEdit.getText().toString()))
                        ageEdit.setError("Age must be <= 120 and > 0");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isAgeValid(ageEdit.getText().toString()))
                        ageEdit.setError("Age must be <= 120 and > 0");
                    regButton.setEnabled(update());
                }
            });
            troopEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isTroopValid(troopEdit.getText().toString()))
                        troopEdit.setError("Troop must be <= 9999 and > 0");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isTroopValid(troopEdit.getText().toString()))
                        troopEdit.setError("Troop must be <= 9999 and > 0");
                    regButton.setEnabled(update());
                }
            });
        }
    }


    /**
     * Creates a new scoutPerson p and uses it to add user to database
     *
     * @param v needed for android:onClick
     */
    public void attemptRegister(View v) {

        //-------------------------------------------------Toggles Loading Screen Layout Over Register Page-----------------------------//

        toggleVis();

        //creates a new scoutPerson with the given info
        scoutPerson p = new scoutPerson();
        p.setFName(fNameEdit.getText().toString().trim());
        p.setLName(lNameEdit.getText().toString().trim());
        p.setUser(userEdit.getText().toString());
        p.setPass(passEdit.getText().toString().trim());
        p.setAge(ageEdit.getText().toString());
        p.setSM(p.getAge()>=18);
        p.setTroop(troopEdit.getText().toString());



        //-------------------------------------------Username check----------------------------------------//

        //if username exists
        if (checkUsernameExists())
        {
            if (usernameCheckSuccess)
            {
                //display toast that username exists
                toggleVis();
                Toast.makeText(this, "Username already exists. Please try a different email.", Toast.LENGTH_LONG).show();
            }
            else
            {
                //hopefully should never run, but error message in case of username check failure
                toggleVis();
                Toast.makeText(this, "There was an error checking username. Please try again.", Toast.LENGTH_LONG).show();
            }
            return;
        }


        //---------------------------------------------Add User Attempt------------------------------------//

        //Sets a countDownLatch, which ensures this thread is run before anything else happens
        CountDownLatch cDL = new CountDownLatch(1);

        //creates add user to database thread
        Thread addUser = new Thread(() -> {
            sqlRunner.addUser(p);
            cDL.countDown();

        });

        //runs the add user thread
        addUser.start();

        //waits until previous thread has completed to move on
        try {
            cDL.await();
        } catch (InterruptedException e) {
            toggleVis();
            Toast.makeText(this, "Error occurred with register. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }


        //if error, kill method and tell user to try again
        if (!sqlRunner.getRegisterSuccess()) {
            toggleVis();
            Toast.makeText(this, "Error occurred with register. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }


        //---------------------------------------------Open Login page-------------------------------------//

        toggleVis();

        Toast.makeText(this, "Register Successful. Please Log In.", Toast.LENGTH_LONG).show();
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);


    }

    //checks to see if email is already in database using the sqlRunner class method userInDatabase
    private boolean checkUsernameExists() {
        userEdit = findViewById(R.id.registerUser);

        //Sets a countDownLatch, which ensures this thread is run before anything else happens
        CountDownLatch cDL = new CountDownLatch(1);

        //creates check thread
        Thread isUserInDB = new Thread(() -> {
            userInDB = sqlRunner.isUserInDatabase(userEdit.getText().toString());
            cDL.countDown();
        });

        //runs the check username thread
        isUserInDB.start();

        //waits until previous thread has completed to move on
        try {
            cDL.await();
        } catch (InterruptedException e) {
            usernameCheckSuccess = false;
            return false;
        }

        usernameCheckSuccess = true;
        return userInDB;
    }


    //orientation change handler
    public void onConfigurationChanged(@NonNull Configuration newConfig) { super.onConfigurationChanged(newConfig); }

    //opens the login screen
    public void openLoginApp(View view) {
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);
    }


    //TODO FIX THIS!!!!!!!
    private void toggleVis()
    {
        loading = findViewById(R.id.loadingScreen);
        ProgressBar spinner = findViewById(R.id.progress_loader);
        TextView loadingText = findViewById(R.id.loading);

        loading.clearAnimation();
        spinner.clearAnimation();
        loadingText.clearAnimation();

        if (loading.getVisibility() == View.INVISIBLE)
        {
            loading.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
        }
        else
        {
            loading.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            loadingText.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Tries to run the text checks located in RegisterViewModel to update the submit button status
     *
     * @return if text checks are true, so the regButton.setEnabled(update()) returns true
     */
    private boolean update() {
        return RegisterViewModel.registerDataChanged(
                fNameEdit.getText().toString(),
                lNameEdit.getText().toString(),
                userEdit.getText().toString(),
                passEdit.getText().toString(),
                ageEdit.getText().toString(),
                troopEdit.getText().toString());

    }
}
