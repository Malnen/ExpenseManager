package com.wsti.expensemanager.ui.register;

class RegisteredUserView {
    private final String displayName;

    RegisteredUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}