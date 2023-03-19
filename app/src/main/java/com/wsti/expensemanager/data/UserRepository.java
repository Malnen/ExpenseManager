package com.wsti.expensemanager.data;

import com.wsti.expensemanager.data.model.User;

public class UserRepository {

    private static volatile UserRepository instance;

    private final UserDataSource dataSource;

    private User user = null;

    private UserRepository(UserDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static UserRepository getInstance(UserDataSource dataSource) {
        if (instance == null) {
            instance = new UserRepository(dataSource);
        }

        return instance;
    }

    public static UserRepository getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    public Result<User> tryToLoginOnRememberedUser(){
        Result<User> userResult = dataSource.tryToLoginOnRememberedUser();
        if(userResult instanceof Result.Success){
            User data = ((Result.Success<User>) userResult).getData();
            setLoggedInUser(data);
        }

        return userResult;
    }

    public Result<User> login(String username, String password) {
        Result<User> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            onLoginSuccess((Result.Success<User>) result);
        }

        return result;
    }

    public Result<User> register(String username, String password, String email) {
        Result<User> result = dataSource.register(username, password, email);
        if (result instanceof Result.Success) {
            onLoginSuccess((Result.Success<User>) result);
        }

        return result;
    }

    private void onLoginSuccess(Result.Success<User> result) {
        User data = result.getData();
        setLoggedInUser(data);
    }

    private void setLoggedInUser(User user) {
        this.user = user;
    }
}