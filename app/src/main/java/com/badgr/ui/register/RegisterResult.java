package com.badgr.ui.register;

import androidx.annotation.Nullable;
import com.badgr.scoutClasses.scoutPerson;

/**
 * Authentication result : success (user details) or error message.
 */
class RegisterResult {
    @Nullable
    private scoutPerson success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable scoutPerson success) {
        this.success = success;
    }

    @Nullable
    scoutPerson getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}