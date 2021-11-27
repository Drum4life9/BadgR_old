package com.badgr.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.badgr.data.LoginRepository;
import com.badgr.data.Result;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.R;

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
        //Links to loginRepository, tries to log in user
        Result<scoutPerson> result = loginRepository.login(username, password);


        if (!(result instanceof Result.Success)) {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    //is called when text is edited on the login screen
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    //checks to see if username is valid
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

}