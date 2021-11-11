package com.badgr.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.badgr.data.LoginRepository;
import com.badgr.data.Result;
import com.badgr.data.model.LoggedInUser;
import com.badgr.R;
import com.badgr.scoutClasses.scoutMaster;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<scoutMaster> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            scoutMaster data = ((Result.Success<scoutMaster>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getFName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    //TODO username stuff
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    //TODO password stuff
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}