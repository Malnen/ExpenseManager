package com.wsti.expensemanager.ui.login;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wsti.expensemanager.data.Result;
import com.wsti.expensemanager.data.UserDataSource;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.model.User;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public LoginViewModelFactory(Context context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(UserRepository.getInstance(new UserDataSource(context)));
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public Result<User> tryToLoginOnRememberedUser() {
        UserRepository repository = UserRepository.getInstance(new UserDataSource(context));
        return repository.tryToLoginOnRememberedUser();
    }
}