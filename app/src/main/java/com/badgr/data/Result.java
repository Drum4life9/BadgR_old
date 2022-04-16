package com.badgr.data;

import androidx.annotation.NonNull;

import com.badgr.scoutClasses.scoutPerson;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    @NonNull
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success<scoutPerson> success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return error.getError();
        }
        return "";
    }

    // Success sub-class
    public final static class Success<scoutPerson> extends Result {
        private final scoutPerson data;

        public Success(scoutPerson data) {
            this.data = data;
        }

        public scoutPerson getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private final String error;

        public Error(String error) {
            this.error = error;
        }

        public String getError() {
            return this.error;
        }
    }
}