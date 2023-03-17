package com.wsti.expensemanager.ui.register;

class RegisterFormState {
    private final Integer usernameError;
    private final Integer emailError;
    private final Integer passwordError;
    private final boolean isDataValid;

    RegisterFormState(Integer usernameError, Integer passwordError, Integer emailError) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
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

    public Integer getEmailError() {
        return emailError;
    }
}