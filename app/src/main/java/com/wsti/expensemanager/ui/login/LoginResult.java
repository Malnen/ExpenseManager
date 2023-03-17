package com.wsti.expensemanager.ui.login;

class LoginResult {
    private LoggedInUserView success;
    private Integer error;

    LoginResult(Integer error) {
        this.error = error;
    }

    LoginResult(LoggedInUserView success) {
        this.success = success;
    }

    LoggedInUserView getSuccess() {
        return success;
    }


    Integer getError() {
        return error;
    }
}