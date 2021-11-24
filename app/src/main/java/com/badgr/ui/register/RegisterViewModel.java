package com.badgr.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.badgr.data.LoginRepository;
import com.badgr.data.Result;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.R;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<RegisterFormState> registerFormState;
    private final MutableLiveData<RegisterResult> registerResult;
    private Object RegisterActivity;


    public RegisterViewModel() {

        registerFormState = new MutableLiveData<>();
        registerResult = new MutableLiveData<>();
    }


    LiveData<RegisterFormState> getregisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getregisterResult() {
        return registerResult;
    }


    public boolean registerDataChanged(String user, String pass, String fName, String lName, String age, String troop) {
        if (!isUserNameValid(user)) {
            //registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null));
            return false;
        } else if (!isPasswordValid(pass)) {
            //registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password));
            return false;
        }
        return true;
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
        return password.equalsIgnoreCase("test");
    }

    //TODO Lots more checks
}