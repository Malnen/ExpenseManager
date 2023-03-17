package com.wsti.expensemanager.ui.login;

class LoginFormState {
    private final Integer usernameError;
    private final Integer passwordError;
    private final boolean isDataValid;

    LoginFormState(Integer usernameError, Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    Integer getUsernameError() {
        return usernameError;
    }

    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}