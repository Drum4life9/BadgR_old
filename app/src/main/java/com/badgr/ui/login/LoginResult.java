package com.badgr.ui.login;

import androidx.annotation.Nullable;
import com.badgr.scoutClasses.scoutPerson;

/**
 * Authentication result : success (scoutPerson) or error message.
 */
public class LoginResult {
    @Nullable
    private scoutPerson success;
    @Nullable
    private String error;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable scoutPerson success) {
        this.success = success;
    }

    @Nullable
    public scoutPerson getSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }
}