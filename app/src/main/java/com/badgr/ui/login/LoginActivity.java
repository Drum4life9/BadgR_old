package com.badgr.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final FrameLayout loadingFrame = findViewById(R.id.loadingScreenLogin);
        final TextView noAccount = findViewById(R.id.noAccount);

        toggleLoading(loadingFrame, false);

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            toggleLoading(loadingFrame, true);
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
                toggleLoading(loadingFrame, false);
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);
        });

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
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            toggleLoading(loadingFrame, true);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            toggleLoading(loadingFrame, true);

            String user = emailEditText.getText().toString(), pass = passwordEditText.getText().toString();
            new Handler().postDelayed(() -> loginViewModel.login(user, pass), 200);

            passwordEditText.setText("");

        });

        noAccount.setOnClickListener(v -> {
            Intent openRegister = new Intent(this, RegisterActivity.class);
            startActivity(openRegister);
        });
    }

    //Changes orientation
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        LoginRepository.logout();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LoginRepository.logout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginRepository.logout();
        toggleLoading(findViewById(R.id.loadingScreenLogin), false);
    }

    private void updateUiWithUser(@NonNull scoutPerson p) {
        String welcome = (getString(R.string.welcome) + " " + p.getFName() + " " + p.getLName() + "!");

        if (p.isSM()) openSMApp();
        else openSApp();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void openSApp() {
        Intent open = new Intent(this, scoutPage.class);
        startActivity(open);
    }


    private void openSMApp() {
        Intent open = new Intent(this, scoutMasterPage.class);
        startActivity(open);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        EditText pass = findViewById(R.id.password);
        pass.setText("");

    }

    private boolean updateBut() {
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        return !(user.getText().toString().equals("") || pass.getText().toString().equals(""));
    }

    private static void toggleLoading(FrameLayout f, boolean tog) {
        if (tog) {
            f.setVisibility(View.VISIBLE);
        } else {
            f.setVisibility(View.GONE);
        }
    }
}