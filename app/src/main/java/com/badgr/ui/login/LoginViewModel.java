package com.badgr.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.badgr.data.LoginRepository;
import com.badgr.data.Result;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        //Links to loginRepository, tries to log in user
        Result result = loginRepository.login(username, password);

        //if the result is a not a success, set error message with the return from result, that will display to screen
        if (!(result instanceof Result.Success)) {
            loginResult.setValue(new LoginResult(result.toString()));
        }
        else loginResult.setValue(new LoginResult(LoginRepository.getUser()));

    }
}