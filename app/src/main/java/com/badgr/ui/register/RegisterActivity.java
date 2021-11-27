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
import java.util.concurrent.*;

import androidx.annotation.NonNull;


import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;


import com.badgr.R;
import com.badgr.ui.login.LoginActivity;

import java.util.concurrent.TimeUnit;


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

    private boolean usernameCheckSuccess = false;
    private boolean userInDB = false;

    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.layout_register);

        //sets texts and buttons from layout
        {
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
        }

        //adds textChangeListeners to items, tries to update button whenever text is changed
        {
            fNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    regButton.setEnabled(update());
                }
            });
            lNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    regButton.setEnabled(update());
                }
            });
            userEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    regButton.setEnabled(update());
                }
            });
        troopEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    regButton.setEnabled(update());
                }
            });
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

    /**
     * Creates a new scoutPerson p and uses it to add user to database
     *
     * @param v needed for android:onClick
     */
    public void attemptRegister(View v) {

        //creates a new scoutPerson with the given info
        scoutPerson p = new scoutPerson();
        p.setFName(fNameEdit.getText().toString().trim());
        p.setLName(lNameEdit.getText().toString().trim());
        p.setUser(userEdit.getText().toString());
        p.setPass(passEdit.getText().toString().trim());
        p.setAge(ageEdit.getText().toString());
        p.setSM(p.getAge()>=18);
        p.setTroop(troopEdit.getText().toString());


        //TODO loading screen?


        //if username exists
        if (checkUsernameExists())
        {
            if (usernameCheckSuccess)
            {
                //display toast that username exists
                Toast.makeText(this, "Username already exists. Please try a different email.", Toast.LENGTH_LONG).show();
            }
            else
            {
                //hopefully shoud never run, but error message in case of username check failure
                Toast.makeText(this, "There was an error checking username. Please try again.", Toast.LENGTH_LONG).show();
            }
            return;
        }

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
            Toast.makeText(this, "Error occurred with register. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }



        //if error, kill method and tell user to try again
        if (!sqlRunner.getRegisterSuccess()) {
            Toast.makeText(this, "Error occurred with register. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }


        //creates yet another countDownLatch
        CountDownLatch authCDL = new CountDownLatch(1);

        //thread to authenticate the user
        Thread authUser = new Thread(() -> {
            sqlRunner.authUser(p.getUser(), p.getPass());
            authCDL.countDown();

        });


        authUser.start();
        //waits until previous thread has completed to move on
        try {
            cDL.await();
        } catch (InterruptedException ignored) {
        }



        //if authentication was not successful, Toast an error message and kill the attempt
        if (!sqlRunner.getAuthSuccess())
        {
            Toast.makeText(this, "There was an error logging in. Please log in from the login page.", Toast.LENGTH_LONG).show();
            Intent oLogin = new Intent(this, LoginActivity.class);
            startActivity(oLogin);
            return;
        }


        //TODO login user here (somehow.....)
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);

    }

    //TODO fix this
    //checks to see if email is already in database using the sqlRunner class method userInDatabase
    private boolean checkUsernameExists() {
        userEdit = (EditText) findViewById(R.id.registerUser);

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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //opens the login screen
    public void openLoginApp(View view) {
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);
    }

}
