package com.wsti.expensemanager.data.model;

import java.util.Objects;

public class User {

    private final String userId;
    private final String login;
    private final String email;

    public User(String userId, String login, String email) {
        this.userId = userId;
        this.login = login;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(userId, other.userId)
                && Objects.equals(login, other.login)
                && Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, login, email);
    }
}