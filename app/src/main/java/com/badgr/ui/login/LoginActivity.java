package com.badgr.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.badgr.R;
import com.badgr.appPages.scoutmasterPage;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.ui.login.LoginViewModel;
import com.badgr.ui.login.LoginViewModelFactory;
import com.badgr.databinding.ActivityLoginBinding;
import com.badgr.sql.sqlRunner;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.badgr.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());                                                              //sets activityLayout

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;


        //sees if text-boxes changed
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {                                                                   //return null if blank login form
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));                   //changes & enables submit button
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });




        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());                                                    //login failure
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());                                                 //login success, runs updateUiWithUser()
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        });
    }

    private void updateUiWithUser(@NonNull LoggedInUserView model) {
        String welcome = (getString(R.string.welcome) + " " + model.getDisplayName() + "!");
        // TODO : initiate successful logged in experience

        openApp();
        //sqlRunner.addUser(new scoutPerson());
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
    }

    public void openApp() {
        Intent open = new Intent(this, scoutmasterPage.class);
        startActivity(open);
    }
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}