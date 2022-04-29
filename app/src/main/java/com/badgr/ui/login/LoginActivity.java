package com.badgr.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.badgr.R;
import com.badgr.data.LoginRepository;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.scoutPagesAndClasses.scoutMasterPage;
import com.badgr.scoutPagesAndClasses.scoutPage;
import com.badgr.ui.register.RegisterActivity;
import com.badgr.ui.register.RegisterViewModel;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //creates loginViewModel, which helps with login authentication and setting LoginRepo
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        //UI elements
        final EditText emailEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final FrameLayout loadingFrame = findViewById(R.id.loadingScreenLogin);
        final TextView noAccount = findViewById(R.id.noAccount);

        //sets loading screen to false
        toggleLoading(loadingFrame, false);

        //once loginRepo user changes
        loginViewModel.getLoginResult().observe(this, loginResult -> {
            //toggle loading screen
            toggleLoading(loadingFrame, true);


            //if nothing return
            if (loginResult == null) { return; }
            //if the result is an error
            if (loginResult.getError() != null) {
                //show the error and toggle loading screen
                showLoginFailed(loginResult.getError());
                toggleLoading(loadingFrame, false);
            }
            //if result is success, update UI and log in the user to their page
            if (loginResult.getSuccess() != null) { updateUiWithUser(loginResult.getSuccess()); }
            setResult(Activity.RESULT_OK);
        });

        //email and password checks, activated button if valid email and password
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!RegisterViewModel.isUserNameValid(emailEditText.getText().toString()))
                    emailEditText.setError("Invalid email");
                loginButton.setEnabled(updateBut());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!RegisterViewModel.isUserNameValid(emailEditText.getText().toString())) {
                    emailEditText.setError("Invalid email");
                    loginButton.setEnabled(false);
                    return;
                }
                loginButton.setEnabled(updateBut());
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(updateBut());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setEnabled(updateBut());
            }
        });
        //if enter is clicked after password, attempt login
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            toggleLoading(loadingFrame, true);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        //attempt login
        loginButton.setOnClickListener(v -> {
            //toggle loading screen
            toggleLoading(loadingFrame, true);

            //gets the username and password strings
            String user = emailEditText.getText().toString(), pass = passwordEditText.getText().toString();
            //wait a bit for loading to toggle, and then attempt login
            new Handler().postDelayed(() -> loginViewModel.login(user, pass), 200);

            //reset password value field to ""
            passwordEditText.setText("");

        });

        //if text field "No account" clicked
        noAccount.setOnClickListener(v -> {
            //open register activity
            Intent openRegister = new Intent(this, RegisterActivity.class);
            startActivity(openRegister);
        });
    }

    //Changes orientation
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //when activity restarts, log out
    @Override
    protected void onRestart() {
        super.onRestart();
        LoginRepository.logout();
    }

    //when activity resumes (from back button), log out and toggle loading screen to false
    @Override
    protected void onResume() {
        super.onResume();
        LoginRepository.logout();
        toggleLoading(findViewById(R.id.loadingScreenLogin), false);
    }

    //when success user is returned from login attempt
    private void updateUiWithUser(@NonNull scoutPerson p) {
        //open respective page
        if (p.isSM()) openSMApp();
        else openSApp();

        //toast a welcome message
        String welcome = (getString(R.string.welcome) + " " + p.getFName() + " " + p.getLName() + "!");
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    //opens scout activity
    private void openSApp() {
        Intent open = new Intent(this, scoutPage.class);
        startActivity(open);
    }

    //opens scoutmaster activity
    private void openSMApp() {
        Intent open = new Intent(this, scoutMasterPage.class);
        startActivity(open);
    }

    //display error that occurred and reset password text box
    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        EditText pass = findViewById(R.id.password);
        pass.setText("");

    }

    //if email and password fields are not blank, update the submit button
    private boolean updateBut() {
        EditText email = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        return !(email.getText().toString().equals("") || pass.getText().toString().equals(""));
    }

    //toggles loading screen
    private static void toggleLoading(FrameLayout f, boolean tog) {
        if (tog) {
            f.setVisibility(View.VISIBLE);
        } else {
            f.setVisibility(View.GONE);
        }
    }
}