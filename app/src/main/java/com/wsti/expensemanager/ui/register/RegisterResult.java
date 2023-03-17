package com.wsti.expensemanager.ui.register;

public class RegisterResult {
    private RegisteredUserView success;
    private String error;

    RegisterResult(String error) {
        this.error = error;
    }

    RegisterResult(RegisteredUserView success) {
        this.success = success;
    }

    RegisteredUserView getSuccess() {
        return success;
    }


    String getError() {
        return error;
    }
}
